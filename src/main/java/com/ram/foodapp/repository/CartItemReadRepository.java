package com.ram.foodapp.repository;

import com.ram.foodapp.dto.request.PageRequest;
import com.ram.foodapp.model.cartitem.CartItem;

import java.util.List;
import java.util.Optional;

public interface CartItemReadRepository {

    List<CartItem> findAll(PageRequest pageRequest);

    List<CartItem> findByUserId(int userId);

    Optional<CartItem>  findByMenuId(int menuId);

    Optional<CartItem> findByUserIdAndMenuItemId(int userId, int menuItemId);

    boolean existsByUserIdAndMenuItemId(int userId, int menuItemId);
}
