import com.ram.foodapp.config.AppConfig;
import com.ram.foodapp.dto.request.PageRequest;
import com.ram.foodapp.model.address.Address;
import com.ram.foodapp.model.restaurant.Restaurant;
import com.ram.foodapp.service.AddressService;
import com.ram.foodapp.service.RestaurantService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.List;

public class Demo {
    public static void main(String[] args) {
        // Load Spring Configuration
        ApplicationContext context =
                new AnnotationConfigApplicationContext(AppConfig.class);
        RestaurantService RestaurantService = context.getBean(RestaurantService.class);
        System.out.println("Restaurant Bean Loaded: " + RestaurantService);
        List<Restaurant> Restaurant = RestaurantService.findAll(new PageRequest(0,10));
        for (Restaurant restaurant : Restaurant) {
            System.out.println(restaurant);
        }
    }
}
