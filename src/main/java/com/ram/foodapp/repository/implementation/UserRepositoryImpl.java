package com.ram.foodapp.repository.implementation;

import com.ram.foodapp.config.DBConnection;
import com.ram.foodapp.dto.request.PageRequest;
import com.ram.foodapp.enums.Role;
import com.ram.foodapp.exception.DataAccessException;
import com.ram.foodapp.model.address.PhoneNumber;
import com.ram.foodapp.model.foodorder.AuditInfo;
import com.ram.foodapp.model.user.ContactInfo;
import com.ram.foodapp.model.user.User;
import com.ram.foodapp.model.user.User.Builder;
import com.ram.foodapp.model.user.UserCredentials;
import com.ram.foodapp.repository.UserRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class UserRepositoryImpl implements UserRepository {
    private static final String INSERT_USER = "INSERT INTO USER (EMAIL, PHONE_NO, NAME, PASSWORD, ROLE) VALUES (?, ?, ?, ?, ?)";
    private static final String FIND_BY_ID = "SELECT * FROM USER WHERE ID = ?";
    private static final String EXISTS_BY_ID = "SELECT 1 FROM USER WHERE id = ?";
    private static final String DELETE_BY_ID = "DELETE FROM USER WHERE id = ?";
    private static final String DEACTIVATE_USER = "UPDATE USER SET ACTIVE = false WHERE id = ?";
    private static final String ACTIVATE_USER = "UPDATE USER SET ACTIVE = true WHERE id = ?";
    private static final String FIND_BY_EMAIL = "SELECT * FROM USER WHERE EMAIL = ?";
    private static final String FIND_BY_ACTIVE_USERS = "SELECT * FROM USER WHERE ACTIVE = true AND LIMIT ? OFFSET ?";
    private static final String FIND_ALL = "SELECT * FROM USER LIMIT ? OFFSET ?";
    private static final String EXISTS_BY_EMAIL = "SELECT 1 FROM USER WHERE EMAIL = ?";


    @Override
    public List<User> findAll(PageRequest pageRequest) {
        List<User> users = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_ALL)) {
            ps.setInt(1, pageRequest.getSize());
            ps.setInt(2, pageRequest.getPage());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    User user = mapToUser(rs);
                    users.add(user);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to fetch users", e);
        }
        return users;
    }


    public User save(User user) {
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(INSERT_USER, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, user.getContactInfo().getEmail());
            pstmt.setString(2, user.getContactInfo().getPhoneNo().getValue());
            pstmt.setString(3, user.getName());
            pstmt.setString(4, user.getCredentials().getPassword());
            pstmt.setString(5, user.getRole().name());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("User creation failed, no rows affected.");
            }
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int generated = rs.getInt(1);
                    User savedUser = new User.Builder()
                            .id(generated)
                            .name(user.getName())
                            .role(user.getRole())
                            .contactInfo(user.getContactInfo())
                            .credentials(user.getCredentials())
                            .auditInfo(user.getAuditInfo())
                            .build();
                    return savedUser;
                } else {
                    throw new SQLException("Failed to retrieve generated ID");
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error saving user", e);
        }
    }

    public Optional<User> findById(int id) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_BY_ID)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User savedUser = mapToUser(rs);
                    return Optional.of(savedUser);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_BY_EMAIL)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapToUser(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching user by email: " + email, e);
        }
        return Optional.empty();
    }

    @Override
    public List<User> findActiveUsers(PageRequest pageRequest) {
        List<User> users = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_BY_ACTIVE_USERS)) {
            ps.setInt(1, pageRequest.getSize());
            ps.setInt(2, pageRequest.getPage());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    users.add(mapToUser(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching active users", e);
        }
        return users;
    }

    @Override
    public void deleteById(int id) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(DELETE_BY_ID)) {
            ps.setInt(1, id);
            ps.executeQuery();
        } catch (SQLException e) {
            throw new RuntimeException("Error while deleting user ", e);
        }
    }

    @Override
    public void deactivateUser(int id) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(DEACTIVATE_USER)) {
            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            if (rows == 0) {
                throw new RuntimeException("No user found to deactivate with id: " + id);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error deactivating user with id: " + id, e);
        }
    }

    @Override
    public void activateUser(int id) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(ACTIVATE_USER)) {
            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            if (rows == 0) {
                throw new RuntimeException("No user found to activate with id: " + id);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error activating user with id: " + id, e);
        }
    }

    @Override
    public boolean existsById(int id) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(EXISTS_BY_ID)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error checking user existence", e);
        }
    }

    @Override
    public boolean existsByEmail(String email) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(EXISTS_BY_EMAIL)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error checking existence of email: " + email, e);
        }
    }

    private User mapToUser(ResultSet rs) {
        Builder user = new User.Builder();
        try {
            user.id(rs.getInt("ID"));
            user.contactInfo(new ContactInfo(rs.getString("EMAIL"), new PhoneNumber(rs.getString("PHONE_NO"))));
            user.name(rs.getString("NAME"));
            user.credentials(new UserCredentials(rs.getString("PASSWORD")));
            user.role(Role.valueOf(rs.getString("ROLE")));
            user.isActive(rs.getBoolean("ACTIVE"));
            user.auditInfo(new AuditInfo(rs.getTimestamp("CREATED_AT").toLocalDateTime()));
            return user.build();
        } catch (SQLException e) {
            throw new DataAccessException("failed at  mapping User ResultSet to Object");
        }
    }
}
