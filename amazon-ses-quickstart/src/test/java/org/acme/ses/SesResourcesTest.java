package org.acme.ses;

import static io.restassured.RestAssured.given;
import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.acme.ses.SesResource.FROM_EMAIL;
import static org.acme.ses.SesResource.TO_EMAIL;
import static org.hamcrest.Matchers.any;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import javax.ws.rs.core.Response.Status;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@QuarkusTest
@QuarkusTestResource(SesResource.class)
public class SesResourcesTest {

    private static final String JSON = "{\"from\":\"%s\", \"to\":\"%s\", \"subject\":\"%s\", \"body\":\"%s\"}";

    @ParameterizedTest
    @ValueSource(strings = {"sync", "async"})
    void testResource(final String testedResource) {

        //Send email
        given()
            .pathParam("resource", testedResource)
            .header(CONTENT_TYPE, APPLICATION_JSON)
            .body(String.format(JSON,
                    FROM_EMAIL,
                    TO_EMAIL, "Hello from Quarkus", "Quarkus is awsome"))
            .when()
            .post("/{resource}/email")
            .then()
            .statusCode(Status.OK.getStatusCode())
            .body(any(String.class));
    }
}
