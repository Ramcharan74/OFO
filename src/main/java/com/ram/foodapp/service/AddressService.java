package com.ram.foodapp.service;

import com.ram.foodapp.dto.request.PageRequest;
import com.ram.foodapp.model.address.Address;

import java.util.List;
import java.util.Optional;

public interface AddressService {

    List<Address> findAll();

    List<Address> findAll(PageRequest pageRequest);

    List<Address> findByUserId(int userId);

    Optional<Address> findById(int id);

    Address findByUserIdAndId(int userId, int addressId);

    Address save(Address address);

    void updateAddress(Address address);

    void deleteById(int id);

    void deleteByUserId(int userId);
}
