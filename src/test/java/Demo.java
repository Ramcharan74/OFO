import com.ram.foodapp.util.JsonUtil;

import java.io.IOException;

import static java.lang.Integer.parseInt;

public class Demo {
    public static void main(String[] args) throws IOException {
//        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
//        MenuItemService menuItemService = context.getBean(MenuItemService.class);
//
//
//        List<MenuItemResponse> list = menuItemService.findAll(new PageRequest(0, 10))
//                .stream()
//                .map(MenuItemMapper::toResponse)
//                .toList();
//        sendJson(HttpServletResponse.SC_OK,
//                ApiResponse.success("Menu items fetched", list));



    }

    private static void sendJson(int status, Object body) throws IOException {
        String json = JsonUtil.DEFAULT_MAPPER.writeValueAsString(body);
        System.out.println("Response JSON: " + json);
    }
}
