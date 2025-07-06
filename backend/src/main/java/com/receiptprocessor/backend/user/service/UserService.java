package com.receiptprocessor.backend.user.service;

import com.receiptprocessor.backend.user.dto.UserDTO;
import com.receiptprocessor.backend.user.model.User;

import java.util.UUID;

/**
 * Placeholder service interface for user-related business logic.
 * This will be implemented when user functionality is added to the system.
 */
public interface UserService {

    User getUserById(UUID userId);

    User createUser(UserDTO user);
}