package com.ram.foodapp.model.address;

import java.util.Objects;

public class Location {
    private final String area;
    private final String exactLocation;
    private final String landMark;
    private final String city;
    private final String state;
    private final Pincode pincode;

    public Location(String area, String exactLocation, String landMark, String city, String state, Pincode pincode) {
        this.area = requireNonBlank(area,"Area");
        this.exactLocation = requireNonBlank(exactLocation,"Exact Location");
        this.landMark = landMark;
        this.city = requireNonBlank(city,"City");
        this.state = requireNonBlank(state,"State");
        this.pincode = Objects.requireNonNull(pincode);
    }

    private String requireNonBlank(String value,String field){
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " cannot be empty");
        }
        return value;
    }

    public String getArea() {
        return area;
    }

    public String getExactLocation() {
        return exactLocation;
    }

    public String getLandMark() {
        return landMark;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public Pincode getPincode() {
        return pincode;
    }
}
