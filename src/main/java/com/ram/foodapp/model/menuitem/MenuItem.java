package com.ram.foodapp.model.menuitem;

import java.util.Objects;


public class MenuItem {
    private final Integer id;
    private final Integer restaurantId;
    private final MenuDetails menuDetails;
    private final Category category;
    private final String imageUrl;
    private final Inventory inventory;


    public MenuItem(Builder builder) {
        this.id = builder.id;
        this.restaurantId = builder.restaurantId;
        this.menuDetails = builder.menuDetails;
        this.category = builder.category;
        this.imageUrl = builder.imageUrl;
        this.inventory = builder.inventory;
    }


    public static class Builder {
        private Integer id;
        private Integer restaurantId;
        private MenuDetails menuDetails;
        private Category category;
        private String imageUrl;
        private Inventory inventory;

        public Builder id(Integer id){
            this.id = id;
            return this;
        }

        public Builder restaurantId(Integer restaurantId){
            this.restaurantId = restaurantId;
            return this;
        }

        public Builder menuDetails(MenuDetails menuDetails){
            this.menuDetails = menuDetails;
            return this;
        }

        public Builder category(Category category){
            this.category = category;
            return this;
        }

        public Builder imageUrl(String imageUrl){
            this.imageUrl = imageUrl;
            return this;
        }

        public Builder inventory(Inventory inventory){
            this.inventory = inventory;
            return this;
        }

        public MenuItem build(){
            Objects.requireNonNull(menuDetails,"MenuDeatails cannot be null");
            Objects.requireNonNull(inventory,"Inventory cannot be null");
            Objects.requireNonNull(category,"Category cannot be null");
            if (imageUrl != null && imageUrl.isBlank()) {
                throw new IllegalArgumentException("Image URL cannot be blank");
            }
            if(restaurantId == null || restaurantId <= 0) throw new IllegalArgumentException("Restaurant Id cannot be null or less than 0");
            return new MenuItem(this);
        }
    }

    public Integer getId() {
        return id;
    }

    public Integer getRestaurantId() {
        return restaurantId;
    }

    public MenuDetails getMenuDetails() {
        return menuDetails;
    }

    public Category getCategory() {
        return category;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Inventory getInventory() {
        return inventory;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MenuItem)) return false;
        MenuItem that = (MenuItem) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
