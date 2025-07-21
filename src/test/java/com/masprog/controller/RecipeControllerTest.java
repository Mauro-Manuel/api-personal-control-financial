package com.masprog.controller;

import com.masprog.integration.AbstractIntegrationTest;
import com.masprog.model.Recipe;
import com.masprog.model.RecipeOrigin;
import com.masprog.repository.RecipeRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RecipeControllerTest extends AbstractIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private RecipeRepository recipeRepository;

    private static final BigDecimal VALID_VALUE = new BigDecimal("100.00");
    private static final LocalDate VALID_DATE = LocalDate.of(2025, 7, 19);

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost:" + port;
        recipeRepository.deleteAll();
    }

    private Recipe createRecipe(RecipeOrigin origin, BigDecimal value, String destination, LocalDate date) {
        Recipe recipe = new Recipe();
        recipe.setOrigin(origin);
        recipe.setValue(value);
        recipe.setDestination(destination);
        recipe.setReceivedDate(date);
        return recipeRepository.save(recipe);
    }

    @Nested
    class CreateRecipeTests {

        @Test
        void shouldCreateRecipeWithValidData() {
            String json = """
                {
                    "origin": "SALARIO",
                    "value": 100.00,
                    "destination": "Banco BAI",
                    "receivedDate": "2025-07-19"
                }
                """;

            given()
                    .contentType(ContentType.JSON)
                    .body(json)
                    .when()
                    .post("/api/v1/recipes")
                    .then()
                    .statusCode(HttpStatus.CREATED.value());
        }

        @Test
        void shouldFailToCreateRecipeWithInvalidData() {
            String invalidJson = """
                {
                    "origin": null,
                    "value": -10,
                    "destination": "",
                    "receivedDate": null
                }
                """;

            given()
                    .contentType(ContentType.JSON)
                    .body(invalidJson)
                    .when()
                    .post("/api/v1/recipes")
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("message", equalTo("Validation failed"))
                    .body("fieldErrors.origin", equalTo("Origin is required"))
                    .body("fieldErrors.value", equalTo("Value must be positive"))
                    .body("fieldErrors.destination", equalTo("Destination is required"))
                    .body("fieldErrors.receivedDate", equalTo("Received date is required"));
        }

        @Test
        void shouldFailToCreateRecipeWithFutureDate() {
            String json = """
                {
                    "origin": "SALARIO",
                    "value": 100.00,
                    "destination": "Banco BAI",
                    "receivedDate": "2025-08-22"
                }
                """;

            given()
                    .contentType(ContentType.JSON)
                    .body(json)
                    .when()
                    .post("/api/v1/recipes")
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("message", equalTo("Validation failed"))
                    .body("fieldErrors.receivedDate", equalTo("Received date must be in the past or present"));
        }
    }

    @Nested
    class GetRecipeTests {

        @Test
        void shouldGetRecipeById() {
            Recipe recipe = createRecipe(RecipeOrigin.SALARIO, VALID_VALUE, "Banco BAI", VALID_DATE);

            given()
                    .contentType(ContentType.JSON)
                    .when()
                    .get("/api/v1/recipes/" + recipe.getId())
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("id", equalTo(recipe.getId().intValue()))
                    .body("origin", equalTo("SALARIO"))
                    .body("value", equalTo(100.00f))
                    .body("destination", equalTo("Banco BAI"))
                    .body("receivedDate", equalTo("2025-07-19"));
        }

        @Test
        void shouldReturn404ForNonExistingRecipe() {
            given()
                    .contentType(ContentType.JSON)
                    .when()
                    .get("/api/v1/recipes/999")
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .body("message", equalTo("Recipe not found with ID: 999"));
        }

        @Test
        void shouldReturn400ForInvalidIdFormat() {
            given()
                    .contentType(ContentType.JSON)
                    .when()
                    .get("/api/v1/recipes/invalid")
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }
    }

    @Nested
    class GetAllRecipesTests {

        @Test
        void shouldReturnPaginatedRecipes() {
            createRecipe(RecipeOrigin.SALARIO, VALID_VALUE, "Banco BAI", VALID_DATE);
            createRecipe(RecipeOrigin.BONUS, new BigDecimal("200.00"), "Banco BIC", LocalDate.of(2025, 7, 18));

            given()
                    .contentType(ContentType.JSON)
                    .queryParam("page", 0)
                    .queryParam("size", 1)
                    .when()
                    .get("/api/v1/recipes")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("content", hasSize(1))
                    .body("totalElements", equalTo(2))
                    .body("totalPages", equalTo(2))
                    .body("size", equalTo(1))
                    .body("number", equalTo(0));
        }

        @Test
        void shouldFilterRecipesByOriginAndDestination() {
            createRecipe(RecipeOrigin.SALARIO, VALID_VALUE, "Banco BAI", VALID_DATE);
            createRecipe(RecipeOrigin.APOSENTADORIA, new BigDecimal("200.00"), "Banco BIC", LocalDate.of(2025, 7, 18));

            given()
                    .contentType(ContentType.JSON)
                    .queryParam("origin", "SALARIO")
                    .queryParam("destination", "Banco BAI")
                    .queryParam("page", 0)
                    .queryParam("size", 10)
                    .when()
                    .get("/api/v1/recipes")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("content", hasSize(1))
                    .body("content[0].origin", equalTo("SALARIO"))
                    .body("content[0].destination", equalTo("Banco BAI"))
                    .body("totalElements", equalTo(1));
        }

        @Test
        void shouldFilterRecipesByDateRange() {
            createRecipe(RecipeOrigin.SALARIO, VALID_VALUE, "Banco BAI", VALID_DATE);
            createRecipe(RecipeOrigin.SALARIO, new BigDecimal("200.00"), "Banco BIC", LocalDate.of(2025, 6, 18));

            given()
                    .contentType(ContentType.JSON)
                    .queryParam("receivedDateFrom", "2025-07-01")
                    .queryParam("receivedDateTo", "2025-07-31")
                    .queryParam("page", 0)
                    .queryParam("size", 10)
                    .when()
                    .get("/api/v1/recipes")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("content", hasSize(1))
                    .body("content[0].receivedDate", equalTo("2025-07-19"))
                    .body("totalElements", equalTo(1));
        }
    }

    @Nested
    class UpdateRecipeTests {

        @Test
        void shouldUpdateRecipeSuccessfully() {
            Recipe recipe = createRecipe(RecipeOrigin.SALARIO, new BigDecimal("1000.00"), "Banco BAI", LocalDate.of(2025, 7, 18));

            String validJson = """
                {
                    "origin": "SALARIO",
                    "value": 5000.00,
                    "destination": "Banco BIC",
                    "receivedDate": "2025-07-19"
                }
                """;

            given()
                    .contentType(ContentType.JSON)
                    .body(validJson)
                    .when()
                    .put("/api/v1/recipes/" + recipe.getId())
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("id", equalTo(recipe.getId().intValue()))
                    .body("origin", equalTo("SALARIO"))
                    .body("value", equalTo(5000.00f))
                    .body("destination", equalTo("Banco BIC"))
                    .body("receivedDate", equalTo("2025-07-19"))
                    .body("month", equalTo(7))
                    .body("year", equalTo(2025));
        }

        @Test
        void shouldReturn404WhenUpdatingNonExistingRecipe() {
            String json = """
                {
                    "origin": "SALARIO",
                    "value": 5000.00,
                    "destination": "Banco BIC",
                    "receivedDate": "2025-07-19"
                }
                """;

            given()
                    .contentType(ContentType.JSON)
                    .body(json)
                    .when()
                    .put("/api/v1/recipes/999")
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .body("message", equalTo("Recipe not found with ID: 999"));
        }

        @Test
        void shouldFailToUpdateWithInvalidData() {
            Recipe recipe = createRecipe(RecipeOrigin.SALARIO, new BigDecimal("1000.00"), "Banco BAI", LocalDate.of(2025, 7, 18));

            String invalidJson = """
                {
                    "origin": null,
                    "value": -10,
                    "destination": "",
                    "receivedDate": null
                }
                """;

            given()
                    .contentType(ContentType.JSON)
                    .body(invalidJson)
                    .when()
                    .put("/api/v1/recipes/" + recipe.getId())
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("message", equalTo("Validation failed"))
                    .body("fieldErrors.origin", equalTo("Origin is required"))
                    .body("fieldErrors.value", equalTo("Value must be positive"))
                    .body("fieldErrors.destination", equalTo("Destination is required"))
                    .body("fieldErrors.receivedDate", equalTo("Received date is required"));
        }

        @Test
        void shouldFailToUpdateWithFutureDate() {
            Recipe recipe = createRecipe(RecipeOrigin.SALARIO, new BigDecimal("1000.00"), "Banco BAI", LocalDate.of(2025, 7, 18));

            String futureDateJson = """
                {
                    "origin": "SALARIO",
                    "value": 5000.00,
                    "destination": "Banco BIC",
                    "receivedDate": "2025-08-22"
                }
                """;

            given()
                    .contentType(ContentType.JSON)
                    .body(futureDateJson)
                    .when()
                    .put("/api/v1/recipes/" + recipe.getId())
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("message", equalTo("Validation failed"))
                    .body("fieldErrors.receivedDate", equalTo("Received date must be in the past or present"));
        }

        @Test
        void shouldReturn400WhenUpdatingWithInvalidIdFormat() {
            String json = """
                {
                    "origin": "SALARIO",
                    "value": 5000.00,
                    "destination": "Banco BIC",
                    "receivedDate": "2025-07-19"
                }
                """;

            given()
                    .contentType(ContentType.JSON)
                    .body(json)
                    .when()
                    .put("/api/v1/recipes/invalid")
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }
    }
}
