package com.ram.foodapp.repository.inmemory;

import com.ram.foodapp.dto.request.PageRequest;
import com.ram.foodapp.model.orderitem.OrderItem;
import com.ram.foodapp.repository.OrderItemRepository;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
public class InMemoryOrderItemRepositoryImpl implements OrderItemRepository {
    private final Map<Integer, OrderItem> store = new ConcurrentHashMap<>();
    private final Map<Integer, Set<Integer>> orderIndex = new ConcurrentHashMap<>();
    private final AtomicInteger idGenerator = new AtomicInteger(1);

    @Override
    public Optional<OrderItem> findById(int id) {
        validateId(id);
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<OrderItem> findByOrderId(int orderId) {
        validateId(orderId);
        Set<Integer> ids = orderIndex.getOrDefault(orderId, Set.of());
        return ids.stream()
                .map(store::get)
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(OrderItem::getId))
                .toList();
    }

    @Override
    public List<OrderItem> findAll(PageRequest pageRequest) {
        validatePage(pageRequest);
        List<OrderItem> list = store.values().stream()
                .sorted(Comparator.comparing(OrderItem::getId))
                .toList();
        return paginate(list, pageRequest);
    }

    @Override
    public List<OrderItem> findByMenuId(int menuId, PageRequest pageRequest) {
        validatePage(pageRequest);
        return List.of(); // or throw UnsupportedOperationException
    }

    @Override
    public OrderItem save(OrderItem orderItem) {
        if (orderItem == null) {
            throw new IllegalArgumentException("OrderItem cannot be null");
        }
        int id = idGenerator.getAndIncrement();
        OrderItem saved = new OrderItem(
                id,
                orderItem.getOrderId(),
                orderItem.getUnitPrice(),
                orderItem.getQuantity(),
                orderItem.getItemName(),
                orderItem.getItemDescription(),
                orderItem.getRestaurantName()
        );
        store.put(id, saved);
        orderIndex.compute(orderItem.getOrderId(), (k, v) -> {
            if (v == null) v = ConcurrentHashMap.newKeySet();
            v.add(id);
            return v;
        });
        return saved;
    }

    @Override
    public List<OrderItem> saveAll(List<OrderItem> orderItems) {
        if (orderItems == null || orderItems.isEmpty()) {
            return List.of();
        }
        List<OrderItem> saved = new ArrayList<>();
        for (OrderItem item : orderItems) {
            saved.add(save(item)); // reuse logic
        }
        return List.copyOf(saved);
    }

    @Override
    public OrderItem update(OrderItem orderItem) {
        if (orderItem == null || orderItem.getId() == null) {
            throw new IllegalArgumentException("OrderItem or id cannot be null");
        }
        return store.compute(orderItem.getId(), (id, existing) -> {
            if (existing == null) {
                throw new IllegalArgumentException("OrderItem not found");
            }
            if (existing.getOrderId() != orderItem.getOrderId()) {
                removeFromIndex(existing);
                addToIndex(orderItem.getOrderId(), id);
            }
            return new OrderItem(
                    id,
                    orderItem.getOrderId(),
                    orderItem.getUnitPrice(),
                    orderItem.getQuantity(),
                    orderItem.getItemName(),
                    orderItem.getItemDescription(),
                    orderItem.getRestaurantName()
            );
        });
    }


    @Override
    public void deleteByOrderId(int orderId) {
        validateId(orderId);
        Set<Integer> ids = orderIndex.remove(orderId);
        if (ids == null) return;
        for (Integer id : ids) {
            store.remove(id);
        }
    }

    private void addToIndex(int orderId, int id) {
        orderIndex.compute(orderId, (k, v) -> {
            if (v == null) v = ConcurrentHashMap.newKeySet();
            v.add(id);
            return v;
        });
    }

    private void removeFromIndex(OrderItem item) {
        Set<Integer> ids = orderIndex.get(item.getOrderId());
        if (ids != null) {
            ids.remove(item.getId());
            if (ids.isEmpty()) {
                orderIndex.remove(item.getOrderId());
            }
        }
    }

    private List<OrderItem> paginate(List<OrderItem> list, PageRequest pageRequest) {
        int page = pageRequest.getPage();
        int size = pageRequest.getSize();
        int start = page * size;
        if (start >= list.size()) {
            return List.of();
        }
        int end = Math.min(start + size, list.size());
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