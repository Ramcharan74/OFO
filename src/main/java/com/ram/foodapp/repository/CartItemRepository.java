package com.ram.foodapp.repository;

import com.ram.foodapp.dto.request.PageRequest;
import com.ram.foodapp.model.cartitem.CartItem;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository {
    //Read
    List<CartItem> findAll(PageRequest pageRequest);

    List<CartItem> findByUserId(int userId);

    Optional<CartItem> findByMenuId(int menuId);

    Optional<CartItem> findByUserIdAndMenuItemId(int userId, int menuItemId);

    boolean existsByUserIdAndMenuItemId(int userId, int menuItemId);

    //write
    CartItem save(CartItem cartItem);

    void updateQuantity(int userId, int menuItemId, int quantity);

    void incrementQuantity(int userId, int menuItemId, int delta);

    void deleteByUserIdAndMenuItemId(int userId, int menuItemId);

    void clearCart(int userId);
}
