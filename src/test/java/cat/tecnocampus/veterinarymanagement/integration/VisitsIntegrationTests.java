package cat.tecnocampus.veterinarymanagement.integration;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Sql(scripts = "classpath:cleanup-test.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:data-test.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class VisitsIntegrationTests {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setup() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }

    @Test
    void getExistingVisit() {
        given()
                .when()
                .get("/visits/1")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("visit_id", equalTo(1))
                .body("reasonForVisit", equalTo("Annual checkup"))
                .body("status", equalTo("SCHEDULED"));
    }

    @Test
    void listAllVisits() {
        given()
                .when()
                .get("/visits")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("size()", is(3))
                .body("[0].visit_id", notNullValue());
    }

    @Test
    void createVisit() {
        String body = "{" +
                "\"visit_date\":\"2025-12-15\"," +
                "\"visit_time\":\"10:45\"," +
                "\"duration\":30," +
                "\"reasonForVisit\":\"Routine check\"," +
                "\"price_per_fifteen\":25.0," +
                "\"veterinarian_id\":1," +
                "\"pet_id\":1," +
                "\"pet_owner_id\":4" +
                "}";

        given()
                .contentType("application/json")
                .body(body)
                .when()
                .post("/visits")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .header("Location", matchesRegex(".*/visits/\\d+$"))
                .body("reasonForVisit", equalTo("Routine check"))
                .body("status", equalTo("SCHEDULED"));
    }
}
