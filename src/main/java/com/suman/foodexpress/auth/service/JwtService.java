package com.suman.foodexpress.auth.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;
import javax.crypto.SecretKey;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

  private final SecretKey signingKey;
  private final long accessTokenExpiration;
  private final long refreshTokenExpiration;

  public JwtService(
      @Value("${app.jwt.secret}") String secret,
      @Value("${app.jwt.access-token-expiration}") long accessTokenExpiration,
      @Value("${app.jwt.refresh-token-expiration}") long refreshTokenExpiration) {
    byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
    this.signingKey = Keys.hmacShaKeyFor(keyBytes);
    this.accessTokenExpiration = accessTokenExpiration;
    this.refreshTokenExpiration = refreshTokenExpiration;
  }

  public String generateAccessToken(UUID userId, String email) {
    return buildToken(userId, email, accessTokenExpiration);
  }

  public String generateRefreshToken(UUID userId, String email) {
    return buildToken(userId, email, refreshTokenExpiration);
  }

  public long getAccessTokenExpiration() {
    return accessTokenExpiration;
  }

  public long getRefreshTokenExpiration() {
    return refreshTokenExpiration;
  }

  public @NonNull Claims parseClaims(@NonNull String token) {
    return Jwts.parser().verifyWith(signingKey).build().parseSignedClaims(token).getPayload();
  }

  @SuppressWarnings("null")
  public UUID extractUserId(@NonNull Claims claims) {
    return UUID.fromString(claims.getSubject());
  }

  public String extractEmail(@NonNull Claims claims) {
    return claims.get("email", String.class);
  }

  public boolean isValid(@NonNull Claims claims, UUID expectedUserId) {
    return expectedUserId.toString().equals(claims.getSubject());
  }

  private String buildToken(UUID userId, String email, long expirationMillis) {
    var now = new Date();
    return Jwts.builder()
        .subject(userId.toString())
        .claim("email", email)
        .issuedAt(now)
        .expiration(new Date(now.getTime() + expirationMillis))
        .signWith(signingKey)
        .compact();
  }
}
