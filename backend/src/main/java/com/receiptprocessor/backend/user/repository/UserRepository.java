package com.receiptprocessor.backend.user.repository;

import com.receiptprocessor.backend.user.model.User;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

/**
 * Placeholder repository for user entity.
 * This will be implemented when user functionality is added to the system.
 */
public interface UserRepository extends MongoRepository<User, UUID> {
    // TODO: Add custom query methods
    // Example: findByUsername, findByEmail, etc.
}