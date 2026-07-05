package com.ram.foodapp.repository.inmemory;

import com.ram.foodapp.dto.request.PageRequest;
import com.ram.foodapp.enums.FoodType;
import com.ram.foodapp.model.menuitem.MenuItem;
import com.ram.foodapp.repository.MenuItemRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
public class InMemoryMenuItemRepositoryImpl implements MenuItemRepository {

    private final Map<Integer, MenuItem> store = new ConcurrentHashMap<>();
    private final AtomicInteger idGenerator = new AtomicInteger(1);

    @Override
    public Optional<MenuItem> findById(int id) {
        validateId(id);
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<MenuItem> findAll(PageRequest pageRequest) {
        validatePage(pageRequest);

        List<MenuItem> list = store.values().stream()
                .sorted(Comparator.comparing(MenuItem::getId))
                .toList();

        return paginate(list, pageRequest);
    }

    @Override
    public List<MenuItem> findByAvailability(Boolean isAvailable) {
        return store.values().stream()
                .filter(item -> item.getInventory().isAvailable() == isAvailable)
                .toList();
    }

    @Override
    public List<MenuItem> findByCategory(FoodType foodType) {
        return store.values().stream()
                .filter(item -> item.getCategory().getFoodType() == foodType)
                .toList();
    }

    @Override
    public List<MenuItem> findByRestaurantId(int restaurantId) {
        validateId(restaurantId);

        return store.values().stream()
                .filter(item -> item.getRestaurantId() == restaurantId)
                .sorted(Comparator.comparing(MenuItem::getId))
                .toList();
    }

    @Override
    public List<MenuItem> findByRestaurantId(int restaurantId, PageRequest pageRequest) {
        return paginate(findByRestaurantId(restaurantId), pageRequest);
    }

    @Override
    public List<MenuItem> findAvailableByRestaurantId(int restaurantId, PageRequest pageRequest) {
        List<MenuItem> list = store.values().stream()
                .filter(item -> item.getRestaurantId() == restaurantId)
                .filter(item -> item.getInventory().isAvailable())
                .sorted(Comparator.comparing(MenuItem::getId))
                .toList();

        return paginate(list, pageRequest);
    }

    @Override
    public List<MenuItem> findByCategorynRestaurant(int restaurantId, FoodType category, PageRequest pageRequest) {
        List<MenuItem> list = store.values().stream()
                .filter(item -> item.getRestaurantId() == restaurantId)
                .filter(item -> item.getCategory().getFoodType() == category)
                .sorted(Comparator.comparing(MenuItem::getId))
                .toList();

        return paginate(list, pageRequest);
    }

    @Override
    public List<MenuItem> findBySubCategorynRestaurant(int restaurantId, String subCategory, PageRequest pageRequest) {
        String normalized = normalize(subCategory);

        List<MenuItem> list = store.values().stream()
                .filter(item -> item.getRestaurantId() == restaurantId)
                .filter(item -> item.getCategory().getSubCategory() != null &&
                        item.getCategory().getSubCategory().toLowerCase().equals(normalized))
                .sorted(Comparator.comparing(MenuItem::getId))
                .toList();

        return paginate(list, pageRequest);
    }

    @Override
    public List<MenuItem> searchByName(int restaurantId, String name, PageRequest pageRequest) {
        String normalized = normalize(name);

        List<MenuItem> list = store.values().stream()
                .filter(item -> item.getRestaurantId() == restaurantId)
                .filter(item -> item.getMenuDetails().getName().toLowerCase().contains(normalized))
                .sorted(Comparator.comparing(MenuItem::getId))
                .toList();

        return paginate(list, pageRequest);
    }

    @Override
    public boolean existsById(int id) {
        validateId(id);
        return store.containsKey(id);
    }

    @Override
    public MenuItem save(MenuItem menuItem) {
        if (menuItem == null) {
            throw new IllegalArgumentException("MenuItem cannot be null");
        }
        int id = idGenerator.getAndIncrement();
        MenuItem saved = new MenuItem.Builder()
                .id(id)
                .restaurantId(menuItem.getRestaurantId())
                .menuDetails(menuItem.getMenuDetails())
                .category(menuItem.getCategory())
                .imageUrl(menuItem.getImageUrl())
                .inventory(menuItem.getInventory())
                .build();
        store.put(id, saved);
        return saved;
    }

    @Override
    public List<MenuItem> saveAll(List<MenuItem> menuItems) {
        if (menuItems == null || menuItems.isEmpty()) {
            return List.of();
        }
        List<MenuItem> saved = new ArrayList<>();
        for (MenuItem item : menuItems) {
            saved.add(save(item));
        }
        return List.copyOf(saved);
    }

    @Override
    public void updateAvailability(int menuItemId, boolean isAvailable) {
        store.compute(menuItemId, (id, existing) -> {
            if (existing == null) throw new IllegalArgumentException("MenuItem not found");
            int qty = isAvailable ? Math.max(existing.getInventory().getQuantity(), 1) : 0;
            return rebuild(existing, qty, null);
        });
    }

    @Override
    public void updatePrice(int menuItemId, double price) {
        if (price <= 0) throw new IllegalArgumentException("Invalid price");
        store.compute(menuItemId, (id, existing) -> {
            if (existing == null) throw new IllegalArgumentException("MenuItem not found");
            return rebuild(existing, null, BigDecimal.valueOf(price));
        });
    }

    @Override
    public void updateQuantity(int menuItemId, int quantity) {
        if (quantity < 0) throw new IllegalArgumentException("Invalid quantity");
        store.compute(menuItemId, (id, existing) -> {
            if (existing == null) throw new IllegalArgumentException("MenuItem not found");
            return rebuild(existing, quantity, null);
        });
    }

    @Override
    public void deleteById(int id) {
        validateId(id);
        store.remove(id);
    }

    private MenuItem rebuild(MenuItem existing, Integer newQty, BigDecimal newPrice) {
        return new MenuItem.Builder()
                .id(existing.getId())
                .restaurantId(existing.getRestaurantId())
                .category(existing.getCategory())
                .imageUrl(existing.getImageUrl())
                .menuDetails(new com.ram.foodapp.model.menuitem.MenuDetails(
                        existing.getMenuDetails().getName(),
                        existing.getMenuDetails().getDescription(),
                        newPrice != null ? newPrice : existing.getMenuDetails().getPrice()
                ))
                .inventory(new com.ram.foodapp.model.menuitem.Inventory(
                        newQty != null ? newQty : existing.getInventory().getQuantity()
                ))
                .build();
    }

    private List<MenuItem> paginate(List<MenuItem> list, PageRequest pageRequest) {
        int start = pageRequest.getPage() * pageRequest.getSize();
        if (start >= list.size()) return List.of();
        int end = Math.min(start + pageRequest.getSize(), list.size());
        return List.copyOf(list.subList(start, end));
    }

    private void validateId(int id) {
        if (id <= 0) throw new IllegalArgumentException("Invalid id");
    }

    private void validatePage(PageRequest pageRequest) {
        if (pageRequest.getPage() < 0 || pageRequest.getSize() <= 0) {
            throw new IllegalArgumentException("Invalid page or size");
        }
    }

    private String normalize(String input) {
        if (input == null || input.isBlank()) {
            throw new IllegalArgumentException("Invalid input");
        }
        return input.trim().toLowerCase();
    }
}