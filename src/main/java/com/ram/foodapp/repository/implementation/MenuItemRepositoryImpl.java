package com.ram.foodapp.repository.implementation;

import com.ram.foodapp.config.DBConnection;
import com.ram.foodapp.enums.FoodType;
import com.ram.foodapp.dto.request.PageRequest;
import com.ram.foodapp.exception.NotFoundException;
import com.ram.foodapp.model.menuitem.Category;
import com.ram.foodapp.model.menuitem.Inventory;
import com.ram.foodapp.model.menuitem.MenuDetails;
import com.ram.foodapp.model.menuitem.MenuItem;
import com.ram.foodapp.repository.MenuItemReadRepository;
import com.ram.foodapp.repository.MenuItemWriteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class MenuItemRepositoryImpl implements MenuItemReadRepository, MenuItemWriteRepository {
    private static final Logger logger = LoggerFactory.getLogger(MenuItemRepositoryImpl.class);
    private static final String BASE_QUERY = "SELECT * FROM MENU_ITEM";
    private static final String RESTAURANT_BASE_QUERY = BASE_QUERY + "WHERE RESTAURANT_ID = ?";
    private static final String ALL_MENU_ITEMS = BASE_QUERY + "ORDER BY ID LIMIT ? OFFSET ?";
    private static final String MENU_ITEM_BY_ID = BASE_QUERY + "WHERE ID = ?";
    private static final String EXISTS_BY_ID = "SELECT 1 FROM MENU_ITEM WHERE ID = ?";
    private static final String MENU_ITEM_BY_RESTAURANT_ID = RESTAURANT_BASE_QUERY + "ORDER BY ID LIMIT ? OFFSET ?";
    private static final String FIND_BY_IS_AVAILABLE_STATUS = BASE_QUERY + "WHERE IS_AVAILABLE = ?";
    private static final String FIND_AVAILABLE_BY_RESTAURANT = RESTAURANT_BASE_QUERY + "AND IS_AVAILABLE = 1 ORDER BY ID LIMIT ? OFFSET ?";
    private static final String FIND_BY_RESTAURANT_N_CATEGORY = RESTAURANT_BASE_QUERY + "AND CATEGORY = ? ORDER BY ID LIMIT ? OFFSET ?";
    private static final String FIND_BY_RESTAURANT_N_SUBCATEGORY = RESTAURANT_BASE_QUERY + "AND SUB_CATEGORY = ? ORDER BY ID LIMIT ? OFFSET ?";
    private static final String SEARCH_BY_NAME = RESTAURANT_BASE_QUERY + "AND NAME LIKE ? ORDER BY ID LIMIT ? OFFSET ?";
    private static final String MENU_ITEM_BY_CATEGORY = BASE_QUERY + "WHERE RESTAURANT_ID = ? AND CATEGORY = ?";
    //menuItem write operations
    private static final String INSERT_MENU_ITEM = "INSERT INTO MENU_ITEM (RESTAURANT_ID,NAME,PRICE,DESCRIPTION,CATEGORY,SUB_CATEGORY,IMAGE_URL,QUANTITY,IS_AVAILABLE) VALUES (?,?,?,?,?,?,?,?,?)";
    private static final String UPDATE_AVAILABILITY = "UPDATE MENU_ITEM SET IS_AVAILABLE = ? WHERE ID = ?";
    private static final String UPDATE_PRICE = "UPDATE MENU_ITEM SET PRICE = ? WHERE ID = ?";
    private static final String UPDATE_QUANTITY = "UPDATE MENU_ITEM SET QUANTITY = ? WHERE ID = ?";
    private static final String DELETE_BY_ID = "DELETE FROM MENU_ITEM WHERE ID = ?";
    @Override
    public MenuItem save(MenuItem menuItem) {
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(INSERT_MENU_ITEM);
            pstmt.setInt(1, menuItem.getRestaurantId());
            pstmt.setString(2, menuItem.getMenuDetails().getName());
            pstmt.setBigDecimal(3, menuItem.getMenuDetails().getPrice());
            pstmt.setString(4, menuItem.getMenuDetails().getDescription());
            pstmt.setString(5, menuItem.getCategory().getFoodType().name());
            pstmt.setString(6, menuItem.getCategory().getSubCategory());
            pstmt.setString(7, menuItem.getImageUrl());
            pstmt.setInt(8, menuItem.getInventory().getQuantity());
            pstmt.setBoolean(9, menuItem.getInventory().isAvailable());
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("MenuItem creation failed, no rows affected.");
            }
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int generated = rs.getInt(1);
                    MenuItem savedMenuItem = new MenuItem.Builder()
                            .id(generated)
                            .restaurantId(menuItem.getRestaurantId())
                            .menuDetails(menuItem.getMenuDetails())
                            .category(menuItem.getCategory())
                            .imageUrl(menuItem.getImageUrl())
                            .inventory(menuItem.getInventory())
                            .build();
                    return savedMenuItem;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Optional<MenuItem> findById(int id) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(MENU_ITEM_BY_ID)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapToMenuItem(rs));
                }
                throw new RuntimeException("MenuItem not found for id=" + id);
            }
        } catch (SQLException e) {
            logger.error("DB Error while fetching menu item by id={}", id, e);
            throw new RuntimeException("Failed to fetch menu item for id=" + id, e);
        }
    }

    public boolean existsById(int id) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(EXISTS_BY_ID)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            logger.error("DB Error while checking existence of menu item id={}", id, e);
            throw new RuntimeException("Failed to check existence for id=" + id, e);
        }
    }

    @Override
    public List<MenuItem> findAll(PageRequest pageRequest) {
        List<MenuItem> menuItemList = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(ALL_MENU_ITEMS)) {
            ps.setInt(1, pageRequest.getSize());
            ps.setInt(2, pageRequest.getPage());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    menuItemList.add(mapToMenuItem(rs));
                }
            }
            return menuItemList;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<MenuItem> findByAvailability(Boolean isAvailable) {
        List<MenuItem> menuItemList = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(FIND_BY_IS_AVAILABLE_STATUS);
            pstmt.setBoolean(1, isAvailable);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                menuItemList.add(mapToMenuItem(rs));
            }
            return menuItemList;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<MenuItem> findByCategory(FoodType foodType) {
        List<MenuItem> menuItemList = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(MENU_ITEM_BY_CATEGORY);
            pstmt.setString(1, foodType.name());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                menuItemList.add(mapToMenuItem(rs));
            }
            return menuItemList;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<MenuItem> findByRestaurantId(int restaurantId){
        List<MenuItem> menuItemList = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(RESTAURANT_BASE_QUERY);
            pstmt.setInt(1,restaurantId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                menuItemList.add(mapToMenuItem(rs));
            }
            return menuItemList;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<MenuItem> findByRestaurantId(int restaurantId, PageRequest pageRequest) {
        List<MenuItem> menuItemList = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(MENU_ITEM_BY_RESTAURANT_ID);
            pstmt.setInt(1,restaurantId);
            pstmt.setInt(2,pageRequest.getSize());
            pstmt.setInt(3,pageRequest.getPage());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                menuItemList.add(mapToMenuItem(rs));
            }
            return menuItemList;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<MenuItem> findAvailableByRestaurantId(int restaurantId, PageRequest pageRequest) {
        List<MenuItem> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_AVAILABLE_BY_RESTAURANT)) {
            ps.setInt(1, restaurantId);
            ps.setInt(2, pageRequest.getSize());
            ps.setInt(3, pageRequest.getPage());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapToMenuItem(rs));
                }
            }
            return list;
        } catch (SQLException e) {
            logger.error("Error fetching available items for restaurantId={}", restaurantId, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<MenuItem> findByCategorynRestaurant(int restaurantId, FoodType category, PageRequest pageRequest) {
        List<MenuItem> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_BY_RESTAURANT_N_CATEGORY)) {
            ps.setInt(1, restaurantId);
            ps.setString(2, category.name());
            ps.setInt(3, pageRequest.getSize());
            ps.setInt(4, pageRequest.getPage());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapToMenuItem(rs));
                }
            }
            return list;
        } catch (SQLException e) {
            logger.error("Error fetching by category for restaurantId={}", restaurantId, e);
            throw new RuntimeException(e);
        }
    }


    @Override
    public List<MenuItem> findBySubCategorynRestaurant(int restaurantId, String subCategory, PageRequest pageRequest) {
        List<MenuItem> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_BY_RESTAURANT_N_SUBCATEGORY)) {
            ps.setInt(1, restaurantId);
            ps.setString(2, subCategory);
            ps.setInt(3, pageRequest.getSize());
            ps.setInt(4, pageRequest.getPage());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapToMenuItem(rs));
                }
            }
            return list;
        } catch (SQLException e) {
            logger.error("Error fetching by subCategory for restaurantId={}", restaurantId, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<MenuItem> searchByName(int restaurantId, String name, PageRequest pageRequest) {
        List<MenuItem> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SEARCH_BY_NAME)) {
            ps.setInt(1, restaurantId);
            ps.setString(2, "%" + name + "%");
            ps.setInt(3, pageRequest.getSize());
            ps.setInt(4, pageRequest.getPage());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapToMenuItem(rs));
                }
            }
            return list;
        } catch (SQLException e) {
            logger.error("Error searching menu items by name={}", name, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<MenuItem> saveAll(List<MenuItem> menuItems) {
        List<MenuItem> savedList = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT_MENU_ITEM, Statement.RETURN_GENERATED_KEYS)) {
            for (MenuItem menuItem : menuItems) {
                ps.setInt(1, menuItem.getRestaurantId());
                ps.setString(2, menuItem.getMenuDetails().getName());
                ps.setBigDecimal(3, menuItem.getMenuDetails().getPrice());
                ps.setString(4, menuItem.getMenuDetails().getDescription());
                ps.setString(5, menuItem.getCategory().getFoodType().name());
                ps.setString(6, menuItem.getCategory().getSubCategory());
                ps.setString(7, menuItem.getImageUrl());
                ps.setInt(8, menuItem.getInventory().getQuantity());
                ps.setBoolean(9, menuItem.getInventory().isAvailable());
                ps.addBatch();
            }
            ps.executeBatch();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                int index = 0;
                while (rs.next()) {
                    MenuItem item = menuItems.get(index++);
                    savedList.add(item);
                }
            }
            return savedList;
        } catch (SQLException e) {
            logger.error("Error while saving menu items", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateAvailability(int menuItemId, boolean isAvailable) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_AVAILABILITY)) {
            ps.setBoolean(1, isAvailable);
            ps.setInt(2, menuItemId);
            int rows = ps.executeUpdate();
            if (rows == 0) {
                throw new NotFoundException("MenuItem not found with id: " + menuItemId);
            }
        } catch (SQLException e) {
            logger.error("Error updating availability for menuItemId={}", menuItemId, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updatePrice(int menuItemId, double price) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_PRICE)) {
            ps.setDouble(1, price);
            ps.setInt(2, menuItemId);
            int rows = ps.executeUpdate();
            if (rows == 0) {
                throw new NotFoundException("MenuItem not found with id: " + menuItemId);
            }
        } catch (SQLException e) {
            logger.error("Error updating price for menuItemId={}", menuItemId, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateQuantity(int menuItemId, int quantity) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_QUANTITY)) {
            ps.setInt(1, quantity);
            ps.setInt(2, menuItemId);
            int rows = ps.executeUpdate();
            if (rows == 0) {
                throw new NotFoundException("MenuItem not found with id: " + menuItemId);
            }
        } catch (SQLException e) {
            logger.error("Error updating quantity for menuItemId={}", menuItemId, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteById(int id) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(DELETE_BY_ID)) {
            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            if (rows == 0) {
                throw new RuntimeException("MenuItem not found for id=" + id);
            }
        } catch (SQLException e) {
            logger.error("Error deleting menuItem id={}", id, e);
            throw new RuntimeException(e);
        }
    }

    public MenuItem mapToMenuItem(ResultSet rs) {
        MenuItem.Builder menuItem = new MenuItem.Builder();
        try {
            menuItem.id(rs.getInt("ID"));
            menuItem.restaurantId(rs.getInt("RESTAURANT_ID"));
            menuItem.menuDetails(new MenuDetails(rs.getString("NAME"), rs.getString("DESCRIPTION"), rs.getBigDecimal("PRICE")));
            menuItem.category(new Category(FoodType.valueOf(rs.getString("CATEGORY")), rs.getString("SUB_CATEGORY")));
            menuItem.imageUrl(rs.getString("IMAGE_URL"));
            menuItem.inventory(new Inventory(rs.getInt("QUANTITY")));
            return menuItem.build();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
