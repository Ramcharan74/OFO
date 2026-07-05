package com.ram.foodapp.repository.inmemory;

import com.ram.foodapp.dto.request.PageRequest;
import com.ram.foodapp.enums.RestaurantStatus;
import com.ram.foodapp.exception.NotFoundException;
import com.ram.foodapp.model.restaurant.Restaurant;
import com.ram.foodapp.model.restaurant.RestaurantDetails;
import com.ram.foodapp.repository.RestaurantRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
public class InMemoryRestaurantRepositoryImpl implements RestaurantRepository {

    private static final Logger logger =
            LoggerFactory.getLogger(InMemoryRestaurantRepositoryImpl.class);

    private final Map<Integer, Restaurant> restaurantStore = new ConcurrentSkipListMap<>();
    private final AtomicInteger idGenerator = new AtomicInteger(1);

    @Override
    public Optional<Restaurant> findById(int id) {
        validateId(id);
        return Optional.ofNullable(restaurantStore.get(id));
    }

    @Override
    public List<Restaurant> findByUserId(int userId) {
        validateId(userId);
        return restaurantStore.values()
                .stream()
                .filter(r -> r.getUserId() == userId)
                .toList();
    }

    @Override
    public List<Restaurant> findByAddressId(int addressId) {
        validateId(addressId);
        return restaurantStore.values()
                .stream()
                .filter(r -> r.getRestaurantDetails().getAddressId() == addressId)
                .toList();
    }

    @Override
    public List<Restaurant> findByStatus(String status, PageRequest pageRequest) {
        validatePage(pageRequest);
        RestaurantStatus restaurantStatus = parseStatus(status);
        List<Restaurant> list = restaurantStore.values()
                .stream()
                .filter(r -> r.getStatus() == restaurantStatus)
                .toList();
        return paginate(list, pageRequest);
    }

    @Override
    public List<Restaurant> findAll(PageRequest pageRequest) {
        validatePage(pageRequest);
        List<Restaurant> list = new ArrayList<>(restaurantStore.values());
        return paginate(list, pageRequest);
    }

    @Override
    public Restaurant save(Restaurant restaurant) {
        if (restaurant == null) {
            throw new IllegalArgumentException("Restaurant should not be null");
        }
        int id = idGenerator.getAndIncrement();
        Restaurant saved = new Restaurant.Builder()
                .id(id)
                .userId(restaurant.getUserId())
                .status(restaurant.getStatus())
                .restaurantDetails(restaurant.getRestaurantDetails())
                .build();
        restaurantStore.put(id, saved);
        logger.info("Restaurant saved with id: {}", id);
        return saved;
    }

    @Override
    public void updateStatus(int restaurantId, String status) {
        RestaurantStatus restaurantStatus = parseStatus(status);
        restaurantStore.compute(restaurantId, (id, existing) -> {
            if (existing == null) {
                throw new NotFoundException("Restaurant not found");
            }
            return new Restaurant.Builder()
                    .id(existing.getId())
                    .userId(existing.getUserId())
                    .status(restaurantStatus)
                    .restaurantDetails(existing.getRestaurantDetails())
                    .build();
        });
        logger.info("Updated status for restaurant id: {}", restaurantId);
    }

    @Override
    public void updateRating(int restaurantId, double rating, int ratingCount) {

        restaurantStore.compute(restaurantId, (id, existing) -> {
            if (existing == null) {
                throw new NotFoundException("Restaurant not found");
            }
            return new Restaurant.Builder()
                    .id(existing.getId())
                    .userId(existing.getUserId())
                    .status(existing.getStatus())
                    .restaurantDetails(new RestaurantDetails(existing.getRestaurantDetails().getName(), existing.getRestaurantDetails().getAddressId(), rating, ratingCount))
                    .build();
        });
        logger.info("Updated rating for restaurant id: {}", restaurantId);
    }

    @Override
    public void deleteById(int id) {
        validateId(id);
        Restaurant removed = restaurantStore.remove(id);
        if (removed == null) {
            throw new NotFoundException("Restaurant not found");
        }
        logger.info("Deleted restaurant id: {}", id);
    }

    private void validateId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Id must be greater than 0");
        }
    }

    private void validatePage(PageRequest pageRequest) {
        int page = pageRequest.getPage();
        int size = pageRequest.getSize();
        if (page < 0 || size <= 0) {
            throw new IllegalArgumentException("Invalid page or size");
        }
    }

    private RestaurantStatus parseStatus(String status) {
        try {
            return RestaurantStatus.valueOf(status.toUpperCase());
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid restaurant status: " + status);
        }
    }

    private List<Restaurant> paginate(List<Restaurant> list, PageRequest pageRequest) {
        int page = pageRequest.getPage();
        int size = pageRequest.getSize();
        int start = page * size;
        if (start >= list.size()) {
            return List.of();
        }
        int end = Math.min(start + size, list.size());
        return List.copyOf(list.subList(start, end));
    }
}