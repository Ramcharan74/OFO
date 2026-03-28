package com.ram.foodapp.dto.request;

public record UpdateAddressRequest(
        int addressId,
        int userId,
        String customerName,
        String phoneNo,
        String area,
        String exactLocation,
        String landmark,
        String pincode,
        String city,
        String state
) {}
