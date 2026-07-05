package com.ram.foodapp.controller;

import com.ram.foodapp.dto.request.*;
import com.ram.foodapp.dto.response.ApiResponse;
import com.ram.foodapp.dto.response.MenuItemResponse;
import com.ram.foodapp.enums.FoodType;
import com.ram.foodapp.exception.ResourceNotFoundException;
import com.ram.foodapp.mapper.MenuItemMapper;
import com.ram.foodapp.model.menuitem.MenuItem;
import com.ram.foodapp.service.MenuItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/menuitem")
public class MenuItemController {

    private static final Logger logger = LoggerFactory.getLogger(MenuItemController.class);

    private final MenuItemService menuItemService;

    public MenuItemController(MenuItemService menuItemService) {
        this.menuItemService = menuItemService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<MenuItemResponse>>> getAll(
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        List<MenuItemResponse> list = menuItemService.findAll(new PageRequest(page, size))
                .stream()
                .map(MenuItemMapper::toResponse)
                .toList();
        return ResponseEntity.ok(ApiResponse.success("Menu items fetched", list));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MenuItemResponse>> getById(@PathVariable int id) {
        MenuItem item = menuItemService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found with id: " + id));
        return ResponseEntity.ok(ApiResponse.success("Menu item fetched", MenuItemMapper.toResponse(item)));
    }

    @GetMapping("/restaurant")
    public ResponseEntity<ApiResponse<List<MenuItemResponse>>> getByRestaurant(
            @RequestParam int restaurantId,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        List<MenuItemResponse> list = menuItemService.findByRestaurantId(restaurantId, new PageRequest(page, size))
                .stream()
                .map(MenuItemMapper::toResponse)
                .toList();
        return ResponseEntity.ok(ApiResponse.success("Menu items by restaurant fetched", list));
    }

    @GetMapping("/category")
    public ResponseEntity<ApiResponse<List<MenuItemResponse>>> getByCategory(
            @RequestParam int restaurantId, @RequestParam String category,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        List<MenuItemResponse> list = menuItemService
                .findByCategorynRestaurant(restaurantId, FoodType.valueOf(category.toUpperCase()), new PageRequest(page, size))
                .stream()
                .map(MenuItemMapper::toResponse)
                .toList();
        return ResponseEntity.ok(ApiResponse.success("Menu items by category fetched", list));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<MenuItemResponse>>> search(
            @RequestParam int restaurantId, @RequestParam String name,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        List<MenuItemResponse> list = menuItemService.searchByName(restaurantId, name, new PageRequest(page, size))
                .stream()
                .map(MenuItemMapper::toResponse)
                .toList();
        return ResponseEntity.ok(ApiResponse.success("Search results", list));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<MenuItemResponse>> create(@Validated @RequestBody CreateMenuItemRequest request) {
        logger.info("Creating menu item for restaurantId={}", request.restaurantId());
        MenuItem saved = menuItemService.save(MenuItemMapper.toEntity(request));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Menu item created", MenuItemMapper.toResponse(saved)));
    }

    @PutMapping("/availability")
    public ResponseEntity<ApiResponse<Void>> updateAvailability(@Validated @RequestBody UpdateAvailabilityRequest request) {
        menuItemService.updateAvailability(request.menuItemId(), request.isAvailable());
        return ResponseEntity.ok(ApiResponse.success("Availability updated", null));
    }

    @PutMapping("/price")
    public ResponseEntity<ApiResponse<Void>> updatePrice(@Validated @RequestBody UpdatePriceRequest request) {
        menuItemService.updatePrice(request.menuItemId(), request.price());
        return ResponseEntity.ok(ApiResponse.success("Price updated", null));
    }

    @PutMapping("/quantity")
    public ResponseEntity<ApiResponse<Void>> updateQuantity(@Validated @RequestBody UpdateQuantityRequest request) {
        menuItemService.updateQuantity(request.menuItemId(), request.quantity());
        return ResponseEntity.ok(ApiResponse.success("Quantity updated", null));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteById(@PathVariable int id) {
        menuItemService.deleteById(id);
        return ResponseEntity.ok(ApiResponse.success("Menu item deleted", null));
    }
}
