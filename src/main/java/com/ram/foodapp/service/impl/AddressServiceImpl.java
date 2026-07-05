package com.ram.foodapp.service.impl;

import com.ram.foodapp.dto.request.PageRequest;
import com.ram.foodapp.exception.DataAccessException;
import com.ram.foodapp.exception.NotFoundException;
import com.ram.foodapp.model.address.Address;
import com.ram.foodapp.repository.AddressRepository;
import com.ram.foodapp.repository.UserRepository;
import com.ram.foodapp.service.AddressService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AddressServiceImpl implements AddressService {

    AddressRepository addressRepository;
    UserRepository userRepository;

    public AddressServiceImpl(AddressRepository addressRepository, UserRepository userRepository) {
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<Address> findAll() {
        return findAll(new PageRequest(0, 10));
    }

    @Override
    public List<Address> findAll(PageRequest pageRequest) {
        if (pageRequest == null) {
            throw new IllegalArgumentException("PageRequest must not be null");
        }
        int size = Math.min(pageRequest.getSize(), 100);
        PageRequest safeRequest = new PageRequest(pageRequest.getPage(), size);
        try {
            return addressRepository.findAll(safeRequest);
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
            if (!userRepository.existsById(userId)) {
                throw new NotFoundException("User not found with id: " + userId);
            }
            return addressRepository.findByUserId(userId);
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
            return addressRepository.findById(id);
        } catch (Exception e) {
            throw new RuntimeException("Error while fetching address by id: " + id, e);
        }
    }

    @Override
    public Address findByUserIdAndId(int userId, int addressId) {
        if (userId <= 0) {
            throw new IllegalArgumentException("userId must be > 0, given: " + userId);
        }

        if (addressId <= 0) {
            throw new IllegalArgumentException("addressId must be > 0, given: " + addressId);
        }
        try {
            if (!userRepository.existsById(userId)) {
                throw new NotFoundException("User not found with id: " + userId);
            }
            return addressRepository.findByUserIdAndId(userId, addressId)
                    .orElseThrow(() -> new NotFoundException(
                            "Address not found for userId: " + userId + ", addressId: " + addressId
                    ));
        } catch (DataAccessException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(
                    "Error while fetching address for userId: " + userId + "ans addressId: " + addressId, e
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
            if (!userRepository.existsById(userId)) {
                throw new NotFoundException("User not found with id: " + userId);
            }
            return addressRepository.save(address);

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
            if (!userRepository.existsById(userId)) {
                throw new NotFoundException("User not found with id: " + userId);
            }
            Address existing = addressRepository.findById(addressId)
                    .orElseThrow(() ->
                            new NotFoundException("Address not found with id: " + addressId)
                    );
            if (existing.getUserId() != userId) {
                throw new IllegalStateException(
                        "Address does not belong to userId: " + userId
                );
            }
            addressRepository.updateAddress(address);
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
            addressRepository.findById(id)
                    .orElseThrow(() ->
                            new NotFoundException("Address not found with id: " + id)
                    );
            addressRepository.deleteById(id);
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
            if (!userRepository.existsById(userId)) {
                throw new NotFoundException("User not found with id: " + userId);
            }
            addressRepository.deleteByUserId(userId);
        } catch (DataAccessException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(
                    "Error while deleting addresses for userId: " + userId, e
            );
        }
    }
}
