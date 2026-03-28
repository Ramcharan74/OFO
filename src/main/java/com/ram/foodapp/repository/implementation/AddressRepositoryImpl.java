package com.ram.foodapp.repository.implementation;

import com.ram.foodapp.config.DBConnection;
import com.ram.foodapp.dto.request.PageRequest;
import com.ram.foodapp.exception.DataAccessException;
import com.ram.foodapp.model.address.*;
import com.ram.foodapp.repository.AddressReadRepository;
import com.ram.foodapp.repository.AddressWriteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AddressRepositoryImpl implements AddressReadRepository, AddressWriteRepository {
    private static final Logger logger = LoggerFactory.getLogger(AddressRepositoryImpl.class);
    private static final String BASE_QUERY = "SELECT * FROM ADDRESS ";
    private static final String ALL_ADDRESS = BASE_QUERY + "ORDER BY ID LIMIT ? OFFSET ?";
    private static final String ADDRESSES_BY_USER_ID = BASE_QUERY + "WHERE USER_ID = ?";
    private static final String ADDRESS_BY_ID = BASE_QUERY + "WHERE ID = ?";
    private static final String FIND_BY_USER_AND_ID = BASE_QUERY + "WHERE USER_ID = ? AND ID = ?";
    //write operation queries
    private static final String INSERT_ADDRESS = "INSERT INTO ADDRESS (USER_ID,CUSTOMER_NAME,PHONE_NO,AREA,EXACT_LOCATION,LANDMARK,PINCODE,CITY,STATE) VALUES (?,?,?,?,?,?,?,?,?)";
    private static final String UPDATE_ADDRESS = "UPDATE ADDRESS SET CUSTOMER_NAME = ?, PHONE_NO = ?, AREA = ?, EXACT_LOCATION = ?, LANDMARK = ?, PINCODE = ?, CITY = ?, STATE = ? WHERE ID = ? AND USER_ID = ?";
    private static final String DELETE_BY_ID = "DELETE FROM ADDRESS WHERE ID = ?";
    private static final String DELETE_BY_USER_ID = "DELETE FROM ADDRESS WHERE USER_ID = ?";

    @Override
    public List<Address> findAll(PageRequest pageRequest) {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(ALL_ADDRESS)) {
            List<Address> addressList = new ArrayList<>();
            ps.setInt(1, pageRequest.getSize());
            ps.setInt(2, pageRequest.getPage());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Address address = mapToAddress(rs);
                    addressList.add(address);
                }
            }
            return addressList;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Address> findByUserId(int userId) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(ADDRESSES_BY_USER_ID)) {
            List<Address> addressList = new ArrayList<>();
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Address address = mapToAddress(rs);
                    addressList.add(address);
                }
            }
            return addressList;
        } catch (SQLException e) {
            throw new DataAccessException("DB error while fetching addresses for userId: " + userId, e);
        }
    }

    @Override
    public Optional<Address> findById(int id) {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(ADDRESS_BY_ID)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapToAddress(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("DB error while fetching address with id: " + id, e);
        }
    }

    @Override
    public Optional<Address> findByUserIdAndId(int userId, int addressId) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_BY_USER_AND_ID)) {
            ps.setInt(1, userId);
            ps.setInt(2, addressId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapToAddress(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            logger.error("Error fetching address for userId={} addressId={}", userId, addressId, e);
            throw new RuntimeException(e);
        }
    }


    @Override
    public Address save(Address address) {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(INSERT_ADDRESS, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, address.getUserId());
            pstmt.setString(2, address.getContactDetails().getCustomerName());
            pstmt.setString(3, address.getContactDetails().getPhoneNumber().getValue());
            pstmt.setString(4, address.getLocation().getArea());
            pstmt.setString(5, address.getLocation().getExactLocation());
            pstmt.setString(6, address.getLocation().getLandMark());
            pstmt.setString(7, address.getLocation().getPincode().getValue());
            pstmt.setString(8, address.getLocation().getCity());
            pstmt.setString(9, address.getLocation().getState());
            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int generated = rs.getInt(1);
                    return new Address.Builder()
                            .id(generated)
                            .userId(address.getUserId())
                            .contactDetails(address.getContactDetails())
                            .location(address.getLocation())
                            .build();
                }
            }
            throw new RuntimeException("Failed to insert address, no ID returned");
        } catch (SQLException e) {
            throw new DataAccessException("Error while saving address", e);
        }
    }

    @Override
    public void updateAddress(Address address) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_ADDRESS)) {
            ps.setString(1, address.getContactDetails().getCustomerName());
            ps.setString(2, address.getContactDetails().getPhoneNumber().getValue());
            ps.setString(3, address.getLocation().getArea());
            ps.setString(4, address.getLocation().getExactLocation());
            ps.setString(5, address.getLocation().getLandMark());
            ps.setString(6, address.getLocation().getPincode().getValue());
            ps.setString(7, address.getLocation().getCity());
            ps.setString(8, address.getLocation().getState());
            ps.setInt(9, address.getId());
            ps.setInt(10, address.getUserId());
            int rows = ps.executeUpdate();
            if (rows == 0) {
                throw new RuntimeException("Address not found for id=" + address.getId());
            }
        } catch (SQLException e) {
            logger.error("Error updating address id={}", address.getId(), e);
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
                throw new DataAccessException("No address deleted for id=" + id);
            }
        } catch (SQLException e) {
            logger.error("Error deleting address id={}", id, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteByUserId(int userId) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(DELETE_BY_USER_ID)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error deleting addresses for userId={}", userId, e);
            throw new RuntimeException(e);
        }
    }

    public Address mapToAddress(ResultSet rs) {
        Address.Builder address = new Address.Builder();
        try {
            address.id(rs.getInt("ID"));
            address.userId(rs.getInt("USER_ID"));
            address.contactDetails(new ContactDetails(rs.getString("CUSTOMER_NAME"), new PhoneNumber(rs.getString("PHONE_NO"))));
            address.location(new Location(rs.getString("AREA"), rs.getString("EXACT_LOCATION"), rs.getString("LANDMARK"), rs.getString("CITY"), rs.getString("STATE"), new Pincode(rs.getString("PINCODE"))));
            return address.build();
        } catch (SQLException e) {
            throw new RuntimeException("Error mapping Address", e);
        }
    }
}
