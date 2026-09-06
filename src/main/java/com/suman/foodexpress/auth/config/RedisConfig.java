package com.suman.foodexpress.auth.config;

import io.lettuce.core.ClientOptions;
import io.lettuce.core.protocol.ProtocolVersion;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.util.StringUtils;

@Configuration
public class RedisConfig {

  @Bean
  @ConditionalOnMissingBean
  public LettuceConnectionFactory redisConnectionFactory(
      @Value("${spring.data.redis.host}") String host,
      @Value("${spring.data.redis.port}") int port,
      @Value("${spring.data.redis.username:}") String username,
      @Value("${spring.data.redis.password:}") String password) {
    RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(host, port);
    if (StringUtils.hasText(username)) {
      config.setUsername(username);
    }
    if (StringUtils.hasText(password)) {
      config.setPassword(RedisPassword.of(password));
    }
    LettuceClientConfiguration clientConfig =
        LettuceClientConfiguration.builder()
            .clientOptions(ClientOptions.builder().protocolVersion(ProtocolVersion.RESP2).build())
            .build();
    return new LettuceConnectionFactory(config, clientConfig);
  }
}
