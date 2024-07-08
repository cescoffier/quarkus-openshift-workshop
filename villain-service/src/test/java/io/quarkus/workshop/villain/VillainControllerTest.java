package io.quarkus.workshop.villain;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.common.mapper.TypeRef;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.List;
import java.util.Random;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.HttpHeaders.ACCEPT;
import static jakarta.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.Response.Status.BAD_REQUEST;
import static jakarta.ws.rs.core.Response.Status.CREATED;
import static jakarta.ws.rs.core.Response.Status.NOT_FOUND;
import static jakarta.ws.rs.core.Response.Status.NO_CONTENT;
import static jakarta.ws.rs.core.Response.Status.OK;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class VillainControllerTest {

    private static final String DEFAULT_NAME = "Super Chocolatine";
    private static final String UPDATED_NAME = "Super Chocolatine (updated)";
    private static final String DEFAULT_OTHER_NAME = "Super Chocolatine chocolate in";
    private static final String UPDATED_OTHER_NAME = "Super Chocolatine chocolate in (updated)";
    private static final String DEFAULT_PICTURE = "super_chocolatine.png";
    private static final String UPDATED_PICTURE = "super_chocolatine_updated.png";
    private static final String DEFAULT_POWERS = "does not eat pain au chocolat";
    private static final String UPDATED_POWERS = "does not eat pain au chocolat (updated)";
    private static final int DEFAULT_LEVEL = 42;
    private static final int UPDATED_LEVEL = 43;

    private static final int NB_VILLAINS = 570;
    private static String villainId;


    @Test
    public void testHelloEndpoint() {
        given()
                .when()
                .get("/api/villains/hello")
                .then()
                .statusCode(200)
                .body(is("Hello Villain Service"));
    }

    @Test
    void shouldNotGetUnknownVillain() {
        Long randomId = new Random().nextLong();
        given()
                .pathParam("id", randomId)
                .when()
                .get("/api/villains/{id}")
                .then()
                .statusCode(NOT_FOUND.getStatusCode());
    }

    @Test
    void shouldGetRandomVillain() {
        given()
                .when()
                .get("/api/villains/random")
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON);
    }

    @Test
    void shouldNotAddInvalidItem() {
        Villain villain = new Villain();
        villain.setName(null);
        villain.setOtherName(DEFAULT_OTHER_NAME);
        villain.setPicture(DEFAULT_PICTURE);
        villain.setPowers(DEFAULT_POWERS);
        villain.setLevel(1);

        given()
                .body(villain)
                .header(CONTENT_TYPE, APPLICATION_JSON)
                .header(ACCEPT, APPLICATION_JSON)
                .when()
                .post("/api/villains")
                .then()
                .statusCode(BAD_REQUEST.getStatusCode());
    }

    @Test
    @Order(1)
    void shouldGetInitialItems() {
        List<Villain> villains = get("/api/villains").then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .body()
                .as(getVillainTypeRef());
        assertEquals(NB_VILLAINS, villains.size());
    }

    @Test
    @Order(2)
    void shouldAddAnItem() {
        Villain villain = new Villain();
        villain.setName(DEFAULT_NAME);
        villain.setOtherName(DEFAULT_OTHER_NAME);
        villain.setPicture(DEFAULT_PICTURE);
        villain.setPowers(DEFAULT_POWERS);
        villain.setLevel(DEFAULT_LEVEL);

        String location = given()
                .body(villain)
                .header(CONTENT_TYPE, APPLICATION_JSON)
                .header(ACCEPT, APPLICATION_JSON)
                .when()
                .post("/api/villains")
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract()
                .header("Location");
        assertTrue(location.contains("/api/villains"));

        // Stores the id
        String[] segments = location.split("/");
        villainId = segments[segments.length - 1];
        assertNotNull(villainId);

        given()
                .pathParam("id", villainId)
                .when()
                .get("/api/villains/{id}")
                .then()
                .statusCode(OK.getStatusCode())
                .body("name", Is.is(DEFAULT_NAME))
                .body("otherName", Is.is(DEFAULT_OTHER_NAME))
                .body("level", Is.is(DEFAULT_LEVEL))
                .body("picture", Is.is(DEFAULT_PICTURE))
                .body("powers", Is.is(DEFAULT_POWERS));

        List<Villain> villains = get("/api/villains").then()
                .statusCode(OK.getStatusCode())
                .extract()
                .body()
                .as(getVillainTypeRef());
        assertEquals(NB_VILLAINS + 1, villains.size());
    }

    @Test
    @Order(3)
    void shouldUpdateAnItem() {
        Villain villain = new Villain();
        villain.setId(Long.valueOf(villainId));
        villain.setName(UPDATED_NAME);
        villain.setOtherName(UPDATED_OTHER_NAME);
        villain.setPicture(UPDATED_PICTURE);
        villain.setPowers(UPDATED_POWERS);
        villain.setLevel(UPDATED_LEVEL);

        given()
                .body(villain)
                .header(CONTENT_TYPE, APPLICATION_JSON)
                .header(ACCEPT, APPLICATION_JSON)
                .when()
                .put("/api/villains")
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .body("name", Is.is(UPDATED_NAME))
                .body("otherName", Is.is(UPDATED_OTHER_NAME))
                .body("level", Is.is(UPDATED_LEVEL))
                .body("picture", Is.is(UPDATED_PICTURE))
                .body("powers", Is.is(UPDATED_POWERS));

        List<Villain> villains = get("/api/villains").then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .body()
                .as(getVillainTypeRef());
        assertEquals(NB_VILLAINS + 1, villains.size());
    }

    @Test
    @Order(4)
    void shouldRemoveAnItem() {
        given()
                .pathParam("id", villainId)
                .when()
                .delete("/api/villains/{id}")
                .then()
                .statusCode(NO_CONTENT.getStatusCode());

        List<Villain> villains = get("/api/villains").then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .body()
                .as(getVillainTypeRef());
        assertEquals(NB_VILLAINS, villains.size());
    }

    private TypeRef<List<Villain>> getVillainTypeRef() {
        return new TypeRef<List<Villain>>() {
            // Kept empty on purpose
        };
    }

}