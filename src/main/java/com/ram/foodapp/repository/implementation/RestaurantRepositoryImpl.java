package com.ram.foodapp.repository.implementation;

import com.ram.foodapp.config.DBConnection;
import com.ram.foodapp.dto.request.PageRequest;
import com.ram.foodapp.enums.RestaurantStatus;
import com.ram.foodapp.model.restaurant.Restaurant;
import com.ram.foodapp.model.restaurant.RestaurantDetails;
import com.ram.foodapp.repository.RestaurantRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@Primary
public class RestaurantRepositoryImpl implements RestaurantRepository {
    private static final Logger logger = LoggerFactory.getLogger(RestaurantRepositoryImpl.class);
    private static final String SAVE_RESTAURANT = "INSERT INTO RESTAURANT (USER_ID,NAME,ADDRESS_ID,STATUS,RATING,RATING_COUNT) VALUES (?,?,?,?,?,?)";
    private static final String FIND_ALL = "SELECT * FROM RESTAURANT LIMIT ? OFFSET ?";
    private static final String RESTAURANT_BY_USER_ID = "SELECT * FROM RESTAURANT WHERE USER_ID = ?";
    private static final String RESTAURANT_BY_ADDRESS_ID = "SELECT * FROM RESTAURANT WHERE ADDRESS_ID = ?";
    private static final String RESTAURANT_BY_ID = "SELECT * FROM RESTAURANT WHERE ID = ?";
    private static final String UPDATE_STATUS = "UPDATE RESTAURANT SET STATUS = ? WHERE ID = ? ";
    private static final String UPDATE_RATING = "UPDATE RESTAURANT SET RATING = ?,RATING_COUNT = ? WHERE ID = ?";
    private static final String DELETE_BY_ID = "DELETE FROM RESTAURANT WHERE ID = ?";
    private static final String RESTAURANT_BY_STATUS = "SELECT * FROM RESTAURANT WHERE STATUS = ? ORDER BY ID LIMIT ? OFFSET ?";

    @Override
    public Restaurant save(Restaurant restaurant) {
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(SAVE_RESTAURANT, Statement.RETURN_GENERATED_KEYS);
            pstmt.setInt(1, restaurant.getUserId());
            pstmt.setString(2, restaurant.getRestaurantDetails().getName());
            pstmt.setInt(3, restaurant.getRestaurantDetails().getAddressId());
            pstmt.setString(4, restaurant.getStatus().name());
            pstmt.setDouble(5, restaurant.getRestaurantDetails().getRating());
            pstmt.setInt(6, restaurant.getRestaurantDetails().getRatingCount());
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Restaurant creation failed, no rows affected.");
            }
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int generated = rs.getInt(1);
                    Restaurant savedRestaurant = new Restaurant.Builder()
                            .id(generated)
                            .restaurantDetails(restaurant.getRestaurantDetails())
                            .userId(restaurant.getUserId())
                            .status(restaurant.getStatus())
                            .build();
                    return savedRestaurant;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    public List<Restaurant> findAll(PageRequest pageRequest) {
        List<Restaurant> restaurantList = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(FIND_ALL);
            ps.setInt(1, pageRequest.getSize());
            ps.setInt(2, pageRequest.getPage());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    restaurantList.add(mapToRestaurant(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return restaurantList;
    }

    @Override
    public List<Restaurant> findByStatus(String status, PageRequest pageRequest) {
        List<Restaurant> restaurantList = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(RESTAURANT_BY_STATUS)) {
            ps.setString(1, status);
            ps.setInt(2, pageRequest.getSize());
            ps.setInt(3, pageRequest.getPage());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    restaurantList.add(mapToRestaurant(rs));
                }
                return restaurantList;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Restaurant> findByUserId(int userId) {
        List<Restaurant> restaurantList = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(RESTAURANT_BY_USER_ID);
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Restaurant restaurant = mapToRestaurant(rs);
                restaurantList.add(restaurant);
            }
            return restaurantList;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Restaurant> findByAddressId(int addressId) {
        List<Restaurant> restaurantList = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection()) {
            PreparedStatement pstmt = connection.prepareStatement(RESTAURANT_BY_ADDRESS_ID);
            pstmt.setInt(1, addressId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                restaurantList.add(mapToRestaurant(rs));
            }
            return restaurantList;
        } catch (SQLException e) {
            throw new RuntimeException("Error while fetching Restaurant by address Id ", e);
        }
    }

    public Optional<Restaurant> findById(int id) {
        try (Connection connection = DBConnection.getConnection()) {
            PreparedStatement pstmt = connection.prepareStatement(RESTAURANT_BY_ID);
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapToRestaurant(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void updateStatus(int restaurantId, String status) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_STATUS)) {
            ps.setString(1, status);
            ps.setInt(2, restaurantId);
            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException(
                        "No rows updated. Restaurant may not exist. restaurantId=" + restaurantId
                );
            }
        } catch (Exception e) {
            logger.error("DB Error while updating status: restaurantId="
                    + restaurantId + ", status=" + status);
            throw new RuntimeException(
                    "Failed to update restaurant status for id=" + restaurantId, e
            );
        }
    }

    @Override
    public void updateRating(int restaurantId, double rating, int ratingCount) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_RATING)) {
            ps.setDouble(1, rating);
            ps.setInt(2, ratingCount);
            ps.setInt(3, restaurantId);
            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException(
                        "No rows updated. Restaurant may not exist. restaurantId=" + restaurantId
                );
            }
        } catch (Exception e) {
            logger.error("DB Error while updating rating: restaurantId="
                    + restaurantId + ", rating=" + rating + ",ratingCount=" + ratingCount);
            throw new RuntimeException(
                    "Failed to update restaurant status for id=" + restaurantId, e
            );
        }
    }

    @Override
    public void deleteById(int id) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(DELETE_BY_ID)) {
            ps.setInt(1, id);
            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException(
                        "No rows updated. Restaurant may not exist. restaurantId=" + id
                );
            }
        } catch (Exception e) {
            logger.error("DB Error while Deleting Restaurant: restaurantId="
                    + id);
            throw new RuntimeException(
                    "Failed to delete restaurant for id=" + id, e
            );
        }
    }


    public Restaurant mapToRestaurant(ResultSet rs) {
        Restaurant.Builder restaurant = new Restaurant.Builder();
        try {
            restaurant.id(rs.getInt("ID"));
            restaurant.userId(rs.getInt("USER_ID"));
            restaurant.restaurantDetails(new RestaurantDetails(rs.getString("NAME"), rs.getInt("ADDRESS_ID"), rs.getDouble("RATING"), rs.getInt("RATING_COUNT")));
            restaurant.status(RestaurantStatus.valueOf(rs.getString("STATUS")));
            return restaurant.build();
        } catch (SQLException e) {
            throw new RuntimeException("Error while converting Restaurant Object :", e);
        }
    }
}
