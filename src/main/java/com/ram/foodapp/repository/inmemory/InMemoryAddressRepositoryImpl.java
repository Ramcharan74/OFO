package com.ram.foodapp.repository.inmemory;

import com.ram.foodapp.dto.request.PageRequest;
import com.ram.foodapp.model.address.Address;
import com.ram.foodapp.repository.AddressRepository;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
public class InMemoryAddressRepositoryImpl implements AddressRepository {

    private final Map<Integer, Address> store = new ConcurrentHashMap<>();
    private final AtomicInteger idGenerator = new AtomicInteger(1);

    @Override
    public List<Address> findAll(PageRequest pageRequest) {
        validatePage(pageRequest);
        List<Address> list = store.values().stream()
                .sorted(Comparator.comparing(Address::getId))
                .toList();
        return paginate(list, pageRequest);
    }

    @Override
    public Optional<Address> findById(int id) {
        validateId(id);
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Address> findByUserId(int userId) {
        validateId(userId);
        return store.values().stream()
                .filter(a -> a.getUserId() == userId)
                .sorted(Comparator.comparing(Address::getId))
                .toList();
    }

    @Override
    public Optional<Address> findByUserIdAndId(int userId, int addressId) {
        validateId(userId);
        validateId(addressId);
        Address address = store.get(addressId);
        if (address == null || address.getUserId() != userId) {
            return Optional.empty();
        }
        return Optional.of(address);
    }

    @Override
    public Address save(Address address) {
        if (address == null) {
            throw new IllegalArgumentException("Address cannot be null");
        }
        int id = idGenerator.getAndIncrement();
        Address saved = new Address.Builder()
                .id(id)
                .userId(address.getUserId())
                .contactDetails(address.getContactDetails())
                .location(address.getLocation())
                .build();
        store.put(id, saved);
        return saved;
    }

    @Override
    public void updateAddress(Address address) {
        if (address == null || address.getId() <= 0) {
            throw new IllegalArgumentException("Invalid address");
        }
        store.compute(address.getId(), (id, existing) -> {
            if (existing == null) {
                throw new IllegalArgumentException("Address not found");
            }
            if (existing.getUserId() != address.getUserId()) {
                throw new IllegalArgumentException("User mismatch");
            }
            return new Address.Builder()
                    .id(existing.getId())
                    .userId(existing.getUserId())
                    .contactDetails(address.getContactDetails())
                    .location(address.getLocation())
                    .build();
        });
    }

    @Override
    public void deleteById(int id) {
        validateId(id);
        store.remove(id);
    }

    @Override
    public void deleteByUserId(int userId) {
        validateId(userId);
        store.entrySet().removeIf(entry ->
                entry.getValue().getUserId() == userId
        );
    }


    private List<Address> paginate(List<Address> list, PageRequest pageRequest) {
        int start = pageRequest.getPage() * pageRequest.getSize();
        if (start >= list.size()) {
            return List.of();
        }
        int end = Math.min(start + pageRequest.getSize(), list.size());
        return List.copyOf(list.subList(start, end));
    }

    private void validateId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Invalid id");
        }
    }

    private void validatePage(PageRequest pageRequest) {
        if (pageRequest.getPage() < 0 || pageRequest.getSize() <= 0) {
            throw new IllegalArgumentException("Invalid page or size");
        }
    }
}