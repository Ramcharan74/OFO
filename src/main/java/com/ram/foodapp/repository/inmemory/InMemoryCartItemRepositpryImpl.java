package com.ram.foodapp.repository.inmemory;

import com.ram.foodapp.dto.request.PageRequest;
import com.ram.foodapp.model.cartitem.CartItem;
import com.ram.foodapp.repository.CartItemRepository;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryCartItemRepositpryImpl implements CartItemRepository {

    private final Map<String, CartItem> store = new ConcurrentHashMap<>();

    @Override
    public List<CartItem> findAll(PageRequest pageRequest) {
        validatePage(pageRequest);
        List<CartItem> list = store.values().stream().sorted(Comparator.comparing(CartItem::getUserId).thenComparing(CartItem::getMenuItemId)).toList();
        return paginate(list,pageRequest);
    }

    @Override
    public List<CartItem> findByUserId(int userId) {
        validateId(userId);
        return store.values().stream()
                .filter(item -> item.getUserId() == userId)
                .sorted(Comparator.comparing(CartItem::getMenuItemId))
                .toList();
    }

    @Override
    public Optional<CartItem> findByMenuId(int menuId) {
        validateId(menuId);
        return store.values().stream()
                .filter(item -> item.getMenuItemId() == menuId)
                .findFirst();
    }

    @Override
    public Optional<CartItem> findByUserIdAndMenuItemId(int userId, int menuItemId) {
        validateId(userId);
        validateId(menuItemId);
        return Optional.ofNullable(store.get(key(userId, menuItemId)));
    }

    @Override
    public boolean existsByUserIdAndMenuItemId(int userId, int menuItemId) {
        return store.containsKey(key(userId, menuItemId));
    }

    @Override
    public CartItem save(CartItem cartItem) {
        if (cartItem == null) {
            throw new IllegalArgumentException("CartItem cannot be null");
        }
        String key = key(cartItem.getUserId(), cartItem.getMenuItemId());
        return store.compute(key, (k, existing) -> {
            if (existing == null) {
                return cartItem;
            }
            return new CartItem(
                    existing.getUserId(),
                    existing.getMenuItemId(),
                    existing.getQuantity() + cartItem.getQuantity(),
                    existing.getItemName(),
                    existing.getUnitPrice(),
                    existing.getRestaurantName()
            );
        });
    }

    @Override
    public void updateQuantity(int userId, int menuItemId, int quantity) {
        validateId(userId);
        validateId(menuItemId);
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be > 0");
        }
        store.compute(key(userId, menuItemId), (k, existing) -> {
            if (existing == null) {
                throw new IllegalArgumentException("Cart item not found");
            }
            return new CartItem(
                    existing.getUserId(),
                    existing.getMenuItemId(),
                    quantity,
                    existing.getItemName(),
                    existing.getUnitPrice(),
                    existing.getRestaurantName()
            );
        });
    }

    @Override
    public void incrementQuantity(int userId, int menuItemId, int delta) {
        validateId(userId);
        validateId(menuItemId);
        if (delta <= 0) {
            throw new IllegalArgumentException("Delta must be > 0");
        }
        store.compute(key(userId, menuItemId), (k, existing) -> {
            if (existing == null) {
                throw new IllegalArgumentException("Cart item not found");
            }
            return new CartItem(
                    existing.getUserId(),
                    existing.getMenuItemId(),
                    existing.getQuantity() + delta,
                    existing.getItemName(),
                    existing.getUnitPrice(),
                    existing.getRestaurantName()
            );
        });
    }

    @Override
    public void deleteByUserIdAndMenuItemId(int userId, int menuItemId) {
        validateId(userId);
        validateId(menuItemId);
        store.remove(key(userId, menuItemId));
    }

    @Override
    public void clearCart(int userId) {
        validateId(userId);
        store.entrySet().removeIf(entry ->
                entry.getValue().getUserId() == userId
        );
    }

    private String key(int userId, int menuItemId) {
        return userId + "_" + menuItemId;
    }

    private List<CartItem> paginate(List<CartItem> list, PageRequest pageRequest) {
        int start = pageRequest.getPage() * pageRequest.getSize();
        if (start >= list.size()) {
            return List.of();
        }
        int end = Math.min(start + pageRequest.getSize(), list.size());
        return List.copyOf(list.subList(start, end));
    }

    private void validateId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Invalid id");
        }
    }

    private void validatePage(PageRequest pageRequest) {
        if (pageRequest.getPage() < 0 || pageRequest.getSize() <= 0) {
            throw new IllegalArgumentException("Invalid page or size");
        }
    }
}
