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
//@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class RecipeControllerTest extends AbstractIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private RecipeRepository recipeRepository;

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost:" + port;
        recipeRepository.deleteAll();
    }

    @Test
    void testCreateRecipeWithInvalidData() {
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
    void testCreateRecipeWithValidData() {
        String validJson = """
            {
                "origin": "SALARIO",
                "value": 100.00,
                "destination": "Banco BAI",
                "receivedDate": "2025-07-19"
            }
            """;

        given()
                .contentType(ContentType.JSON)
                .body(validJson)
                .when()
                .post("/api/v1/recipes")
                .then()
                .statusCode(HttpStatus.CREATED.value());
    }

    @Test
    void testCreateRecipeWithFutureDate() {
        String futureDateJson = """
            {
                "origin": "SALARIO",
                "value": 100.00,
                "destination": "Banco BAI",
                "receivedDate": "2025-08-22"
            }
            """;

        given()
                .contentType(ContentType.JSON)
                .body(futureDateJson)
                .when()
                .post("/api/v1/recipes")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("message", equalTo("Validation failed"))
                .body("fieldErrors.receivedDate", equalTo("Received date must be in the past or present"));
    }

    @Test
    void testGetAllRecipesWithPagination() throws Exception {
        // Arrange: Create test data
        Recipe recipe1 = new Recipe();
        recipe1.setOrigin(RecipeOrigin.SALARIO);
        recipe1.setValue(new BigDecimal("100.00"));
        recipe1.setDestination("Banco BAI");
        recipe1.setReceivedDate(LocalDate.of(2025, 7, 19));
        recipeRepository.save(recipe1);

        Recipe recipe2 = new Recipe();
        recipe2.setOrigin(RecipeOrigin.BONUS);
        recipe2.setValue(new BigDecimal("200.00"));
        recipe2.setDestination("Banco BIC");
        recipe2.setReceivedDate(LocalDate.of(2025, 7, 18));
        recipeRepository.save(recipe2);

        // Act & Assert
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
    void testGetAllRecipesWithFilters() throws Exception {
        // Arrange: Create test data
        Recipe recipe1 = new Recipe();
        recipe1.setOrigin(RecipeOrigin.SALARIO);
        recipe1.setValue(new BigDecimal("100.00"));
        recipe1.setDestination("Banco BAI");
        recipe1.setReceivedDate(LocalDate.of(2025, 7, 19));
        recipeRepository.save(recipe1);

        Recipe recipe2 = new Recipe();
        recipe2.setOrigin(RecipeOrigin.APOSENTADORIA);
        recipe2.setValue(new BigDecimal("200.00"));
        recipe2.setDestination("Banco BIC");
        recipe2.setReceivedDate(LocalDate.of(2025, 7, 18));
        recipeRepository.save(recipe2);

        // Act & Assert: Filter by origin and destination
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
    void testGetAllRecipesWithDateRangeFilter() throws Exception {
        // Arrange: Create test data
        Recipe recipe1 = new Recipe();
        recipe1.setOrigin(RecipeOrigin.SALARIO);
        recipe1.setValue(new BigDecimal("100.00"));
        recipe1.setDestination("Banco BAI");
        recipe1.setReceivedDate(LocalDate.of(2025, 7, 19));
        recipeRepository.save(recipe1);

        Recipe recipe2 = new Recipe();
        recipe2.setOrigin(RecipeOrigin.SALARIO);
        recipe2.setValue(new BigDecimal("200.00"));
        recipe2.setDestination("Banco BIC");
        recipe2.setReceivedDate(LocalDate.of(2025, 6, 18));
        recipeRepository.save(recipe2);

        // Act & Assert: Filter by receivedDate range
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
//
//    @Disabled("Desativado, falta aplicar tratamento de exception no controller")
//    @Test
//    void testGetAllRecipesWithInvalidOrigin() {
//        // Act & Assert: Invalid origin filter
//        given()
//                .contentType(ContentType.JSON)
//                .queryParam("origin", "INVALID_ORIGIN")
//                .queryParam("page", 0)
//                .queryParam("size", 10)
//                .when()
//                .get("/api/v1/recipes")
//                .then()
//                .statusCode(HttpStatus.BAD_REQUEST.value())
//                .body("message", equalTo("Invalid origin value"));
//    }

    @Test
    void testGetRecipeByIdSuccess() {
        // Arrange: Create test data
        Recipe recipe = new Recipe();
        recipe.setOrigin(RecipeOrigin.SALARIO);
        recipe.setValue(new BigDecimal("100.00"));
        recipe.setDestination("Banco BAI");
        recipe.setReceivedDate(LocalDate.of(2025, 7, 19));
        recipe = recipeRepository.save(recipe);

        // Act & Assert
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
    void testGetRecipeByIdNotFound() {
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/v1/recipes/999")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("message",equalTo("Recipe not found with ID: 999"));
    }
    @Test
    void testGetRecipeByIdInvalidId() {
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/v1/recipes/invalid")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void testUpdateRecipeWithValidIdAndData() {
        // Arrange: Criar uma receita no banco
        Recipe recipe = new Recipe();
        recipe.setOrigin(RecipeOrigin.SALARIO);
        recipe.setValue(new BigDecimal("1000.00"));
        recipe.setDestination("Banco BAI");
        recipe.setReceivedDate(LocalDate.of(2025, 7, 18));
        recipe.setMonth(7);
        recipe.setYear(2025);
        recipe.setCreatedAt(LocalDate.of(2025, 7, 18));
        recipe.setUpdatedAt(LocalDate.of(2025, 7, 18));
        recipe = recipeRepository.save(recipe);

        String validJson = """
            {
                "origin": "SALARIO",
                "value": 5000.00,
                "destination": "Banco BIC",
                "receivedDate": "2025-07-19"
            }
            """;

        // Act & Assert
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
                .body("year", equalTo(2025))
                .body("createdAt", equalTo(LocalDate.now().toString()))
                .body("updatedAt", equalTo(LocalDate.now().toString()));
    }
    @Test
    void testUpdateRecipeWithInvalidId() {
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
                .put("/api/v1/recipes/999")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("message", equalTo("Recipe not found with ID: 999"));
    }

    @Test
    void testUpdateRecipeWithInvalidData() {
        // Arrange: Criar uma receita no banco
        Recipe recipe = new Recipe();
        recipe.setOrigin(RecipeOrigin.SALARIO);
        recipe.setValue(new BigDecimal("1000.00"));
        recipe.setDestination("Banco BAI");
        recipe.setReceivedDate(LocalDate.of(2025, 7, 18));
        recipe = recipeRepository.save(recipe);

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
    void testUpdateRecipeWithFutureDate() {
        // Arrange: Criar uma receita no banco
        Recipe recipe = new Recipe();
        recipe.setOrigin(RecipeOrigin.SALARIO);
        recipe.setValue(new BigDecimal("1000.00"));
        recipe.setDestination("Banco BAI");
        recipe.setReceivedDate(LocalDate.of(2025, 7, 18));
        recipe = recipeRepository.save(recipe);

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
    void testUpdateRecipeWithInvalidIdFormat() {
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
                .put("/api/v1/recipes/invalid")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

}
