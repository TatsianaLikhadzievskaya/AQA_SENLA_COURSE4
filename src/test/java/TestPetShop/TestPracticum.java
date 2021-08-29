package TestPetShop;

import com.google.gson.JsonParser;
import endpoints.EndPointPetShop;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import models.User;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import utils.Log;
import utilsAPI.PetShopApiSpecification;

import java.io.File;

import static io.restassured.RestAssured.*;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchema;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestPracticum {

    private static RequestSpecification requestSpec = PetShopApiSpecification.getRequestSpecification();
    private static File jsonSchema = new File("src/test/resources/json/petJsonSchema.json");

    @Test
    @Order(1)
    public void createUserTest() {

       User user = new User(12, "petlover", "Kate", "Jackson", "petlover@gmail.com", "katty2000", "+375445965231", 2);

      Response response = given().spec(requestSpec)
                .when()
                .body(user)
                .post(EndPointPetShop.USER);
      response.then()
                .assertThat()
                .statusCode(200)
                .body(containsString("12"));

       String responseBody = response.getBody().asString();
       JsonParser parser = new JsonParser();
       String id = parser.parse(responseBody).getAsJsonObject().get("message").getAsString();
       Log.info("Created user's id: " + id);
    }

    @Test
    @Order(2)
    public void createWithListTest() {

        User user1 = new User(25, "catsfan", "John", "Smith", "catsfan@gmail.com", "johny1234", "+375445297260", 5);
        User user2 = new User(85, "dogsfan", "Jack", "Black", "dogsfan@gmail.com", "jackyblack", "+375440987660", 3);
        User[] users = {user1, user2};

        given().spec(requestSpec)
                .when()
                .body(users)
                .post(EndPointPetShop.CREATE_WITH_LIST)
                .then()
                .assertThat()
                .statusCode(200)
                .body(containsString("ok"));
    }

    @Test
    @Order(3)
    public void getUserByUserNameTest() {

            given().spec(requestSpec)
                    .when()
                    .get("/user/petlover")
                    .prettyPeek()
                    .then()
                    .body("lastName", equalTo("Jackson"))
                    .body(matchesJsonSchema(jsonSchema));
            // jsonSchema doesn't contain field 'firstName' because it's missing at the website's response
    }

    @Test
    @Order(4)
    public void deleteUserTest() {

        given().spec(requestSpec)
                .when()
                .delete("/user/dogsfan")
                .then()
                .statusCode(200)
                .body("message", equalTo("dogsfan"));
    }

    @Test
    @Order(5)
    public void updateUserTest() {

        // update password
        User user = new User(25, "catsfan", "John", "Smith", "catsfan@gmail.com", "johny_smith", "+375445297260", 5);

        given().spec(requestSpec)
                .when()
                .body(user)
                .put("/user/catsfan")
                .then()
                .assertThat()
                .statusCode(200)
                .body("message", equalTo("25"));
    }

}
