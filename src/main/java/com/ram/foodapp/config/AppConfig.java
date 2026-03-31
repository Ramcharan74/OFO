package com.ram.foodapp.config;

import com.ram.foodapp.repository.*;
import com.ram.foodapp.repository.implementation.*;
import com.ram.foodapp.service.*;
import com.ram.foodapp.service.impl.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    //user classes
    @Bean
    public UserRepository userRepository() {
        return new UserRepositoryImpl();
    }

    @Bean
    public UserService userService() {
        return new UserServiceImpl(userRepository());
    }

    //Restaurant classes
    @Bean
    public RestaurantRepository restaurantRepository() {
        return new RestaurantRepositoryImpl();
    }

    @Bean
    public RestaurantService restaurantService() {
        return new RestaurantServiceImpl(restaurantRepository(), menuItemService());
    }


    //OrderItem classes
    @Bean
    public OrderItemRepository orderItemRepository() {
        return new OrderItemRepositoryImpl();
    }

    @Bean
    public OrderItemService orderItemService() {
        return new OrderItemServiceImpl(orderItemRepository());
    }

    //MenuItem classes
    @Bean
    public MenuItemRepository menuItemRepository() {
        return new MenuItemRepositoryImpl();
    }

    @Bean
    public MenuItemService menuItemService() {
        return new MenuItemServiceImpl(menuItemRepository());
    }

    //foodOrder classes
    @Bean
    public OrderRepository foodOrderRepository() {
        return new OrderRepositoryImpl();
    }

    @Bean
    public FoodOrderService foodOrderService() {
        return new FoodOrderServiceImpl(foodOrderRepository());
    }

    //cartItem classes
    @Bean
    public CartItemRepository cartItemRepository() {
        return new CartItemRepositoryImpl();
    }

    @Bean
    public CartItemService cartItemService() {
        return new CartItemServiceImpl(cartItemRepository(), menuItemService());
    }

    //address classes
    @Bean
    public AddressRepository addressRepository() {
        return new AddressRepositoryImpl();
    }

    @Bean
    public AddressService addressService() {
        return new AddressServiceImpl(addressRepository(), userRepository());
    }

}
