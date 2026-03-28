package com.ram.foodapp.service.impl;

import com.ram.foodapp.dto.request.PageRequest;
import com.ram.foodapp.exception.DataAccessException;
import com.ram.foodapp.exception.NotFoundException;
import com.ram.foodapp.exception.ServiceException;
import com.ram.foodapp.exception.UserAlreadyExistsException;
import com.ram.foodapp.mapper.UserMapper;
import com.ram.foodapp.model.user.User;
import com.ram.foodapp.repository.implementation.UserRepositoryImpl;
import com.ram.foodapp.service.UserService;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;


public class UserServiceImpl implements UserService {
    private final UserRepositoryImpl userRepositoryImpl;

    public UserServiceImpl(UserRepositoryImpl userRepositoryImpl) {
        this.userRepositoryImpl = userRepositoryImpl;
    }

    public List<User> findAll(){
        PageRequest pageRequest = new PageRequest(0,10);
        return findAll(pageRequest);
    }
    public List<User> findAll(PageRequest pageRequest) {
        validatePageRequest(pageRequest);
        try {
            return userRepositoryImpl.findAll(pageRequest);
        }catch (DataAccessException e){
            throw new DataAccessException("Failed to fetch Users",e);
        }
    }

    @Override
    public User findById(int id) {
        validateId(id);
        try {
            return userRepositoryImpl.findById(id)
                    .orElseThrow(() -> new NotFoundException("User not found with id: " + id));
        } catch (DataAccessException e) {
            throw new ServiceException("Failed to fetch user with id: " + id, e);
        }
    }

    @Override
    public User save(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        if(userRepositoryImpl.existsByEmail(user.getContactInfo().getEmail())){
            throw new UserAlreadyExistsException("User email already exists");
        }
        return userRepositoryImpl.save(user);
    }

    @Override
    public Optional<User> findByEmail(String email){
        validateEmail(email);
        email = email.trim().toLowerCase();
        try {
            return userRepositoryImpl.findByEmail(email);
        } catch (DataAccessException e) {
            throw new ServiceException("Failed to fetch user by email: " + email, e);
        }
    }

    @Override
    public List<User> findActiveUsers(PageRequest pageRequest){
        validatePageRequest(pageRequest);
        try {
            return userRepositoryImpl.findActiveUsers(pageRequest);
        } catch (DataAccessException e) {
            throw new ServiceException("Failed to fetch active users", e);
        }
    }

    @Override
    public void deactivateUser(int id) {
        validateId(id);
        try {
            if (!userRepositoryImpl.existsById(id)) {
                throw new NotFoundException("User not found with id: " + id);
            }
            userRepositoryImpl.deactivateUser(id);
        } catch (DataAccessException e) {
            throw new ServiceException("Failed to deactivate user with id: " + id, e);
        }
    }

    @Override
    public void activateUser(int id) {
        validateId(id);
        try {
            if (!userRepositoryImpl.existsById(id)) {
                throw new NotFoundException("User not found with id: " + id);
            }
            userRepositoryImpl.activateUser(id);
        } catch (DataAccessException e) {
            throw new ServiceException("Failed to activate user with id: " + id, e);
        }
    }

    @Override
    public boolean existsById(int id) {
        try {
            return userRepositoryImpl.existsById(id);
        } catch (DataAccessException e) {
            throw new ServiceException("Failed to check user existence for id: " + id, e);
        }
    }

    @Override
    public boolean existsByEmail(String email){
        validateEmail(email);
        email = email.trim().toLowerCase();
        try {
            return userRepositoryImpl.existsByEmail(email);
        } catch (DataAccessException e) {
            throw new ServiceException("Failed to check email existence: " + email, e);
        }
    }

    void validateId(int id){
        if (id <= 0) {
            throw new IllegalArgumentException("id must be a positive integer");
        }
    }

    void validateEmail(String email){
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be null or blank");
        }
    }

    void validatePageRequest(PageRequest pageRequest){
        if (pageRequest == null) {
            throw new IllegalArgumentException("PageRequest cannot be null");
        }
    }
}
