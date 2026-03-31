package com.ram.foodapp.repository;

import com.ram.foodapp.dto.request.PageRequest;
import com.ram.foodapp.model.address.Address;

import java.util.List;
import java.util.Optional;

public interface AddressRepository {
    //Read
    List<Address> findAll(PageRequest pageRequest);

    Optional<Address> findById(int id);

    List<Address> findByUserId(int userId);

    Optional<Address> findByUserIdAndId(int userId, int addressId);

    //write
    Address save(Address address);

    void updateAddress(Address address);

    void deleteById(int id);

    void deleteByUserId(int userId);
}
