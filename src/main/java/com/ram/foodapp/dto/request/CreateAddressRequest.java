package com.ram.foodapp.dto.request;

public record CreateAddressRequest(
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