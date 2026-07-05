package com.ram.foodapp.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

public record CreateAddressRequest(
        @Positive
        int userId,
        @NotBlank
        String customerName,
        @Pattern(regexp = "\\d{10}")
        String phoneNo,
        @NotBlank
        String area,
        @NotBlank
        String exactLocation,
        String landmark,
        @Pattern(regexp = "\\d{6}")
        String pincode,
        @NotBlank
        String city,
        @NotBlank
        String state
) {}