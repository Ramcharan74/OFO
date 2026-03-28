package com.ram.foodapp.repository.implementation;

import com.ram.foodapp.config.DBConnection;
import com.ram.foodapp.dto.request.PageRequest;
import com.ram.foodapp.model.foodorder.AuditInfo;
import com.ram.foodapp.model.foodorder.FoodOrder;
import com.ram.foodapp.enums.OrderStatus;
import com.ram.foodapp.model.foodorder.Payment;
import com.ram.foodapp.repository.OrderReadRepository;
import com.ram.foodapp.repository.OrderWriteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OrderRepositoryImpl implements OrderReadRepository, OrderWriteRepository {
    private static final Logger logger = LoggerFactory.getLogger(OrderRepositoryImpl.class);
    private static final String BASE_QUERY = "SELECT * FROM FOOD_ORDER ";
    private static final String FIND_ALL = BASE_QUERY + "ORDER BY CREATED_AT DESC LIMIT ? OFFSET ?";
    private static final String FIND_BY_USERID = BASE_QUERY + "WHERE USER_ID = ? ORDER BY CREATED_AT DESC LIMIT ? OFFSET ?";
    private static final String FIND_BY_ID = BASE_QUERY + "WHERE ID = ?";
    private static final String FIND_BY_DATE = BASE_QUERY + "WHERE DATE(ORDER_DATETIME) = ? ORDER BY ORDER_DATETIME DESC";
    private static final String FIND_BY_STATUS = BASE_QUERY + "WHERE STATUS = ? ORDER BY CREATED_AT DESC LIMIT ? OFFSET ?";
    private static final String FIND_BY_PAYMENT_MODE = BASE_QUERY + "WHERE PAYMENT_MODE = ? ORDER BY CREATED_AT DESC LIMIT ? OFFSET ?";
    private static final String FIND_BY_IS_PAID_STATUS = BASE_QUERY + "WHERE IS_PAID = ?";
    private static final String INSERT_FOOD_ORDER = "INSERT INTO FOOD_ORDER (USER_ID,STATUS,TOTAL_PRICE,IS_PAID,PAYMENT_MODE) VALUES (?,?,?,?,?)";
    private static final String UPDATE_STATUS = "UPDATE FOOD_ORDER SET STATUS = ? WHERE ID = ?";
    private static final String UPDATE_PAYMENT_STATUS = "UPDATE FOOD_ORDER SET IS_PAID = ?  WHERE ID = ?";
    private static final String UPDATE_PAYMENT_MODE = "UPDATE FOOD_ORDER SET PAYMENT_MODE = ?  WHERE ID = ?";
    private static final String UPDATE_TOTAL_PRICE = "UPDATE FOOD_ORDER SET TOTAL_PRICE = ? WHERE ID = ?";

    @Override
    public List<FoodOrder> findAll(PageRequest pageRequest){
        List<FoodOrder> foodOrderList = new ArrayList<>();
        try(Connection conn = DBConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(FIND_ALL)){
            ps.setInt(1,pageRequest.getSize());
            ps.setInt(2,pageRequest.getPage());
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                foodOrderList.add(mapToFoodOrder(rs));
            }
            return foodOrderList;
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<FoodOrder> findByUserId(int userId,PageRequest pageRequest){
        List<FoodOrder> foodOrderList = new ArrayList<>();
        try(Connection conn = DBConnection.getConnection()){
            PreparedStatement pstmt = conn.prepareStatement(FIND_BY_USERID);
            pstmt.setInt(1,userId);
            pstmt.setInt(2,pageRequest.getSize());
            pstmt.setInt(3,pageRequest.getPage());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()){
                foodOrderList.add(mapToFoodOrder(rs));
            }
            return foodOrderList;
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Optional<FoodOrder> findById(int id){
        try(Connection conn = DBConnection.getConnection()){
            PreparedStatement ps = conn.prepareStatement(FIND_BY_ID);
            ps.setInt(1,id);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                return Optional.of(mapToFoodOrder(rs));
            }
            return null;
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<FoodOrder> findByDate(Date date){
        List<FoodOrder> foodOrderList = new ArrayList<>();
        try(Connection conn = DBConnection.getConnection()){
            PreparedStatement ps = conn.prepareStatement(FIND_BY_DATE);
            ps.setDate(1,new java.sql.Date(date.getTime()));
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                foodOrderList.add(mapToFoodOrder(rs));
            }
            return foodOrderList;
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<FoodOrder> findByStatus(String status,PageRequest pageRequest){
        List<FoodOrder> foodOrderList = new ArrayList<>();
        try(Connection conn = DBConnection.getConnection()){
            PreparedStatement ps = conn.prepareStatement(FIND_BY_STATUS);
            ps.setString(1,status);
            ps.setInt(2,pageRequest.getSize());
            ps.setInt(3,pageRequest.getPage());
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                foodOrderList.add(mapToFoodOrder(rs));
            }
            return foodOrderList;
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<FoodOrder> findByPaymentMode(String paymentMode,PageRequest pageRequest){
        List<FoodOrder> foodOrderList = new ArrayList<>();
        try(Connection conn = DBConnection.getConnection()){
            PreparedStatement ps = conn.prepareStatement(FIND_BY_PAYMENT_MODE);
            ps.setString(1,paymentMode);
            ps.setInt(2,pageRequest.getSize());
            ps.setInt(3,pageRequest.getPage());
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                foodOrderList.add(mapToFoodOrder(rs));
            }
            return foodOrderList;
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<FoodOrder> findByPaidStatus(Boolean isPaid){
        List<FoodOrder> foodOrderList = new ArrayList<>();
        try(Connection conn = DBConnection.getConnection()){
            PreparedStatement ps = conn.prepareStatement(FIND_BY_IS_PAID_STATUS);
            ps.setBoolean(1,isPaid);
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                foodOrderList.add(mapToFoodOrder(rs));
            }
            return foodOrderList;
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public FoodOrder save(FoodOrder foodOrder){
        try(Connection connection = DBConnection.getConnection()){
            PreparedStatement pstmt = connection.prepareStatement(INSERT_FOOD_ORDER, Statement.RETURN_GENERATED_KEYS);
            pstmt.setInt(1,foodOrder.getUserId());
            pstmt.setString(2, foodOrder.getStatus().name());
            pstmt.setBigDecimal(3,foodOrder.getTotalPrice());
            pstmt.setBoolean(4,foodOrder.getPayment().isPaid());
            pstmt.setString(5,foodOrder.getPayment().getPaymentMode().name());
            pstmt.executeQuery();
            try(ResultSet rs = pstmt.getGeneratedKeys()){
                if(rs.next()){
                    int generated = rs.getInt(1);
                    FoodOrder savedFoodOrder = new FoodOrder.Builder()
                            .id(generated)
                            .orderStatus(foodOrder.getStatus())
                            .totalPrice(foodOrder.getTotalPrice())
                            .orderDateTime(foodOrder.getOrderDateTime())
                            .payment(foodOrder.getPayment())
                            .createdAt(foodOrder.getCreatedAt())
                            .build();
                    return savedFoodOrder;
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void updateStatus(int orderId, OrderStatus status) {

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_STATUS)) {

            ps.setString(1, status.name());
            ps.setInt(2, orderId);

            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("No order found with id=" + orderId);
            }

        } catch (SQLException e) {
            logger.error("DB Error while updating order status: id={}, status={}", orderId, status, e);
            throw new RuntimeException("Failed to update order status for id=" + orderId, e);
        }
    }


    @Override
    public void updatePaymentStatus(int orderId, boolean isPaid) {

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_PAYMENT_STATUS)) {

            ps.setBoolean(1, isPaid);
            ps.setInt(2, orderId);

            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("No order found with id=" + orderId);
            }

        } catch (SQLException e) {
            logger.error("DB Error while updating payment status: id={}, isPaid={}", orderId, isPaid, e);
            throw new RuntimeException("Failed to update payment status for id=" + orderId, e);
        }
    }


    @Override
    public void updatePaymentMode(int orderId, String paymentMode) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_PAYMENT_MODE)) {
            ps.setString(1, paymentMode);
            ps.setInt(2, orderId);
            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("No order found with id=" + orderId);
            }
        } catch (SQLException e) {
            logger.error("DB Error while updating payment mode: id={}, mode={}", orderId, paymentMode, e);
            throw new RuntimeException("Failed to update payment mode for id=" + orderId, e);
        }
    }

    @Override
    public void updateTotalPrice(int orderId, double totalPrice) {

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_TOTAL_PRICE)) {

            ps.setDouble(1, totalPrice);
            ps.setInt(2, orderId);

            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("No order found with id=" + orderId);
            }

        } catch (SQLException e) {
            logger.error("DB Error while updating total price: id={}, price={}", orderId, totalPrice, e);
            throw new RuntimeException("Failed to update total price for id=" + orderId, e);
        }
    }

    private FoodOrder mapToFoodOrder(ResultSet rs){
        FoodOrder.Builder foodOrder = new FoodOrder.Builder();
        try {
            foodOrder.id(rs.getInt("ID"));
            foodOrder.userId(rs.getInt("USER_ID"));
            foodOrder.orderStatus(OrderStatus.valueOf(rs.getString("STATUS")));
            foodOrder.totalPrice(rs.getBigDecimal("TOTAL_PRICE"));
            foodOrder.orderDateTime(rs.getTimestamp("ORDER_DATETIME").toLocalDateTime());
            foodOrder.payment(new Payment(rs.getBoolean("IS_PAID"),rs.getString("PAYMENT_MODE")));
            foodOrder.createdAt(new AuditInfo(rs.getTimestamp("CREATED_AT").toLocalDateTime()));
            return foodOrder.build();
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }
}
