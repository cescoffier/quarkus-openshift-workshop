package io.quarkus.workshop.fightui;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
class ConfigTest {

    @Test
    void checkConfig() {
        given()
                .when()
                .get("/config/endpoints")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("heroes", is("https://foo.bar/heroes"))
                .body("villains", is("https://foo.bar/villains"))
                .body("fight", is("https://foo.bar/fight"));
    }
}