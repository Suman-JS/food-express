package com.suman.foodexpress.auth.config;

import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

@Configuration
public class CacheConfig {

  @Bean
  public RedisCacheManager cacheManager(
      RedisConnectionFactory connectionFactory, @Value("${app.cache.ttl:30m}") Duration ttl) {
    GenericJacksonJsonRedisSerializer valueSerializer =
        GenericJacksonJsonRedisSerializer.builder().enableUnsafeDefaultTyping().build();
    RedisCacheConfiguration defaults =
        RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(ttl)
            .serializeValuesWith(
                RedisSerializationContext.SerializationPair.fromSerializer(valueSerializer))
            .disableCachingNullValues();
    return RedisCacheManager.builder(connectionFactory).cacheDefaults(defaults).build();
  }
}
