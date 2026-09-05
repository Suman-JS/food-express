package com.suman.foodexpress.auth.dto;

import java.time.Instant;
import java.util.UUID;

public record AuthResponse(
        String accessToken,
        String tokenType,
        long expiresIn,
        UUID userId,
        String email,
        boolean emailVerified,
        Instant emailVerifiedAt) {

    public static AuthResponse of(String accessToken, long expiresIn,
            UUID userId, String email, boolean emailVerified, Instant emailVerifiedAt) {
        return new AuthResponse(accessToken, "Bearer", expiresIn, userId, email,
                emailVerified, emailVerifiedAt);
    }
}