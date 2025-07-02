package com.receiptprocessor.backend.user.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;

import java.util.UUID;

/**
 * Placeholder model for user entity.
 * This will be implemented when user functionality is added to the system.
 */
@Document("users")
@Data
public class User {
    @Id
    private UUID id;
    
    // TODO: Add user fields
    // Example: username, email, createdAt, preferences, etc.
    
    public User() {
        this.id = UUID.randomUUID();
    }
}