package com.ram.foodapp.repository;

import com.ram.foodapp.model.address.Address;

public interface AddressWriteRepository {

    Address save(Address address);

    void updateAddress(Address address);

    void deleteById(int id);

    void deleteByUserId(int userId);
}