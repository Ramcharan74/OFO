import com.fasterxml.jackson.databind.ObjectMapper;
import com.ram.foodapp.config.AppConfig;
import com.ram.foodapp.controller.FoodOrderServlet;
import com.ram.foodapp.dto.request.PageRequest;
import com.ram.foodapp.dto.response.ApiResponse;
import com.ram.foodapp.dto.response.FoodOrderResponse;
import com.ram.foodapp.dto.response.MenuItemResponse;
import com.ram.foodapp.dto.response.UserResponse;
import com.ram.foodapp.mapper.FoodOrderMapper;
import com.ram.foodapp.mapper.MenuItemMapper;
import com.ram.foodapp.mapper.UserMapper;
import com.ram.foodapp.service.FoodOrderService;
import com.ram.foodapp.service.MenuItemService;
import com.ram.foodapp.service.UserService;
import com.ram.foodapp.util.JsonUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.IOException;
import java.util.List;

import static java.lang.Integer.parseInt;

public class Demo {
    public static void main(String[] args) throws IOException {
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        MenuItemService menuItemService = context.getBean(MenuItemService.class);


        List<MenuItemResponse> list = menuItemService.findAll(new PageRequest(0, 10))
                .stream()
                .map(MenuItemMapper::toResponse)
                .toList();
        sendJson(HttpServletResponse.SC_OK,
                ApiResponse.success("Menu items fetched", list));
    }

    private static void sendJson(int status, Object body) throws IOException {
        String json = JsonUtil.DEFAULT_MAPPER.writeValueAsString(body);
        System.out.println("Response JSON: " + json);
    }
}
