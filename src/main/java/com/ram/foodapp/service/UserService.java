package com.ram.foodapp.service;

import com.ram.foodapp.dto.request.PageRequest;
import com.ram.foodapp.model.user.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<User> findAll();
    List<User> findAll(PageRequest pageRequest);
    User findById(int id);
    User save(User user);
    Optional<User> findByEmail(String email);
    List<User> findActiveUsers(PageRequest pageRequest);
    void deactivateUser(int id);
    void activateUser(int id);
    boolean existsById(int id);
    boolean existsByEmail(String email);
}
