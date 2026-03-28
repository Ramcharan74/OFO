package com.ram.foodapp.dto.response;

public record AddressResponse(
        int id,
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
