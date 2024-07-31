package qa.ozhegov;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.Matchers.*;

public class CreateUsersTests extends TestBase {

    @ParameterizedTest(name = "При создании пользователя с именем {0} и работой {1} корректное имя и работа отображаются в ответе")
    @CsvSource({
            "Max Ozhegov, QA Engineer",
            "Sergey Brin,  CEO",
            "Vasya Pupkin, Plumber",
            "Filipp Kirkorov, Singer"}
    )
    void correctNameShouldBeVisibleAfterSuccessCreation(String name, String job) {

        String createData = "{\"name\":\"" + name + "\", \"job\":\"" + job + "\"}";

        given()

                .body(createData)
                .contentType(JSON)
                .log().body()

                .when()
                .post("/users")

                .then()
                .log().body()
                .body("name", is(name))
                .body("job", is(job));


    }

    @Test
    @DisplayName("В теле ответа на успешный запрос создания пользователя должно быть 4 параметра")
    void responseBodyShouldHaveFourParams() {

        String createData = "{\"name\": \"morpheus\",\"job\": \"leader\"}";

        given()

                .body(createData)
                .contentType(JSON)
                .log().body()

                .when()
                .post("/users")

                .then()
                .log().body()
                .body("size()", is(4));

    }

    @Test
    @DisplayName("В теле ответа на успешный запрос создания пользователя содержится текущая дата")
    void responseBodyShouldContainCurrentDay() {

        String createData = "{\"name\": \"morpheus\",\"job\": \"leader\"}";

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localDate = LocalDate.now();
        String currentDate = dtf.format(localDate);

        given()

                .body(createData)
                .contentType(JSON)
                .log().body()

                .when()
                .post("/users")

                .then()
                .log().body()
                .body("createdAt", containsString(currentDate));

    }

    @Test
    @DisplayName("При успешном создании пользователя приходит 201 статус-код")
    void code201ShouldBeVisibleInResponseAfterSuccessfulCreation() {

        String createData = "{\"name\": \"morpheus\",\"job\": \"leader\"}";

        given()

                .body(createData)
                .contentType(JSON)
                .log().body()

                .when()
                .post("/users")

                .then()
                .log().body()
                .statusCode(201);

    }

    @Test
    @DisplayName("При попытке созать пользователя без указания имени в ответе на зпрос приходит 400 статус-код")
    void code400ShouldBeVisibleInResponseWithoutName() {

        String createData = "{\"name\",\"job\": \"leader\"}";

        given()
                .body(createData)
                .contentType(JSON)
                .log().body()

                .when()
                .post("/users")

                .then()
                .log().body()
                .statusCode(400);

    }

    @Test
    @DisplayName("При попытке созать пользователя без указания работы в ответе на зпрос приходит 400 статус-код")
    void code400ShouldBeVisibleInResponseWithoutJob() {

        String createData = "{\"name\": \"morpheus\",\"job\"}";

        given()
                .body(createData)
                .contentType(JSON)
                .log().body()

                .when()
                .post("/users")

                .then()
                .log().body()
                .statusCode(400);

    }

}
