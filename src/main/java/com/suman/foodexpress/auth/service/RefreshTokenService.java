package com.suman.foodexpress.auth.service;

import java.time.Duration;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RefreshTokenService {

  private static final String REFRESH_PREFIX = "refresh_token:";
  private static final String USER_TOKENS_PREFIX = "user_tokens:";

  private final StringRedisTemplate redisTemplate;
  private final JwtService jwtService;

  public RefreshTokenService(StringRedisTemplate redisTemplate, JwtService jwtService) {
    this.redisTemplate = redisTemplate;
    this.jwtService = jwtService;
  }

  public void storeRefreshToken(UUID userId, String refreshToken) {
    Duration ttl = Duration.ofMillis(jwtService.getRefreshTokenExpiration());
    redisTemplate.opsForValue().set(REFRESH_PREFIX + refreshToken, userId.toString(), ttl);
    redisTemplate.opsForSet().add(userTokensKey(userId), refreshToken);
    redisTemplate.expire(userTokensKey(userId), ttl);
  }

  public boolean isRefreshTokenValid(String refreshToken, UUID expectedUserId) {
    String storedUserId = redisTemplate.opsForValue().get(REFRESH_PREFIX + refreshToken);
    return storedUserId != null && storedUserId.equals(expectedUserId.toString());
  }

  public String getUserIdForRefreshToken(String refreshToken) {
    return redisTemplate.opsForValue().get(REFRESH_PREFIX + refreshToken);
  }

  public void revokeRefreshToken(String refreshToken) {
    String storedUserId = redisTemplate.opsForValue().get(REFRESH_PREFIX + refreshToken);
    if (storedUserId != null) {
      redisTemplate.opsForSet().remove(userTokensKey(UUID.fromString(storedUserId)), refreshToken);
    }
    redisTemplate.delete(REFRESH_PREFIX + refreshToken);
  }

  public void revokeAllUserRefreshTokens(UUID userId) {
    String userTokensKey = userTokensKey(userId);
    Set<String> tokens =
        Optional.ofNullable(redisTemplate.opsForSet().members(userTokensKey)).orElse(Set.of());
    tokens.forEach(token -> redisTemplate.delete(REFRESH_PREFIX + token));
    redisTemplate.delete(userTokensKey);
  }

  private String userTokensKey(UUID userId) {
    return USER_TOKENS_PREFIX + userId;
  }
}
