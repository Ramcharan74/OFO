package com.ram.foodapp.model.restaurant;

import com.ram.foodapp.enums.RestaurantStatus;


import java.util.Objects;

public class Restaurant {

    private final Integer id;
    private final int userId;
    private final RestaurantStatus status;
    private final RestaurantDetails restaurantDetails;

    private Restaurant(Builder builder) {
        this.id = builder.id;
        this.userId = builder.userId;
        this.status = builder.status;
        this.restaurantDetails = builder.restaurantDetails;
    }

    public static class Builder {
        private Integer id;
        private int userId;
        private RestaurantStatus status;
        private RestaurantDetails restaurantDetails;

        public Builder id(Integer id) {
            this.id = id;
            return this;
        }

        public Builder userId(int userId) {
            this.userId = userId;
            return this;
        }

        public Builder status(RestaurantStatus status) {
            this.status = status;
            return this;
        }

        public Builder restaurantDetails(RestaurantDetails restaurantDetails) {
            this.restaurantDetails = restaurantDetails;
            return this;
        }

        public Restaurant build() {
            if (userId <= 0) {
                throw new IllegalArgumentException("UserId must be greater than 0");
            }

            if (status == null) {
                status = RestaurantStatus.CLOSE;
            }

            Objects.requireNonNull(restaurantDetails, "RestaurantDetails cannot be null");

            return new Restaurant(this);
        }
    }

    public Integer getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public RestaurantStatus getStatus() {
        return status;
    }

    public RestaurantDetails getRestaurantDetails() {
        return restaurantDetails;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Restaurant)) return false;
        Restaurant that = (Restaurant) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Restaurant{id=" + id +
                ", userId=" + userId +
                ", status=" + status +
                ", name=" + restaurantDetails.getName() +
                '}';
    }
}
