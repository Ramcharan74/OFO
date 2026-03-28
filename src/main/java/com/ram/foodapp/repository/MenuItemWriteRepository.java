package com.ram.foodapp.repository;

import com.ram.foodapp.model.menuitem.MenuItem;

import java.util.List;

public interface MenuItemWriteRepository {

    MenuItem save(MenuItem menuItem);

    List<MenuItem> saveAll(List<MenuItem> menuItems);

    void updateAvailability(int menuItemId, boolean isAvailable);

    void updatePrice(int menuItemId, double price);

    void updateQuantity(int menuItemId, int quantity);

    void deleteById(int id);
}