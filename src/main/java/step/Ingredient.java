package step;

import io.qameta.allure.Step;
import io.restassured.response.Response;

import static step.Endpoints.baseRqSpec;
import static step.Endpoints.INGREDIENTS_PATH;
import static io.restassured.RestAssured.given;

public class Ingredient {
    @Step("Получение данных об ингредиентах")
    public Response getIngredients() {
        return given()
                .spec(baseRqSpec)
                .get(INGREDIENTS_PATH);
    }
}