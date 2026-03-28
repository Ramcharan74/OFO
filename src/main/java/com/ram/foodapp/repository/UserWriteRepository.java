package com.ram.foodapp.repository;

import com.ram.foodapp.model.user.User;

public interface UserWriteRepository {

    User save(User user);

    void deleteById(int id);

    void deactivateUser(int id);

    void activateUser(int id);
}
