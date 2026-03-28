package com.ram.foodapp.repository;

import com.ram.foodapp.dto.request.PageRequest;
import com.ram.foodapp.model.user.User;

import java.util.List;
import java.util.Optional;

public interface UserReadRepository {

    List<User> findAll(PageRequest pageRequest);

    Optional<User> findById(int id);

    Optional<User> findByEmail(String email);

    List<User> findActiveUsers(PageRequest pageRequest);

    boolean existsById(int id);

    boolean existsByEmail(String email);
}
