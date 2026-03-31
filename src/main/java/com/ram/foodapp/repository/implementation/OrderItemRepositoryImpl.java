package com.ram.foodapp.repository.implementation;

import com.ram.foodapp.config.DBConnection;
import com.ram.foodapp.dto.request.PageRequest;
import com.ram.foodapp.model.orderitem.OrderItem;
import com.ram.foodapp.repository.OrderItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class OrderItemRepositoryImpl implements OrderItemRepository {
    private static final Logger logger = LoggerFactory.getLogger(OrderItemRepositoryImpl.class);
    private static final String ALL_ORDER_ITEMS = "SELECT * FROM ORDER_ITEM ORDER BY ID LIMIT ? OFFSET ?";
    private static final String ORDER_ITEMS_BY_ORDER_ID = "SELECT * FROM ORDER_ITEM WHERE ORDER_ID = ?";
    private static final String ORDER_ITEMS_BY_ID = "SELECT * FROM ORDER_ITEM WHERE ID = ?";
    private static final String ORDER_ITEM_BY_MENU_ITEM_ID = "SELECT * FROM ODER_ITEM WHERE MENU_ITEM_ID = ? ORDER BY ID LIMIT ? OFFSET ?";
    private static final String INSERT_ORDER_ITEM = "INSERT INTO ORDER_ITEM (ORDER_ID,UNIT_PRICE,QUANTITY,ITEM_NAME) VALUES (?,?,?,?)";
    private static final String DELETE_BY_ID = "DELETE FROM ORDER_ITEM WHERE ID = ?";
    private static final String UPDATE_ORDER_ITEM = "UPDATE ORDER_ITEM SET UNIT_PRICE = ?, QUANTITY = ?, ITEM_NAME = ?, ITEM_DESCRIPTION = ?, RESTAURANT_NAME = ? WHERE ID = ?";

    @Override
    public OrderItem save(OrderItem orderItem) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT_ORDER_ITEM, Statement.RETURN_GENERATED_KEYS);) {
            ps.setInt(1, orderItem.getOrderId());
            ps.setBigDecimal(2, orderItem.getUnitPrice());
            ps.setInt(3, orderItem.getQuantity());
            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Restaurant creation failed, no rows affected.");
            }
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int generated = rs.getInt(1);
                    OrderItem savedOrderItem = new OrderItem(generated, orderItem.getOrderId(), orderItem.getUnitPrice(), orderItem.getQuantity(), orderItem.getItemName(), orderItem.getItemDescription(), orderItem.getRestaurantName());
                    return savedOrderItem;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public OrderItem update(OrderItem orderItem) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_ORDER_ITEM)) {
            ps.setBigDecimal(1, orderItem.getUnitPrice());
            ps.setInt(2, orderItem.getQuantity());
            ps.setString(3, orderItem.getItemName());
            ps.setString(4, orderItem.getItemDescription());
            ps.setString(5, orderItem.getRestaurantName());
            ps.setInt(6, orderItem.getId());
            int rowsUpdated = ps.executeUpdate();
            if (rowsUpdated == 0) {
                throw new RuntimeException("No OrderItem found with id: " + orderItem.getId());
            }
            return orderItem;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating OrderItem", e);
        }
    }

    @Override
    public List<OrderItem> saveAll(List<OrderItem> orderItems) {
        if (orderItems == null || orderItems.isEmpty()) {
            return new ArrayList<>();
        }
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT_ORDER_ITEM)) {
            conn.setAutoCommit(false);
            for (OrderItem item : orderItems) {
                ps.setInt(1, item.getOrderId());
                ps.setBigDecimal(3, item.getUnitPrice());
                ps.setInt(4, item.getQuantity());
                ps.addBatch();
            }
            ps.executeBatch();
            conn.commit();
            return orderItems;
        } catch (SQLException e) {
            logger.error("DB Error while saving order items batch", e);
            throw new RuntimeException("Failed to save order items", e);
        }
    }

    @Override
    public void deleteByOrderId(int orderId) {

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(DELETE_BY_ID)) {

            ps.setInt(1, orderId);

            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("No order items found for orderId=" + orderId);
            }

        } catch (SQLException e) {
            logger.error("DB Error while deleting order items by orderId={}", orderId, e);
            throw new RuntimeException("Failed to delete order items for orderId=" + orderId, e);
        }
    }

    @Override
    public Optional<OrderItem> findById(int id) {
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement pst = conn.prepareStatement(ORDER_ITEMS_BY_ID);
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return Optional.of(mapToOrderItem(rs));
            }
            return Optional.empty();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public List<OrderItem> findByOrderId(int orderId) {
        List<OrderItem> orderItemList = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement pst = conn.prepareStatement(ORDER_ITEMS_BY_ORDER_ID);
            pst.setInt(1, orderId);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                orderItemList.add(mapToOrderItem(rs));
            }
            return orderItemList;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<OrderItem> findAll(PageRequest pageRequest) {
        List<OrderItem> orderItemList = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(ALL_ORDER_ITEMS)) {
            ps.setInt(1, pageRequest.getSize());
            ps.setInt(2, pageRequest.getPage());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                orderItemList.add(mapToOrderItem(rs));
            }
            return orderItemList;
        } catch (SQLException e) {
            throw new RuntimeException("Error while fetching All OrderItems", e);
        }
    }

    @Override
    public List<OrderItem> findByMenuId(int menuId, PageRequest pageRequest) {
        List<OrderItem> orderItemList = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement pst = conn.prepareStatement(ORDER_ITEM_BY_MENU_ITEM_ID);
            pst.setInt(1, menuId);
            pst.setInt(2, pageRequest.getSize());
            pst.setInt(3, pageRequest.getPage());
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                orderItemList.add(mapToOrderItem(rs));
            }
            return orderItemList;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private OrderItem mapToOrderItem(ResultSet rs) {
        try {
            return new OrderItem(rs.getInt("ID"), rs.getInt("ORDER_ID"), rs.getBigDecimal("UNIT_PRICE"), rs.getInt("QUANTITY"), rs.getString("ITEM_NAME"), rs.getString("ITEM_DESCRIPTION"), rs.getString("RESTAURANT_NAME"));
        } catch (SQLException e) {
            throw new RuntimeException("Error while converting resultset to OrderItem Object", e);
        }
    }
}
