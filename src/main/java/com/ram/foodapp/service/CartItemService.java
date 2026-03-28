package com.ram.foodapp.service;

import com.ram.foodapp.dto.request.PageRequest;
import com.ram.foodapp.model.cartitem.CartItem;

import java.util.List;
import java.util.Optional;

public interface CartItemService {
    CartItem save(CartItem cartItem);
    List<CartItem> findAll(PageRequest pageRequest);
    List<CartItem> findByUserId(int userId);
    Optional<CartItem> findByMenuId(int menuId);
    Optional<CartItem> findByUserIdAndMenuItemId(int userId, int menuId);
    boolean existsByUserIdAndMenuItemId(int userId, int menuItemId);
    void updateQuantity(int userId, int menuItemId, int quantity);
    void incrementQuantity(int userId, int menuItemId, int delta);
    void deleteByUserIdAndMenuItemId(int userId, int menuItemId);
    void clearCart(int userId);
}
