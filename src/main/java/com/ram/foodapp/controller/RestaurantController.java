package com.ram.foodapp.controller;

import com.ram.foodapp.dto.request.CreateRestaurantRequest;
import com.ram.foodapp.dto.request.PageRequest;
import com.ram.foodapp.dto.request.UpdateRestaurantRatingRequest;
import com.ram.foodapp.dto.request.UpdateRestaurantStatusRequest;
import com.ram.foodapp.dto.response.ApiResponse;
import com.ram.foodapp.dto.response.RestaurantResponse;
import com.ram.foodapp.exception.DataAccessException;
import com.ram.foodapp.mapper.RestaurantMapper;
import com.ram.foodapp.model.restaurant.Restaurant;
import com.ram.foodapp.service.RestaurantService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/restaurant")
public class RestaurantController {

    private static final Logger logger = LoggerFactory.getLogger(RestaurantController.class);

    private final RestaurantService restaurantService;

    public RestaurantController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<RestaurantResponse>>> getAll(
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        List<RestaurantResponse> restaurants = restaurantService.findAll(new PageRequest(page, size))
                .stream()
                .map(RestaurantMapper::toResponse)
                .toList();
        return ResponseEntity.ok(ApiResponse.success("Restaurants fetched", restaurants));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RestaurantResponse>> getById(@PathVariable int id) {
        Restaurant restaurant = restaurantService.findById(id)
                .orElseThrow(() -> new DataAccessException("Restaurant not found"));
        return ResponseEntity.ok(ApiResponse.success("Restaurant fetched", RestaurantMapper.toResponse(restaurant)));
    }

    @GetMapping("/user")
    public ResponseEntity<ApiResponse<List<RestaurantResponse>>> getByUserId(@RequestParam int userId) {
        List<RestaurantResponse> list = restaurantService.findByUserId(userId)
                .stream()
                .map(RestaurantMapper::toResponse)
                .toList();
        return ResponseEntity.ok(ApiResponse.success("User restaurants fetched", list));
    }

    @GetMapping("/address")
    public ResponseEntity<ApiResponse<List<RestaurantResponse>>> getByAddressId(@RequestParam int addressId) {
        List<RestaurantResponse> list = restaurantService.findByAddressId(addressId)
                .stream()
                .map(RestaurantMapper::toResponse)
                .toList();
        return ResponseEntity.ok(ApiResponse.success("Restaurants by address fetched", list));
    }

    @GetMapping("/status")
    public ResponseEntity<ApiResponse<List<RestaurantResponse>>> getByStatus(
            @RequestParam String status,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        List<RestaurantResponse> list = restaurantService.findByStatus(status, new PageRequest(page, size))
                .stream()
                .map(RestaurantMapper::toResponse)
                .toList();
        return ResponseEntity.ok(ApiResponse.success("Restaurants by status fetched", list));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<RestaurantResponse>> create(@Validated @RequestBody CreateRestaurantRequest request) {
        logger.info("Creating restaurant for userId={}", request.userId());
        Restaurant saved = restaurantService.save(RestaurantMapper.toEntity(request));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Restaurant created", RestaurantMapper.toResponse(saved)));
    }

    @PutMapping("/status")
    public ResponseEntity<ApiResponse<Void>> updateStatus(@Validated @RequestBody UpdateRestaurantStatusRequest request) {
        restaurantService.updateStatus(request.restaurantId(), request.status());
        return ResponseEntity.ok(ApiResponse.success("Status updated", null));
    }

    @PutMapping("/rating")
    public ResponseEntity<ApiResponse<Void>> updateRating(@Validated @RequestBody UpdateRestaurantRatingRequest request) {
        restaurantService.updateRating(request.restaurantId(), request.rating(), request.ratingCount());
        return ResponseEntity.ok(ApiResponse.success("Rating updated", null));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteById(@PathVariable int id) {
        restaurantService.deleteById(id);
        return ResponseEntity.ok(ApiResponse.success("Restaurant deleted", null));
    }
}
