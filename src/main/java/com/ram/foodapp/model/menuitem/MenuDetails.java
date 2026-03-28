package com.ram.foodapp.model.menuitem;

import java.math.BigDecimal;
import java.util.Objects;

public class MenuDetails {

    private static final int MAX_NAME_LENGTH = 30;
    private static final int MAX_DESC_LENGTH = 150;

    private final String name;
    private final String description;
    private final BigDecimal price;

    public MenuDetails(String name, String description, BigDecimal price) {

        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Name cannot be null or blank");

        name = name.trim();

        if (name.length() > MAX_NAME_LENGTH)
            throw new IllegalArgumentException("Name cannot exceed " + MAX_NAME_LENGTH + " characters");

        if (description != null && description.length() > MAX_DESC_LENGTH)
            throw new IllegalArgumentException("Description cannot exceed " + MAX_DESC_LENGTH + " characters");

        if (price == null || price.signum() <= 0)
            throw new IllegalArgumentException("Price must be greater than zero");

        this.name = name;
        this.description = (description == null || description.isBlank()) ? null : description.trim();
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MenuDetails)) return false;
        MenuDetails that = (MenuDetails) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(description, that.description) &&
                Objects.equals(price, that.price);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, price);
    }

    @Override
    public String toString() {
        return "MenuDetails{name='" + name + "', price=" + price + '}';
    }
}
