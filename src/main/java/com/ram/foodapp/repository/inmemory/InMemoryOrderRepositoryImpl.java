package com.ram.foodapp.repository.inmemory;

import com.ram.foodapp.dto.request.PageRequest;
import com.ram.foodapp.enums.OrderStatus;
import com.ram.foodapp.enums.PaymentMode;
import com.ram.foodapp.model.foodorder.FoodOrder;
import com.ram.foodapp.model.foodorder.Payment;
import com.ram.foodapp.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
public class InMemoryOrderRepositoryImpl implements OrderRepository {
    private static final Logger logger = LoggerFactory.getLogger(InMemoryOrderRepositoryImpl.class);
    private final Map<Integer, FoodOrder> orderStore = new ConcurrentHashMap<>();
    private final AtomicInteger idGenerator = new AtomicInteger(1);

    @Override
    public Optional<FoodOrder> findById(int id) {
        validateId(id);
        return Optional.ofNullable(orderStore.get(id));
    }

    @Override
    public List<FoodOrder> findByUserId(int userId, PageRequest pageRequest) {
        validateId(userId);
        validatePage(pageRequest);
        List<FoodOrder> list = orderStore.values().stream()
                .filter(o -> o.getUserId() == userId)
                .sorted(Comparator.comparing(FoodOrder::getId))
                .toList();
        return paginate(list, pageRequest);
    }

    @Override
    public List<FoodOrder> findByStatus(String status, PageRequest pageRequest) {
        validatePage(pageRequest);
        OrderStatus orderStatus = parseStatus(status);
        List<FoodOrder> list = orderStore.values().stream()
                .filter(o -> o.getStatus() == orderStatus)
                .sorted(Comparator.comparing(FoodOrder::getId))
                .toList();
        return paginate(list, pageRequest);
    }

    @Override
    public List<FoodOrder> findByPaidStatus(Boolean isPaid) {
        return orderStore.values().stream()
                .filter(o -> o.getPayment().isPaid() == isPaid)
                .toList();
    }

    @Override
    public List<FoodOrder> findAll(PageRequest pageRequest) {
        validatePage(pageRequest);
        List<FoodOrder> list = orderStore.values().stream()
                .sorted(Comparator.comparing(FoodOrder::getId))
                .toList();
        return paginate(list, pageRequest);
    }

    @Override
    public List<FoodOrder> findByDate(Date date) {
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }
        return orderStore.values().stream()
                .filter(o -> Date.valueOf(o.getOrderDateTime().toLocalDate()).equals(date))
                .toList();
    }

    @Override
    public List<FoodOrder> findByPaymentMode(String paymentMode, PageRequest pageRequest) {
        validatePage(pageRequest);
        String normalized = normalize(paymentMode);
        List<FoodOrder> list = orderStore.values().stream()
                .filter(o -> normalized.equals(o.getPayment().getPaymentMode().name().toLowerCase()))
                .toList();
        return paginate(list, pageRequest);
    }

    @Override
    public FoodOrder save(FoodOrder order) {
        if (order == null) {
            throw new IllegalArgumentException("Order cannot be null");
        }
        int id = idGenerator.getAndIncrement();
        FoodOrder saved = new FoodOrder.Builder()
                .id(id)
                .userId(order.getUserId())
                .orderStatus(order.getStatus())
                .totalPrice(order.getTotalPrice())
                .orderDateTime(order.getOrderDateTime())
                .payment(order.getPayment())
                .createdAt(order.getCreatedAt())
                .build();
        orderStore.put(id, saved);
        logger.info("Order saved with id: {}", id);
        return saved;
    }

    @Override
    public void updateStatus(int orderId, OrderStatus status) {
        orderStore.compute(orderId, (id, existing) -> {
            if (existing == null) throw new IllegalArgumentException("Order not found");
            return buildUpdatedOrder(existing, status, null, null, null);
        });
    }

    @Override
    public void updatePaymentStatus(int orderId, boolean isPaid) {
        orderStore.compute(orderId, (id, existing) -> {
            if (existing == null) throw new IllegalArgumentException("Order not found");
            return buildUpdatedOrder(existing, null, isPaid, null, null);
        });
    }

    @Override
    public void updatePaymentMode(int orderId, String paymentMode) {
        String normalized = normalize(paymentMode);
        orderStore.compute(orderId, (id, existing) -> {
            if (existing == null) throw new IllegalArgumentException("Order not found");
            return buildUpdatedOrder(existing, null, null, normalized, null);
        });
    }

    @Override
    public void updateTotalPrice(int orderId, double totalPrice) {
        if (totalPrice <= 0) {
            throw new IllegalArgumentException("Invalid price");
        }
        orderStore.compute(orderId, (id, existing) -> {
            if (existing == null) throw new IllegalArgumentException("Order not found");
            return buildUpdatedOrder(existing, null, null, null, BigDecimal.valueOf(totalPrice));
        });
    }

    private List<FoodOrder> paginate(List<FoodOrder> list, PageRequest pageRequest) {
        int page = pageRequest.getPage();
        int size = pageRequest.getSize();
        int start = page * size;
        if (start >= list.size()) {
            return List.of();
        }
        int end = Math.min(start + size, list.size());
        return List.copyOf(list.subList(start, end));
    }

    private String normalize(String input) {
        if (input == null || input.isBlank()) {
            throw new IllegalArgumentException("Invalid input");
        }
        return input.trim().toLowerCase();
    }

    private void validateId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Id must be greater than 0");
        }
    }

    private void validatePage(PageRequest pageRequest) {
        if (pageRequest.getPage() < 0 || pageRequest.getSize() <= 0) {
            throw new IllegalArgumentException("Invalid page or size");
        }
    }

    private OrderStatus parseStatus(String status) {
        try {
            return OrderStatus.valueOf(status.toUpperCase());
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid restaurant status: " + status);
        }
    }

    private FoodOrder buildUpdatedOrder(FoodOrder existing, OrderStatus status, Boolean isPaid, String paymentMode, BigDecimal price) {
        return new FoodOrder.Builder()
                .id(existing.getId())
                .userId(existing.getUserId())
                .orderStatus(status != null ? status : existing.getStatus())
                .totalPrice(price != null ? price : existing.getTotalPrice())
                .orderDateTime(existing.getOrderDateTime())
                .payment(new Payment(isPaid != null ? isPaid : existing.getPayment().isPaid(), paymentMode != null ? PaymentMode.valueOf(paymentMode).name() : existing.getPayment().getPaymentMode().name()))
                .createdAt(existing.getCreatedAt())
                .build();
    }
}
