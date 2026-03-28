package com.ram.foodapp.model.user;

import com.ram.foodapp.enums.Role;
import com.ram.foodapp.model.foodorder.AuditInfo;

import java.util.Objects;

public class User {

    private final int id;
    private final String name;
    private final Role role;
    private final Boolean isActive;
    private final ContactInfo contactInfo;
    private final UserCredentials credentials;
    private final AuditInfo auditInfo;

    private User(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.role = builder.role;
        this.isActive = builder.isActive;
        this.contactInfo = builder.contactInfo;
        this.credentials = builder.credentials;
        this.auditInfo = builder.auditInfo;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Role getRole() {
        return role;
    }

    public ContactInfo getContactInfo() {
        return contactInfo;
    }

    public UserCredentials getCredentials() {
        return credentials;
    }

    public AuditInfo getAuditInfo() {
        return auditInfo;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", role=" + role +
                ", contactInfo=" + contactInfo +
                ", createdAt=" + (auditInfo != null ? auditInfo.getCreatedAt() : null) +
                '}';
    }

    // 🔹 Builder Class
    public static class Builder {

        private int id;
        private String name;
        private Role role;
        private ContactInfo contactInfo;
        private UserCredentials credentials;
        private AuditInfo auditInfo;
        private boolean isActive;

        public Builder id(int id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder role(Role role) {
            this.role = role;
            return this;
        }

        public Builder isActive(boolean isActive){
            this.isActive = isActive;
            return this;
        }

        public Builder contactInfo(ContactInfo contactInfo) {
            this.contactInfo = contactInfo;
            return this;
        }

        public Builder credentials(UserCredentials credentials) {
            this.credentials = credentials;
            return this;
        }

        public Builder auditInfo(AuditInfo auditInfo) {
            this.auditInfo = auditInfo;
            return this;
        }

        public User build() {
            validate();
            return new User(this);
        }

        private void validate() {
            if (name == null || name.isBlank()) {
                throw new IllegalArgumentException("Name required");
            }
            Objects.requireNonNull(role, "Role required");
            Objects.requireNonNull(contactInfo, "ContactInfo required");
            Objects.requireNonNull(credentials, "Credentials required");
        }
    }
}