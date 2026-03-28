package com.ram.foodapp.model.restaurant;

import java.util.Objects;

public class RestaurantDetails {

    private static final int MAX_NAME_LENGTH = 50;

    private final String name;
    private final int addressId;
    private final double rating;
    private final int ratingCount;

    public RestaurantDetails(String name, int addressId, double rating, int ratingCount) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be null or blank");
        }
        name = name.trim();

        if (name.length() > MAX_NAME_LENGTH) {
            throw new IllegalArgumentException("Name cannot exceed " + MAX_NAME_LENGTH + " characters");
        }
        if (addressId <= 0) {
            throw new IllegalArgumentException("AddressId must be greater than 0");
        }
        if (rating < 0.0 || rating > 5.0) {
            throw new IllegalArgumentException("Rating must be between 0.0 and 5.0");
        }
        if (ratingCount < 0) {
            throw new IllegalArgumentException("Rating count cannot be negative");
        }

        this.name = name;
        this.addressId = addressId;
        this.rating = rating;
        this.ratingCount = ratingCount;
    }

    public String getName() {
        return name;
    }

    public int getAddressId() {
        return addressId;
    }

    public double getRating() {
        return rating;
    }

    public int getRatingCount() {
        return ratingCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RestaurantDetails)) return false;
        RestaurantDetails that = (RestaurantDetails) o;
        return addressId == that.addressId &&
                Double.compare(that.rating, rating) == 0 &&
                ratingCount == that.ratingCount &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, addressId, rating, ratingCount);
    }

    @Override
    public String toString() {
        return "RestaurantDetails{name='" + name + "', rating=" + rating + '}';
    }
}
