package com.ram.foodapp.model.foodorder;

import com.ram.foodapp.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;


public class FoodOrder {
    private final Integer id;
    private final int userId;
    private final OrderStatus status;
    private final BigDecimal totalPrice;
    private final LocalDateTime orderDateTime;
    private final Payment payment;
    private final AuditInfo createdAt;

    FoodOrder(Builder builder) {
        this.id = builder.id;
        this.userId = builder.userId;
        this.status = builder.status;
        this.totalPrice = builder.totalPrice;
        this.orderDateTime = builder.orderDateTime;
        this.payment = builder.payment;
        this.createdAt = builder.createdAt;
    }

    public Integer getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public LocalDateTime getOrderDateTime() {
        return orderDateTime;
    }

    public Payment getPayment() {
        return payment;
    }

    public AuditInfo getCreatedAt() {
        return createdAt;
    }

    public static class Builder {
        private Integer id;
        private int userId;
        private OrderStatus status;
        private BigDecimal totalPrice;
        private LocalDateTime orderDateTime;
        private Payment payment;
        private AuditInfo createdAt;

        public Builder id(int id) {
            this.id = id;
            return this;
        }

        public Builder userId(int userId) {
            this.userId = userId;
            return this;
        }

        public Builder orderStatus(OrderStatus status) {
            this.status = status;
            return this;
        }

        public Builder totalPrice(BigDecimal totalPrice) {
            this.totalPrice = totalPrice;
            return this;
        }

        public Builder orderDateTime(LocalDateTime orderDateTime) {
            this.orderDateTime = orderDateTime;
            return this;
        }

        public Builder payment(Payment payment) {
            this.payment = payment;
            return this;
        }

        public Builder createdAt(AuditInfo createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public FoodOrder build() {
            if (userId == 0) throw new IllegalStateException("userId cannot be null");
            status = (status != null) ? status : OrderStatus.CREATED;
            orderDateTime = (orderDateTime != null) ? orderDateTime : LocalDateTime.now();
            if (totalPrice == null || totalPrice.signum() <= 0) {
                throw new IllegalStateException("Total price must be greater than zero");
            }
            Objects.requireNonNull(payment, "Payment cannot be null");
            return new FoodOrder(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FoodOrder foodOrder = (FoodOrder) o;
        return id == foodOrder.id;
    }


    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
