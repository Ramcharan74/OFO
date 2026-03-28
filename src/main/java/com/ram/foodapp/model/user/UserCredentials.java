package com.ram.foodapp.model.user;

import java.util.Objects;

public class UserCredentials {

    private final String password;

    public UserCredentials(String password) {
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters");
        }
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public boolean matches(String inputPassword) {
        return this.password.equals(inputPassword);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserCredentials)) return false;
        UserCredentials that = (UserCredentials) o;
        return password.equals(that.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(password);
    }

}