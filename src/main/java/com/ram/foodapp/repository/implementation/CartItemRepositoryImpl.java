package com.ram.foodapp.repository.implementation;

import com.ram.foodapp.config.DBConnection;
import com.ram.foodapp.dto.request.PageRequest;
import com.ram.foodapp.model.cartitem.CartItem;
import com.ram.foodapp.repository.CartItemReadRepository;
import com.ram.foodapp.repository.CartItemWriteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class CartItemRepositoryImpl implements CartItemReadRepository, CartItemWriteRepository {
    private static final Logger logger = LoggerFactory.getLogger(CartItemRepositoryImpl.class);
    private static final String BASE_QUERY = "SELECT * FROM CART_ITEM";
    private static final String ALL_CART_ITEMS = BASE_QUERY + "ORDER BY ID LIMIT ? OFFSET ?";
    private static final String USER_CART_ITEMS = "SELECT * FROM CART_ITEM WHERE USER_ID = ?";
    private static final String CART_ITEM_BY_MENU_ITEM_ID = "SELECT * FROM CART_ITEM WHERE MENU_ITEM_ID = ?";
    private static final String CART_ITEM_BY_MENU_AND_USER_ID = "SELECT * FROM CART_ITEM WHERE MENU_ITEM_ID = ? AND USER_ID = ?";
    private static final String EXISTS_BY_USER_AND_MENU_ITEM = "SELECT 1 FROM CART_ITEM WHERE USER_ID = ? AND MENU_ITEM_ID = ? LIMIT 1";
    //write operation queries
    private static final String INSERT_CART_ITEM = "INSERT INTO CART_ITEM (USER_ID,MENU_ITEM_ID,QUANTITY,ITEM_NAME,UNIT_PRICE,RESTAURANT_NAME) VALUES (?,?,?,?,?)";
    private static final String UPDATE_QUANTITY = "UPDATE CART_ITEM SET QUANTITY = ? WHERE USER_ID = ? AND MENU_ITEM_ID = ?";
    private static final String INCREMENT_QUANTITY = "UPDATE CART_ITEM SET QUANTITY = QUANTITY + ? WHERE USER_ID = ? AND MENU_ITEM_ID = ?";
    private static final String DELETE_BY_USER_AND_MENU_ITEM = "DELETE FROM CART_ITEM WHERE USER_ID = ? AND MENU_ITEM_ID = ?";
    private static final String CLEAR_CART = "DELETE FROM CART_ITEM WHERE USER_ID = ?";
    @Override
    public CartItem save(CartItem cartItem){
        try(Connection connection = DBConnection.getConnection()){
            PreparedStatement pstmt = connection.prepareStatement(INSERT_CART_ITEM, Statement.RETURN_GENERATED_KEYS);
            pstmt.setInt(1,cartItem.getUserId());
            pstmt.setInt(2,cartItem.getMenuItemId());
            pstmt.setInt(3,cartItem.getQuantity());
            pstmt.setString(4,cartItem.getItemName());
            pstmt.setBigDecimal(5,cartItem.getUnitPrice());
            pstmt.setString(6,cartItem.getRestaurantName());
            int rows = pstmt.executeUpdate();
            if (rows == 0) {
                throw new RuntimeException("Failed to insert cart item");
            }
            return cartItem;
        }catch (SQLException e){
            throw new RuntimeException("DB error while saving cart item", e);
        }
    }

    @Override
    public List<CartItem> findAll(PageRequest pageRequest){
        List<CartItem> cartItemList = new ArrayList<>();
        try(Connection conn = DBConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(ALL_CART_ITEMS)){
            ps.setInt(1,pageRequest.getSize());
            ps.setInt(2,pageRequest.getPage());
            try(ResultSet rs = ps.executeQuery()){
                while(rs.next()){
                    cartItemList.add(mapToCartItem(rs));
                }
            }
            return cartItemList;
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<CartItem> findByUserId(int userId){
        List<CartItem> cartItemList = new ArrayList<>();
        try(Connection conn = DBConnection.getConnection()){
            PreparedStatement pstmt = conn.prepareStatement(USER_CART_ITEMS);
            pstmt.setInt(1,userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()){
                cartItemList.add(mapToCartItem(rs));
            }
            return cartItemList;
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Optional<CartItem> findByMenuId(int menuId){
        try(Connection conn = DBConnection.getConnection()){
            PreparedStatement pstmt = conn.prepareStatement(CART_ITEM_BY_MENU_ITEM_ID);
            pstmt.setInt(1,menuId);
            ResultSet rs = pstmt.executeQuery();
            if(rs.next()){
                return Optional.of(mapToCartItem(rs));
            }
            return null;
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Optional<CartItem> findByUserIdAndMenuItemId(int userId, int menuId){
        try(Connection conn = DBConnection.getConnection()){
            PreparedStatement pstmt = conn.prepareStatement(CART_ITEM_BY_MENU_AND_USER_ID);
            pstmt.setInt(1,menuId);
            pstmt.setInt(1,userId);
            ResultSet rs = pstmt.executeQuery();
            if(rs.next()){
                return Optional.of(mapToCartItem(rs));
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean existsByUserIdAndMenuItemId(int userId, int menuItemId) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(EXISTS_BY_USER_AND_MENU_ITEM)) {
            ps.setInt(1, userId);
            ps.setInt(2, menuItemId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            logger.error("Error checking existence for userId={} and menuItemId={}", userId, menuItemId, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateQuantity(int userId, int menuItemId, int quantity) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_QUANTITY)) {
            ps.setInt(1, quantity);
            ps.setInt(2, userId);
            ps.setInt(3, menuItemId);

            int rows = ps.executeUpdate();

            if (rows == 0) {
                throw new RuntimeException("CartItem not found for userId=" + userId + " menuItemId=" + menuItemId);
            }

        } catch (SQLException e) {
            logger.error("Error updating quantity userId={} menuItemId={}", userId, menuItemId, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void incrementQuantity(int userId, int menuItemId, int delta) {

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(INCREMENT_QUANTITY)) {

            ps.setInt(1, delta);
            ps.setInt(2, userId);
            ps.setInt(3, menuItemId);

            int rows = ps.executeUpdate();

            if (rows == 0) {
                throw new RuntimeException("CartItem not found for userId=" + userId + " menuItemId=" + menuItemId);
            }

        } catch (SQLException e) {
            logger.error("Error incrementing quantity userId={} menuItemId={}", userId, menuItemId, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteByUserIdAndMenuItemId(int userId, int menuItemId) {

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(DELETE_BY_USER_AND_MENU_ITEM)) {
            ps.setInt(1, userId);
            ps.setInt(2, menuItemId);
            int rows = ps.executeUpdate();
            if (rows == 0) {
                throw new RuntimeException("CartItem not found for userId=" + userId + " menuItemId=" + menuItemId);
            }
        } catch (SQLException e) {
            logger.error("Error deleting cart item userId={} menuItemId={}", userId, menuItemId, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void clearCart(int userId) {

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(CLEAR_CART)) {

            ps.setInt(1, userId);

            ps.executeUpdate();

        } catch (SQLException e) {
            logger.error("Error clearing cart for userId={}", userId, e);
            throw new RuntimeException(e);
        }
    }

    public CartItem mapToCartItem(ResultSet rs){
        try{
            return new CartItem(rs.getInt("USER_ID"),rs.getInt("MENU_ITEM_ID"),rs.getInt("QUANTITY"), rs.getString("ITEM_NAME"),rs.getBigDecimal("UNIT_PRICE"), rs.getString("RESTAURANT_NAME"));
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }
}
