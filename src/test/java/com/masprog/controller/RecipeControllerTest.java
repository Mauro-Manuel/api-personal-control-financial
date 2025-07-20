package com.masprog.controller;

import com.masprog.integration.AbstractIntegrationTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class RecipeControllerTest extends AbstractIntegrationTest {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost:" + port;
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
                "receivedDate": "2025-07-21"
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
}
