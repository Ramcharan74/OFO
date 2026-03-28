package com.ram.foodapp.model.user;

import com.ram.foodapp.model.address.PhoneNumber;

import java.util.Objects;

public class ContactInfo {

    private final String email;
    private final PhoneNumber phoneNo;

    public ContactInfo(String email, PhoneNumber phoneNo) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email required");
        }
        email = email.trim().toLowerCase();

        if (!isValidEmail(email)) {
            throw new IllegalArgumentException("Invalid email format");
        }
        this.email = email;
        this.phoneNo = phoneNo;
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return email.matches(emailRegex);
    }

    public String getEmail() { return email; }
    public PhoneNumber getPhoneNo() { return phoneNo; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ContactInfo)) return false;
        ContactInfo that = (ContactInfo) o;
        return email.equals(that.email) && phoneNo.equals(that.phoneNo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, phoneNo);
    }

    @Override
    public String toString() {
        return "ContactInfo{" +
                "email='" + email + '\'' +
                ", phoneNo='" + phoneNo + '\'' +
                '}';
    }
}