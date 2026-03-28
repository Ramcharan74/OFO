package com.ram.foodapp.service.impl;

import com.ram.foodapp.dto.request.PageRequest;
import com.ram.foodapp.exception.DataAccessException;
import com.ram.foodapp.exception.NotFoundException;
import com.ram.foodapp.model.address.Address;
import com.ram.foodapp.repository.implementation.AddressRepositoryImpl;
import com.ram.foodapp.repository.implementation.UserRepositoryImpl;
import com.ram.foodapp.service.AddressService;

import java.util.List;
import java.util.Optional;

public class AddressServiceImpl implements AddressService {

    AddressRepositoryImpl addressRepositoryImpl;
    UserRepositoryImpl userRepositoryImpl;

    public AddressServiceImpl(AddressRepositoryImpl addressRepositoryImpl,UserRepositoryImpl userRepositoryImpl) {
        this.addressRepositoryImpl = addressRepositoryImpl;
        this.userRepositoryImpl = userRepositoryImpl;
    }

    @Override
    public List<Address> findAll(){
        return findAll(new PageRequest(0,10));
    }

    @Override
    public List<Address> findAll(PageRequest pageRequest) {
        if (pageRequest == null) {
            throw new IllegalArgumentException("PageRequest must not be null");
        }
        int size = Math.min(pageRequest.getSize(), 100);
        PageRequest safeRequest = new PageRequest(pageRequest.getPage(), size);
        try {
            return addressRepositoryImpl.findAll(safeRequest);
        } catch (Exception e) {
            throw new RuntimeException("Error while fetching addresses", e);
        }
    }

    @Override
    public List<Address> findByUserId(int userId) {
        if (userId <= 0) {
            throw new IllegalArgumentException("userId must be > 0, given: " + userId);
        }
        try {
            if(!userRepositoryImpl.existsById(userId)){
                throw new NotFoundException("User not found with id: " + userId);
            }
            return addressRepositoryImpl.findByUserId(userId);
        } catch (Exception e) {
            throw new RuntimeException("Error while fetching address by id: " + userId, e);
        }
    }

    @Override
    public Optional<Address> findById(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Id must be > 0, given: " + id);
        }
        try {
            return addressRepositoryImpl.findById(id);
        } catch (Exception e) {
            throw new RuntimeException("Error while fetching address by id: " + id, e);
        }
    }

    @Override
    public Address findByUserIdAndId(int userId, int addressId){
        if (userId <= 0) {
            throw new IllegalArgumentException("userId must be > 0, given: " + userId);
        }

        if (addressId <= 0) {
            throw new IllegalArgumentException("addressId must be > 0, given: " + addressId);
        }
        try{
            if (!userRepositoryImpl.existsById(userId)) {
                throw new NotFoundException("User not found with id: " + userId);
            }
            return addressRepositoryImpl.findByUserIdAndId(userId, addressId)
                    .orElseThrow(() -> new NotFoundException(
                            "Address not found for userId: " + userId + ", addressId: " + addressId
                    ));
        }catch (DataAccessException e){
            throw e;
        }catch (Exception e){
            throw new RuntimeException(
                    "Error while fetching address for userId: " + userId + "ans addressId: "+ addressId, e
            );
        }
    }

    @Override
    public Address save(Address address) {
        if (address == null) {
            throw new IllegalArgumentException("Address must not be null");
        }
        if (address.getUserId() <= 0) {
            throw new IllegalArgumentException("Invalid userId: " + address.getUserId());
        }
        int userId = address.getUserId();
        try {
            if (!userRepositoryImpl.existsById(userId)) {
                throw new NotFoundException("User not found with id: " + userId);
            }
            return addressRepositoryImpl.save(address);

        } catch (DataAccessException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(
                    "Error while saving address for userId: " + userId, e
            );
        }
    }

    @Override
    public void updateAddress(Address address) {
        if (address == null) {
            throw new IllegalArgumentException("Address must not be null");
        }
        int addressId = address.getId();
        int userId = address.getUserId();
        if (addressId <= 0) {
            throw new IllegalArgumentException("addressId must be > 0, given: " + addressId);
        }
        try {
            if (!userRepositoryImpl.existsById(userId)) {
                throw new NotFoundException("User not found with id: " + userId);
            }
            Address existing = addressRepositoryImpl.findById(addressId)
                    .orElseThrow(() ->
                            new NotFoundException("Address not found with id: " + addressId)
                    );
            if (existing.getUserId() != userId) {
                throw new IllegalStateException(
                        "Address does not belong to userId: " + userId
                );
            }
            addressRepositoryImpl.updateAddress(address);
        } catch (DataAccessException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(
                    "Error while updating address with id: " + addressId, e
            );
        }
    }

    @Override
    public void deleteById(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("addressId must be > 0, given: " + id);
        }
        try {
            addressRepositoryImpl.findById(id)
                    .orElseThrow(() ->
                            new NotFoundException("Address not found with id: " + id)
                    );
            addressRepositoryImpl.deleteById(id);
        } catch (DataAccessException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(
                    "Error while deleting address with id: " + id, e
            );
        }
    }

    @Override
    public void deleteByUserId(int userId) {
        if (userId <= 0) {
            throw new IllegalArgumentException("userId must be > 0, given: " + userId);
        }
        try {
            if (!userRepositoryImpl.existsById(userId)) {
                throw new NotFoundException("User not found with id: " + userId);
            }
            addressRepositoryImpl.deleteByUserId(userId);
        } catch (DataAccessException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(
                    "Error while deleting addresses for userId: " + userId, e
            );
        }
    }
}
