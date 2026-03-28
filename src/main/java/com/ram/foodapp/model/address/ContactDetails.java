package com.ram.foodapp.model.address;

import java.util.Objects;

public class ContactDetails {
    private final String customerName;
    private final PhoneNumber phoneNumber;

    public ContactDetails(String customerName, PhoneNumber phoneNumber) {
        if(customerName == null || customerName.isBlank()){
            throw new IllegalArgumentException("Customer name cannot be empty");
        }
        this.customerName = customerName;
        this.phoneNumber = Objects.requireNonNull(phoneNumber);
    }

    public String getCustomerName() {
        return customerName;
    }

    public PhoneNumber getPhoneNumber() {
        return phoneNumber;
    }
}
