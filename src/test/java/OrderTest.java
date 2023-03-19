import io.qameta.allure.Epic;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import step.Ingredient;
import step.Order;
import step.User;

import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.*;
import static step.UserCreate.generateUserData;

@Epic("Stellar Burgers")

@DisplayName("Получение заказов")
public class OrderTest {
    User user;
    Order order;
    Ingredient ingredient;
    String accessToken;

    private String createAndLoginUser() {
        Map<String, String> data = generateUserData();
        Response response = user.createUser(data.get("email"), data.get("password"), data.get("username"));
        user.loginUser(data.get("email"), data.get("password"));
        return response.path("accessToken");
    }

    @Before
    public void setUp() {
        user = new User();
        order = new Order();
        ingredient = new Ingredient();
        accessToken = createAndLoginUser();
        Response responseIngredients = ingredient.getIngredients();
        List<String> ingredients = responseIngredients.path("data._id");
        order.createOrder(ingredients, accessToken);
    }

    @After
    public void cleanUp() {
        user.deleteUser(accessToken);
    }

    @Test
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("Получение списка заказов авторизованным пользователем")
    public void successfullyReceiveOrderListTest() {
        Response response = order.getOrders(accessToken);
        assertEquals("Неверный код ответа", 200, response.statusCode());
        assertTrue("Невалидные данные в ответе: success", response.path("success"));
        assertThat("Заказа не существует", response.path("orders"), notNullValue());
    }

    @Test
    @DisplayName("Получение списка заказ без авторизации")
    public void receiveOrderListWithoutAuthorizationTest() {
        Response response = order.getOrders("");
        assertEquals("Неверный код ответа", 401, response.statusCode());
        assertFalse("Невалидные данные в ответе: success", response.path("success"));
        assertEquals("Невалидные данные в ответе: message", "You should be authorised", response.path("message"));
    }
}
