package com.ram.foodapp.model.menuitem;

import com.ram.foodapp.enums.FoodType;

import java.util.Objects;

public class Category {
    private final FoodType foodType;
    private final String subCategory;

    public Category(FoodType foodType, String subCategory) {
        Objects.requireNonNull(foodType,"Food type should not be null");
        this.foodType = foodType;
        this.subCategory = subCategory;
    }

    public FoodType getFoodType() {
        return foodType;
    }

    public String getSubCategory() {
        return subCategory;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return foodType == category.foodType && Objects.equals(subCategory, category.subCategory);
    }
}
