package com.receiptprocessor.backend.user.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;

import java.time.LocalDateTime;
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

    private String email;

    private LocalDateTime createdAt;
    
    public User() {
        this.id = UUID.randomUUID();
    }
}