package com.ram.foodapp.repository;

import com.ram.foodapp.model.cartitem.CartItem;

public interface CartItemWriteRepository {

    CartItem save(CartItem cartItem);

    void updateQuantity(int userId, int menuItemId, int quantity);

    void incrementQuantity(int userId, int menuItemId, int delta);

    void deleteByUserIdAndMenuItemId(int userId, int menuItemId);

    void clearCart(int userId);
}
