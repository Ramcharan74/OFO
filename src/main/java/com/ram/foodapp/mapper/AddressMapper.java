package com.ram.foodapp.mapper;

import com.ram.foodapp.dto.request.CreateAddressRequest;
import com.ram.foodapp.dto.request.UpdateAddressRequest;
import com.ram.foodapp.dto.response.AddressResponse;
import com.ram.foodapp.model.address.*;

public class AddressMapper {

    public static Address toEntity(CreateAddressRequest req) {
        return new Address.Builder()
                .userId(req.userId())
                .contactDetails(new ContactDetails(
                        req.customerName(),
                        new PhoneNumber(req.phoneNo())
                ))
                .location(new Location(
                        req.area(),
                        req.exactLocation(),
                        req.landmark(),
                        req.city(),
                        req.state(),
                        new Pincode(req.pincode())
                ))
                .build();
    }

    public static Address toEntity(UpdateAddressRequest req) {
        return new Address.Builder()
                .id(req.addressId())
                .userId(req.userId())
                .contactDetails(new ContactDetails(
                        req.customerName(),
                        new PhoneNumber(req.phoneNo())
                ))
                .location(new Location(
                        req.area(),
                        req.exactLocation(),
                        req.landmark(),
                        req.city(),
                        req.state(),
                        new Pincode(req.pincode())
                ))
                .build();
    }

    public static AddressResponse toResponse(Address address) {
        return new AddressResponse(
                address.getId(),
                address.getUserId(),
                address.getContactDetails().getCustomerName(),
                address.getContactDetails().getPhoneNumber().getValue(),
                address.getLocation().getArea(),
                address.getLocation().getExactLocation(),
                address.getLocation().getLandMark(),
                address.getLocation().getPincode().getValue(),
                address.getLocation().getCity(),
                address.getLocation().getState()
        );
    }
}
