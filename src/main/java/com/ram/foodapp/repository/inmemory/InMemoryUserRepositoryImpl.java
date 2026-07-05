package com.ram.foodapp.repository.inmemory;

import com.ram.foodapp.dto.request.PageRequest;
import com.ram.foodapp.model.foodorder.AuditInfo;
import com.ram.foodapp.model.user.User;
import com.ram.foodapp.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
public class InMemoryUserRepositoryImpl implements UserRepository {
    private static final Logger logger =
            LoggerFactory.getLogger(InMemoryUserRepositoryImpl.class);

    private final Map<Integer, User> userStore = new ConcurrentHashMap<>();
    private final Map<String, Integer> emailIndex = new ConcurrentHashMap<>();
    private final AtomicInteger idGenerator = new AtomicInteger(1);

    public List<User> findAll(PageRequest pageRequest) {
        int page = pageRequest.getPage();
        int size = pageRequest.getSize();
        validatePage(page, size);
        List<User> users = userStore.entrySet().stream().sorted(Map.Entry.comparingByKey()).map(Map.Entry::getValue).toList();
        return paginate(users, page, size);
    }

    @Override
    public Optional<User> findById(int id) {
        validateId(id);
        return Optional.ofNullable(userStore.get(id));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        String normalized = normalizeEmail(email);
        Integer id = emailIndex.get(normalized);
        if (id == null) return Optional.empty();
        return Optional.ofNullable(userStore.get(id));
    }

    @Override
    public List<User> findActiveUsers(PageRequest pageRequest) {
        int page = pageRequest.getPage();
        int size = pageRequest.getSize();
        validatePage(page, size);
        List<User> users = userStore.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .map(Map.Entry::getValue)
                .filter(User::getIsActive)
                .toList();
        return paginate(users, page, size);
    }

    @Override
    public boolean existsById(int id) {
        validateId(id);
        return userStore.containsKey(id);
    }

    @Override
    public boolean existsByEmail(String email) {
        String normalized = normalizeEmail(email);
        return emailIndex.containsKey(normalized);
    }

    public User save(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        String email = normalizeEmail(user.getContactInfo().getEmail());
        if (emailIndex.containsKey(email)) {
            throw new IllegalArgumentException("Email already exists");
        }
        int id = idGenerator.getAndIncrement();
        User savedUser = new User.Builder()
                .id(id)
                .name(user.getName())
                .role(user.getRole())
                .contactInfo(user.getContactInfo())
                .credentials(user.getCredentials())
                .isActive(true)
                .auditInfo(new AuditInfo(LocalDateTime.now()))
                .build();
        userStore.put(id, savedUser);
        emailIndex.put(email, id);
        return savedUser;
    }

    public void deleteById(int id) {
        validateId(id);
        User user = userStore.remove(id);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }
        emailIndex.remove(
                user.getContactInfo().getEmail().toLowerCase()
        );
    }

    @Override
    public void deactivateUser(int id) {
        validateId(id);
        User user = getExistingUser(id);
        if (!user.getIsActive()) {
            logger.info("User already inactive");
            return;
        }
        userStore.put(id, buildUpdatedUser(user, false));
    }

    @Override
    public void activateUser(int id) {
        validateId(id);
        User user = getExistingUser(id);
        if (user.getIsActive()) {
            logger.info("User already active");
            return;
        }
        userStore.put(id, buildUpdatedUser(user, true));
    }

    private void validateId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Invalid Id");
        }
    }

    private void validatePage(int page, int size) {
        if (page < 0 || size <= 0) {
            throw new IllegalArgumentException("Invalid page or size");
        }
    }

    private String normalizeEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email should not be null or blank");
        }
        return email.trim().toLowerCase();
    }

    private User getExistingUser(int id) {
        User user = userStore.get(id);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }
        return user;
    }

    private List<User> paginate(List<User> users, int page, int size) {
        int start = page * size;
        if (start >= users.size()) {
            return List.of();
        }
        int end = Math.min(start + size, users.size());
        return List.copyOf(users.subList(start, end));
    }

    private User buildUpdatedUser(User user, boolean active) {
        return new User.Builder()
                .id(user.getId())
                .name(user.getName())
                .role(user.getRole())
                .contactInfo(user.getContactInfo())
                .credentials(user.getCredentials())
                .isActive(active)
                .auditInfo(user.getAuditInfo())
                .build();
    }
}
