package com.ram.foodapp.model.address;


import java.util.Objects;

public class Address {
    private final int id;
    private final int userId;
    private final ContactDetails contactDetails;
    private final Location location;

    public Address(Builder builder) {
        this.id = builder.id;
        if (builder.userId <= 0) {
            throw new IllegalArgumentException("userId must be > 0");
        }
        this.userId = builder.userId;
        this.contactDetails = Objects.requireNonNull(builder.contactDetails);
        this.location = Objects.requireNonNull(builder.location);
    }

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public ContactDetails getContactDetails() {
        return contactDetails;
    }

    public Location getLocation() {
        return location;
    }

    public static class Builder{
        private int id;
        private int userId;
        private ContactDetails contactDetails;
        private Location location;

        public Builder id(int id){
            this.id = id;
            return this;
        }

        public Builder userId(int userId){
            this.userId = userId;
            return this;
        }

        public Builder contactDetails(ContactDetails contactDetails){
            this.contactDetails = contactDetails;
            return this;
        }

        public Builder location(Location location){
            this.location = location;
            return this;
        }

        public Address build() {
            Address address = new Address(this);
            return address;
        }
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Address)) return false;
        Address address = (Address) o;
        return id == address.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
