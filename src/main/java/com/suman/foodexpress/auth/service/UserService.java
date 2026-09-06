package com.suman.foodexpress.auth.service;

import com.suman.foodexpress.auth.entity.User;
import com.suman.foodexpress.auth.entity.UserRole;
import com.suman.foodexpress.auth.exception.ApiException;
import com.suman.foodexpress.auth.repository.UserRepository;
import com.suman.foodexpress.common.enums.StatusEnum;
import java.time.Instant;
import java.util.UUID;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  public User register(String email, String rawPassword, String name) {
    Instant now = Instant.now();
    User user =
        User.builder()
            .email(email)
            .passwordHash(passwordEncoder.encode(rawPassword))
            .name(name)
            .status(StatusEnum.ACTIVE)
            .role(UserRole.USER)
            .createdAt(now)
            .updatedAt(now)
            .build();
    return userRepository.save(user);
  }

  @CacheEvict(
      cacheNames = {"usersById", "usersByEmail"},
      allEntries = true)
  public User save(User user) {
    return userRepository.save(user);
  }

  @Cacheable(cacheNames = "usersById", key = "#id")
  public User findById(UUID id) {
    return userRepository
        .findById(id)
        .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));
  }

  @Cacheable(cacheNames = "usersByEmail", key = "#email")
  public User findByEmail(String email) {
    return userRepository
        .findByEmail(email)
        .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));
  }

  public boolean existsByEmail(String email) {
    return userRepository.existsByEmail(email);
  }
}
