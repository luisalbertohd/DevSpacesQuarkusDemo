package org.acme.hibernate.search.elasticsearch.service;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.contains;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;

@QuarkusTest
public class LibraryResourceTest {

    @Test
    public void testLibrary() throws Exception {
        // Full text search
        RestAssured.when().get("/library/author/search?pattern=john").then()
                .statusCode(200)
                .body("firstName", contains("John"),
                        "lastName", contains("Irving"));

        RestAssured.when().get("/library/author/search?pattern=vertigo").then()
                .statusCode(200)
                .body("firstName", contains("Paul"),
                        "lastName", contains("Auster"));

        RestAssured.when().get("/library/author/search?pattern=mystery").then()
                .statusCode(200)
                .body("firstName", contains("John"),
                        "lastName", contains("Irving"));

        // Add an author
        RestAssured.given()
                .contentType(ContentType.URLENC.withCharset(StandardCharsets.UTF_8))
                .formParam("firstName", "David")
                .formParam("lastName", "Wrong")
                .put("/library/author/")
                .then()
                .statusCode(204);

        Integer davidLodgeId = RestAssured.when().get("/library/author/search?pattern=dav*").then()
                .statusCode(200)
                .body("firstName", contains("David"),
                        "lastName", contains("Wrong"))
                .extract().path("[0].id");

        // Update an author
        RestAssured.given()
                .contentType(ContentType.URLENC.withCharset(StandardCharsets.UTF_8))
                .formParam("firstName", "David")
                .formParam("lastName", "Lodge")
                .post("/library/author/" + davidLodgeId)
                .then()
                .statusCode(204);

        RestAssured.when().get("/library/author/search?pattern=dav*").then()
                .statusCode(200)
                .body("firstName", contains("David"),
                        "lastName", contains("Lodge"));

        // Add a book
        RestAssured.given()
                .contentType(ContentType.URLENC.withCharset(StandardCharsets.UTF_8))
                .formParam("title", "Therapy")
                .formParam("authorId", davidLodgeId)
                .put("/library/book/")
                .then()
                .statusCode(204);

        RestAssured.when().get("/library/author/search?pattern=therapy").then()
                .statusCode(200)
                .body("firstName", contains("David"),
                        "lastName", contains("Lodge"));
    }
}
