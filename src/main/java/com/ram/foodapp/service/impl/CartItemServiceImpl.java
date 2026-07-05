package com.ram.foodapp.service.impl;

import com.ram.foodapp.dto.request.PageRequest;
import com.ram.foodapp.model.cartitem.CartItem;
import com.ram.foodapp.repository.CartItemRepository;
import com.ram.foodapp.service.CartItemService;
import com.ram.foodapp.service.MenuItemService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class CartItemServiceImpl implements CartItemService {

    private final CartItemRepository cartItemRepository;
    private final MenuItemService menuItemService;

    public CartItemServiceImpl(CartItemRepository cartItemRepository, MenuItemService menuItemService) {
        this.cartItemRepository = cartItemRepository;
        this.menuItemService = menuItemService;
    }

    @Override
    public CartItem save(CartItem cartItem) {
        validate(cartItem);
        Optional<CartItem> existing = cartItemRepository
                .findByUserIdAndMenuItemId(cartItem.getUserId(), cartItem.getMenuItemId());
        if (existing.isPresent()) {
            int newQuantity = existing.get().getQuantity() + cartItem.getQuantity();
            cartItemRepository.updateQuantity(
                    cartItem.getUserId(),
                    cartItem.getMenuItemId(),
                    newQuantity
            );
            return new CartItem(
                    cartItem.getUserId(),
                    cartItem.getMenuItemId(),
                    newQuantity,
                    cartItem.getItemName(),
                    cartItem.getUnitPrice(),
                    cartItem.getRestaurantName()
            );
        }
        return cartItemRepository.save(cartItem);
    }

    @Override
    public List<CartItem> findAll(PageRequest pageRequest) {
        Objects.requireNonNull(pageRequest, "PageRequest cannot be null");
        return cartItemRepository.findAll(pageRequest);
    }

    @Override
    public List<CartItem> findByUserId(int userId) {
        validateUserId(userId);
        return cartItemRepository.findByUserId(userId);
    }

    @Override
    public Optional<CartItem> findByMenuId(int menuId) {
        validateMenuItemId(menuId);
        return cartItemRepository.findByMenuId(menuId);
    }

    @Override
    public Optional<CartItem> findByUserIdAndMenuItemId(int userId, int menuItemId) {
        validateUserId(userId);
        validateMenuItemId(menuItemId);
        return cartItemRepository.findByUserIdAndMenuItemId(userId, menuItemId);
    }

    @Override
    public boolean existsByUserIdAndMenuItemId(int userId, int menuItemId) {
        validateUserId(userId);
        validateMenuItemId(menuItemId);
        return cartItemRepository.existsByUserIdAndMenuItemId(userId, menuItemId);
    }

    @Override
    public void updateQuantity(int userId, int menuItemId, int quantity) {
        validateUserId(userId);
        validateMenuItemId(menuItemId);
        if (quantity <= 0) {
            cartItemRepository.deleteByUserIdAndMenuItemId(userId, menuItemId);
            return;
        }
        if (!menuItemService.existsById(menuItemId)) {
            throw new IllegalArgumentException("Menu Item does not exist");
        }
        if (!existsByUserIdAndMenuItemId(userId, menuItemId)) {
            throw new IllegalArgumentException("Cart item does not exist");
        }
        cartItemRepository.updateQuantity(userId, menuItemId, quantity);
    }

    @Override
    public void incrementQuantity(int userId, int menuItemId, int quantity) {
        validateUserId(userId);
        validateMenuItemId(menuItemId);
        if (quantity == 0) return;
        Optional<CartItem> existing = findByUserIdAndMenuItemId(userId, menuItemId);
        if (existing.isEmpty()) {
            if (quantity < 0) {
                throw new IllegalArgumentException("Cannot decrement non-existing item");
            }
            save(new CartItem(userId, menuItemId, quantity, existing.get().getItemName(), existing.get().getUnitPrice(), existing.get().getRestaurantName()));
            return;
        }
        int newQuantity = existing.get().getQuantity() + quantity;
        if (newQuantity <= 0) {
            deleteByUserIdAndMenuItemId(userId, menuItemId);
        } else {
            cartItemRepository.updateQuantity(userId, menuItemId, newQuantity);
        }
    }

    @Override
    public void deleteByUserIdAndMenuItemId(int userId, int menuItemId) {
        validateUserId(userId);
        validateMenuItemId(menuItemId);
        cartItemRepository.deleteByUserIdAndMenuItemId(userId, menuItemId);
    }

    @Override
    public void clearCart(int userId) {
        validateUserId(userId);
        cartItemRepository.clearCart(userId);
    }

    private void validate(CartItem cartItem) {
        Objects.requireNonNull(cartItem, "CartItem cannot be null");
    }

    private void validateUserId(int userId) {
        if (userId <= 0) {
            throw new IllegalArgumentException("Invalid userId");
        }
    }

    private void validateMenuItemId(int menuItemId) {
        if (menuItemId <= 0) {
            throw new IllegalArgumentException("Invalid menuItemId");
        }
    }
}
