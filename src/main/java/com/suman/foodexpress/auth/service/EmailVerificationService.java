package com.suman.foodexpress.auth.service;

import com.suman.foodexpress.auth.entity.User;
import com.suman.foodexpress.auth.exception.ApiException;
import java.security.SecureRandom;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class EmailVerificationService {

  private static final String VERIFY_PREFIX = "verify_otp:";
  private static final int OTP_LENGTH = 6;
  private static final SecureRandom RANDOM = new SecureRandom();

  private final StringRedisTemplate redisTemplate;
  private final UserService userService;
  private final MailService mailService;
  private final Duration otpTtl;

  public EmailVerificationService(
      StringRedisTemplate redisTemplate,
      UserService userService,
      MailService mailService,
      @Value("${app.verify.otp-ttl-minutes:10}") long otpTtlMinutes) {
    this.redisTemplate = redisTemplate;
    this.userService = userService;
    this.mailService = mailService;
    this.otpTtl = Duration.ofMinutes(otpTtlMinutes);
  }

  public void issue(String email) {
    String otp = generateOtp();
    redisTemplate.opsForValue().set(VERIFY_PREFIX + email, otp, otpTtl);
    mailService.sendVerificationCode(email, otp);
  }

  public void verify(String email, String otp) {
    String storedOtp = redisTemplate.opsForValue().get(VERIFY_PREFIX + email);
    if (storedOtp == null || !storedOtp.equals(otp)) {
      throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid verification code");
    }
    User user = userService.findByEmail(email);
    if (!user.isEmailVerified()) {
      user.setVerifiedAt(java.time.Instant.now());
      user.markUpdated();
      userService.save(user);
    }
    redisTemplate.delete(VERIFY_PREFIX + email);
  }

  public void resend(String email) {
    User user = userService.findByEmail(email);
    if (user.isEmailVerified()) {
      throw new ApiException(HttpStatus.BAD_REQUEST, "Email already verified");
    }
    issue(email);
  }

  private static String generateOtp() {
    String otp = Integer.toString(RANDOM.nextInt(0, 1_000_000));
    return "0".repeat(OTP_LENGTH - otp.length()) + otp;
  }
}
