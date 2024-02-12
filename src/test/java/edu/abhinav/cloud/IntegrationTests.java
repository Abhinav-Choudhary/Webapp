package edu.abhinav.cloud;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import io.restassured.http.ContentType;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import edu.abhinav.cloud.pojo.AddUser;
// import edu.abhinav.cloud.pojo.User;

@SpringBootTest
public class IntegrationTests {

    @Test
    //Test 1 - Create an account, and using the GET call, validate account exists.
    void testCreateAccount() {
        String firstName = "Random fName";
        String lastName = "Random lName";
        String username = "random@random.com";
        String password = "random@123";

        AddUser newUser = new AddUser();
        newUser.setFirst_name(firstName);
        newUser.setLast_name(lastName);
        newUser.setUsername(username);
        newUser.setPassword(password);

       given()
       .port(8080)
       .contentType(ContentType.JSON)
       .body(newUser)
       .when()
       .post("/v1/user")
       .then()
       .statusCode(HttpStatus.CREATED.value());

        given()
        .port(8080)
        .auth().preemptive().basic(username, password)
        .when()
        .get("/v1/user/self")
        .then()
        .statusCode(HttpStatus.OK.value())
        .body(
            "username", is(newUser.getUsername()),
            "first_name", is(newUser.getFirst_name()),
            "last_name", is(newUser.getLast_name()),
            "id", notNullValue(),
            "account_created", notNullValue(),
            "account_updated", notNullValue()
       );
    }

    @Test
    //Test 2 - Update the account and using the GET call, validate the account was updated.
    void testAccountExists() {
        String firstName = "New First Name";
        String lastName = "New Last Name";
        String password = "random@123";
        String authUserName = "random@random.com";
        String authPassword = "random@123";

        AddUser updateUser = new AddUser();
        updateUser.setFirst_name(firstName);
        updateUser.setLast_name(lastName);
        updateUser.setPassword(password);

        given()
        .port(8080)
        .auth().preemptive().basic(authUserName, authPassword)
        .contentType(ContentType.JSON)
        .body(updateUser)
        .when()
        .put("/v1/user/self")
        .then()
        .statusCode(HttpStatus.NO_CONTENT.value());

        given()
        .port(8080)
        .auth().preemptive().basic(authUserName, authPassword)
        .when()
        .get("/v1/user/self")
        .then()
        .statusCode(HttpStatus.OK.value())
        .body(
            "username", is(authUserName),
            "first_name", is(updateUser.getFirst_name()),
            "last_name", is(updateUser.getLast_name()),
            "id", notNullValue(),
            "account_created", notNullValue(),
            "account_updated", notNullValue()
       );

    }
    
}
