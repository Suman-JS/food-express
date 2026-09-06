package com.suman.foodexpress.auth.dto;

import com.suman.foodexpress.auth.entity.User;
import com.suman.foodexpress.auth.entity.UserRole;
import com.suman.foodexpress.common.enums.StatusEnum;
import java.time.Instant;
import java.util.UUID;

public record UserProfileResponse(
    UUID id,
    String email,
    String name,
    String profileImageUrl,
    UserRole role,
    StatusEnum status,
    boolean emailVerified,
    Instant verifiedAt,
    Instant createdAt) {

  public static UserProfileResponse of(User user) {
    return new UserProfileResponse(
        user.getId(),
        user.getEmail(),
        user.getName(),
        user.getProfileImageUrl(),
        user.getRole(),
        user.getStatus(),
        user.isEmailVerified(),
        user.getVerifiedAt(),
        user.getCreatedAt());
  }
}
