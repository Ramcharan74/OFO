package com.ram.foodapp.model.address;

import java.util.Objects;

public class PhoneNumber {
    private final String value;

    public PhoneNumber(String value){
        if(value == null || !value.matches("\\d{10}")){
            throw new IllegalArgumentException("Invalid phone number");
        }
        this.value = value;
    }

    public String getValue(){
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(!(obj instanceof PhoneNumber)) return false;
        PhoneNumber that = (PhoneNumber) obj;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
