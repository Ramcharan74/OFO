package com.ram.foodapp.controller;

import com.ram.foodapp.dto.request.AddToCartRequest;
import com.ram.foodapp.dto.request.IncrementCartRequest;
import com.ram.foodapp.dto.request.UpdateCartQuantityRequest;
import com.ram.foodapp.dto.response.ApiResponse;
import com.ram.foodapp.dto.response.CartItemResponse;
import com.ram.foodapp.mapper.CartItemMapper;
import com.ram.foodapp.model.cartitem.CartItem;
import com.ram.foodapp.service.CartItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartItemController {

    private static final Logger logger = LoggerFactory.getLogger(CartItemController.class);

    private final CartItemService cartItemService;

    public CartItemController(CartItemService cartItemService) {
        this.cartItemService = cartItemService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CartItemResponse>>> getByUser(@RequestParam int userId) {
        List<CartItemResponse> list = cartItemService.findByUserId(userId)
                .stream()
                .map(CartItemMapper::toResponse)
                .toList();
        return ResponseEntity.ok(ApiResponse.success("Cart fetched", list));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CartItemResponse>> addToCart(@Validated @RequestBody AddToCartRequest request) {
        logger.info("Adding to cart userId={}, menuItemId={}", request.userId(), request.menuItemId());
        if (cartItemService.existsByUserIdAndMenuItemId(request.userId(), request.menuItemId())) {
            cartItemService.incrementQuantity(request.userId(), request.menuItemId(), request.quantity());
            return ResponseEntity.ok(ApiResponse.success("Cart quantity updated", null));
        }
        CartItem saved = cartItemService.save(CartItemMapper.toEntity(request));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Item added to cart", CartItemMapper.toResponse(saved)));
    }

    @PutMapping("/quantity")
    public ResponseEntity<ApiResponse<Void>> updateQuantity(@Validated @RequestBody UpdateCartQuantityRequest request) {
        cartItemService.updateQuantity(request.userId(), request.menuItemId(), request.quantity());
        return ResponseEntity.ok(ApiResponse.success("Quantity updated", null));
    }

    @PutMapping("/increment")
    public ResponseEntity<ApiResponse<Void>> incrementQuantity(@Validated @RequestBody IncrementCartRequest request) {
        cartItemService.incrementQuantity(request.userId(), request.menuItemId(), request.delta());
        return ResponseEntity.ok(ApiResponse.success("Quantity incremented", null));
    }

    @DeleteMapping("/clear")
    public ResponseEntity<ApiResponse<Void>> clearCart(@RequestParam int userId) {
        cartItemService.clearCart(userId);
        return ResponseEntity.ok(ApiResponse.success("Cart cleared", null));
    }

    @DeleteMapping("/{userId}/{menuItemId}")
    public ResponseEntity<ApiResponse<Void>> removeItem(@PathVariable int userId, @PathVariable int menuItemId) {
        cartItemService.deleteByUserIdAndMenuItemId(userId, menuItemId);
        return ResponseEntity.ok(ApiResponse.success("Item removed", null));
    }
}
