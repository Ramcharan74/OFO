package com.ram.foodapp.model.address;

import java.util.Objects;

public class Pincode {
    private final String value;

    public Pincode(String value){
        if(value == null || !value.matches("\\d{6}")){
            throw new IllegalArgumentException("Invalid pincode");
        }
        this.value = value;
    }

    public String getValue(){
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if (!(o instanceof Pincode)) return false;
        Pincode pincode = (Pincode) o;
        return value.equals(pincode.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
