package com.suman.foodexpress.auth;

import com.suman.foodexpress.auth.dto.AuthResponse;
import com.suman.foodexpress.auth.dto.LoginRequest;
import com.suman.foodexpress.auth.dto.RegisterRequest;
import com.suman.foodexpress.auth.dto.ResendVerificationRequest;
import com.suman.foodexpress.auth.dto.UserProfileResponse;
import com.suman.foodexpress.auth.dto.VerifyEmailRequest;
import com.suman.foodexpress.auth.entity.User;
import com.suman.foodexpress.auth.security.Public;
import com.suman.foodexpress.auth.service.AuthService;
import com.suman.foodexpress.auth.service.AuthService.RegisterResult;
import com.suman.foodexpress.auth.service.AuthService.Tokens;
import com.suman.foodexpress.auth.service.CookieService;
import com.suman.foodexpress.auth.service.EmailVerificationService;
import com.suman.foodexpress.auth.service.UserService;
import com.suman.foodexpress.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.security.Principal;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Authentication")
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

  private final AuthService authService;
  private final CookieService cookieService;
  private final EmailVerificationService emailVerificationService;
  private final UserService userService;

  public AuthController(
      AuthService authService,
      CookieService cookieService,
      EmailVerificationService emailVerificationService,
      UserService userService) {
    this.authService = authService;
    this.cookieService = cookieService;
    this.emailVerificationService = emailVerificationService;
    this.userService = userService;
  }

  @PostMapping("/register")
  @Public
  @Operation(summary = "Register as an user")
  public ResponseEntity<ApiResponse<AuthResponse>> register(
      @Valid @RequestBody RegisterRequest request) {
    RegisterResult result = authService.register(request);
    return ResponseEntity.status(HttpStatus.CREATED)
        .header(
            HttpHeaders.SET_COOKIE,
            cookieService.createRefreshTokenCookie(result.tokens().refreshToken()).toString())
        .body(
            ApiResponse.success(
                result.tokens().response(), "Registered successfully", HttpStatus.CREATED));
  }

  @Public
  @PostMapping("/login")
  @Operation(summary = "Login with email and password")
  public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
    Tokens tokens = authService.login(request);
    return ResponseEntity.ok()
        .header(
            HttpHeaders.SET_COOKIE,
            cookieService.createRefreshTokenCookie(tokens.refreshToken()).toString())
        .body(ApiResponse.success(tokens.response(), "Login successful"));
  }

  @PostMapping("/refresh")
  @Operation(summary = "Renew an access token")
  @Public
  public ResponseEntity<ApiResponse<AuthResponse>> refresh(HttpServletRequest httpRequest) {
    String refreshToken = cookieService.extractRefreshToken(httpRequest);
    Tokens tokens = authService.refresh(refreshToken);
    return ResponseEntity.ok()
        .header(
            HttpHeaders.SET_COOKIE,
            cookieService.createRefreshTokenCookie(tokens.refreshToken()).toString())
        .body(ApiResponse.success(tokens.response(), "Token refreshed"));
  }

  @GetMapping("/me")
  @Operation(summary = "Retrieve profile of an authenticated user")
  public ResponseEntity<ApiResponse<UserProfileResponse>> me(Principal principal) {
    User user = userService.findByEmail(principal.getName());
    return ResponseEntity.ok(ApiResponse.success(UserProfileResponse.of(user), "User profile"));
  }

  @PostMapping("/logout")
  @Operation(summary = "Logout an authenticated user")
  public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest httpRequest) {
    authService.logout(cookieService.extractRefreshToken(httpRequest));
    ResponseCookie clearCookie = cookieService.clearRefreshTokenCookie();
    return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, clearCookie.toString())
        .body(ApiResponse.success(null, "Logged out"));
  }

  @PostMapping("/verify-email")
  @Operation(summary = "Verify email after registration")
  @Public
  public ResponseEntity<ApiResponse<Void>> verifyEmail(
      @Valid @RequestBody VerifyEmailRequest request) {
    emailVerificationService.verify(request.email().toLowerCase(), request.otp());
    return ResponseEntity.ok(ApiResponse.success(null, "Email verified"));
  }

  @PostMapping("/resend-verification")
  @Operation(summary = "Resend verification code")
  @Public
  public ResponseEntity<ApiResponse<Void>> resendVerification(
      @Valid @RequestBody ResendVerificationRequest request) {
    emailVerificationService.resend(request.email().toLowerCase());
    return ResponseEntity.ok(ApiResponse.success(null, "Verification email sent"));
  }
}
