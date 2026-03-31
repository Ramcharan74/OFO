package com.ram.foodapp.service.impl;

import com.ram.foodapp.dto.request.PageRequest;
import com.ram.foodapp.exception.DataAccessException;
import com.ram.foodapp.exception.NotFoundException;
import com.ram.foodapp.exception.ServiceException;
import com.ram.foodapp.exception.UserAlreadyExistsException;
import com.ram.foodapp.model.user.User;
import com.ram.foodapp.repository.UserRepository;
import com.ram.foodapp.service.UserService;

import java.util.List;
import java.util.Optional;


public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> findAll() {
        PageRequest pageRequest = new PageRequest(0, 10);
        return findAll(pageRequest);
    }

    public List<User> findAll(PageRequest pageRequest) {
        validatePageRequest(pageRequest);
        try {
            return userRepository.findAll(pageRequest);
        } catch (DataAccessException e) {
            throw new DataAccessException("Failed to fetch Users", e);
        }
    }

    @Override
    public User findById(int id) {
        validateId(id);
        try {
            return userRepository.findById(id)
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
        if (userRepository.existsByEmail(user.getContactInfo().getEmail())) {
            throw new UserAlreadyExistsException("User email already exists");
        }
        return userRepository.save(user);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        validateEmail(email);
        email = email.trim().toLowerCase();
        try {
            return userRepository.findByEmail(email);
        } catch (DataAccessException e) {
            throw new ServiceException("Failed to fetch user by email: " + email, e);
        }
    }

    @Override
    public List<User> findActiveUsers(PageRequest pageRequest) {
        validatePageRequest(pageRequest);
        try {
            return userRepository.findActiveUsers(pageRequest);
        } catch (DataAccessException e) {
            throw new ServiceException("Failed to fetch active users", e);
        }
    }

    @Override
    public void deactivateUser(int id) {
        validateId(id);
        try {
            if (!userRepository.existsById(id)) {
                throw new NotFoundException("User not found with id: " + id);
            }
            userRepository.deactivateUser(id);
        } catch (DataAccessException e) {
            throw new ServiceException("Failed to deactivate user with id: " + id, e);
        }
    }

    @Override
    public void activateUser(int id) {
        validateId(id);
        try {
            if (!userRepository.existsById(id)) {
                throw new NotFoundException("User not found with id: " + id);
            }
            userRepository.activateUser(id);
        } catch (DataAccessException e) {
            throw new ServiceException("Failed to activate user with id: " + id, e);
        }
    }

    @Override
    public boolean existsById(int id) {
        try {
            return userRepository.existsById(id);
        } catch (DataAccessException e) {
            throw new ServiceException("Failed to check user existence for id: " + id, e);
        }
    }

    @Override
    public boolean existsByEmail(String email) {
        validateEmail(email);
        email = email.trim().toLowerCase();
        try {
            return userRepository.existsByEmail(email);
        } catch (DataAccessException e) {
            throw new ServiceException("Failed to check email existence: " + email, e);
        }
    }

    void validateId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("id must be a positive integer");
        }
    }

    void validateEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be null or blank");
        }
    }

    void validatePageRequest(PageRequest pageRequest) {
        if (pageRequest == null) {
            throw new IllegalArgumentException("PageRequest cannot be null");
        }
    }
}
