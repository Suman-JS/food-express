package com.suman.foodexpress.auth.service;

import com.suman.foodexpress.auth.dto.AuthResponse;
import com.suman.foodexpress.auth.dto.LoginRequest;
import com.suman.foodexpress.auth.dto.RegisterRequest;
import com.suman.foodexpress.auth.entity.User;
import com.suman.foodexpress.auth.exception.ApiException;
import io.jsonwebtoken.ExpiredJwtException;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

  private final UserService userService;
  private final AuthenticationManager authenticationManager;
  private final JwtService jwtService;
  private final RefreshTokenService refreshTokenService;
  private final EmailVerificationService emailVerificationService;

  public AuthService(
      UserService userService,
      AuthenticationManager authenticationManager,
      JwtService jwtService,
      RefreshTokenService refreshTokenService,
      EmailVerificationService emailVerificationService) {
    this.userService = userService;
    this.authenticationManager = authenticationManager;
    this.jwtService = jwtService;
    this.refreshTokenService = refreshTokenService;
    this.emailVerificationService = emailVerificationService;
  }

  public Tokens login(LoginRequest request) {
    String email = request.email().toLowerCase();
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(email, request.password()));
    User user = userService.findByEmail(email);
    return issueTokens(user);
  }

  @Transactional
  public RegisterResult register(RegisterRequest request) {
    String email = request.email().toLowerCase();
    if (userService.existsByEmail(email)) {
      throw new ApiException(HttpStatus.CONFLICT, "Email already registered");
    }
    User user = userService.register(email, request.password(), request.name());
    emailVerificationService.issue(user.getEmail());
    return new RegisterResult(issueTokens(user), user);
  }

  public Tokens refresh(String refreshToken) {
    if (refreshToken == null) {
      throw new ApiException(HttpStatus.UNAUTHORIZED, "Refresh token missing");
    }
    String storedUserId = refreshTokenService.getUserIdForRefreshToken(refreshToken);
    if (storedUserId == null) {
      throw new ApiException(HttpStatus.UNAUTHORIZED, "Invalid or expired refresh token");
    }
    UUID userId;
    try {
      userId = UUID.fromString(storedUserId);
    } catch (IllegalArgumentException ex) {
      throw new ApiException(HttpStatus.UNAUTHORIZED, "Invalid or expired refresh token");
    }
    try {
      jwtService.parseClaims(refreshToken);
    } catch (ExpiredJwtException ex) {
      refreshTokenService.revokeRefreshToken(refreshToken);
      throw new ApiException(HttpStatus.UNAUTHORIZED, "Refresh token expired");
    }
    User user = userService.findById(userId);
    refreshTokenService.revokeRefreshToken(refreshToken);
    return issueTokens(user);
  }

  public void logout(String refreshToken) {
    if (refreshToken != null) {
      refreshTokenService.revokeRefreshToken(refreshToken);
    }
  }

  private Tokens issueTokens(User user) {
    String accessToken = jwtService.generateAccessToken(user.getId(), user.getEmail());
    String refreshToken = jwtService.generateRefreshToken(user.getId(), user.getEmail());
    refreshTokenService.storeRefreshToken(user.getId(), refreshToken);
    AuthResponse response =
        AuthResponse.of(
            accessToken,
            jwtService.getAccessTokenExpiration(),
            user.getId(),
            user.getEmail(),
            user.isEmailVerified(),
            user.getVerifiedAt());
    return new Tokens(response, refreshToken);
  }

  public record Tokens(AuthResponse response, String refreshToken) {}

  public record RegisterResult(Tokens tokens, User user) {}
}
