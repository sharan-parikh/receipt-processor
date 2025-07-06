package com.receiptprocessor.backend.user.service;

import com.receiptprocessor.backend.user.dto.UserDTO;
import com.receiptprocessor.backend.user.model.User;
import com.receiptprocessor.backend.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.util.UUID;
import jakarta.validation.Valid;

@Service
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;

  public UserServiceImpl(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public User getUserById(UUID userId) {
    return userRepository.findById(userId).orElseThrow(
        () -> new RuntimeException("User not found with id: " + userId)
    );
  }

  @Override
  public User createUser(@Valid UserDTO user) {
    if (userRepository.existsByEmail(user.getEmail())) {
      throw new RuntimeException("User already exists with email: " + user.getEmail());
    }

    User newUser = new User();
    newUser.setId(UUID.randomUUID());
    newUser.setEmail(user.getEmail());
    return userRepository.save(newUser);
  }
}
