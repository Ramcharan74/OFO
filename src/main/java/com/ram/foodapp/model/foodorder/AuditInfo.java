package com.ram.foodapp.model.foodorder;

import java.time.LocalDateTime;

public class AuditInfo {
    private final LocalDateTime createdAt;

    public AuditInfo(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
