package com.suman.foodexpress.websocket;

import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

  private final GenericWebSocketHandler handler;
  private final JwtHandshakeInterceptor handshakeInterceptor;
  private final String path;
  private final long maxSessionIdleTimeout;
  private final int maxTextMessageBufferSize;
  private final List<String> allowedOrigins;

  public WebSocketConfig(
      GenericWebSocketHandler handler,
      JwtHandshakeInterceptor handshakeInterceptor,
      @Value("${app.websocket.path:/ws}") String path,
      @Value("${app.websocket.max-session-idle-timeout:0}") long maxSessionIdleTimeout,
      @Value("${app.websocket.max-text-message-buffer-size:65536}") int maxTextMessageBufferSize,
      @Value("${app.cors.allowed-origins:http://localhost:3000,http://localhost:3001}")
          List<String> allowedOrigins) {
    this.handler = handler;
    this.handshakeInterceptor = handshakeInterceptor;
    this.path = path;
    this.maxSessionIdleTimeout = maxSessionIdleTimeout;
    this.maxTextMessageBufferSize = maxTextMessageBufferSize;
    this.allowedOrigins = allowedOrigins;
  }

  @Override
  public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
    registry
        .addHandler(handler, path)
        .addInterceptors(handshakeInterceptor)
        .setAllowedOriginPatterns(allowedOrigins.toArray(String[]::new));
  }

  @Bean
  @ConditionalOnProperty(
      name = "app.websocket.container.enabled",
      havingValue = "true",
      matchIfMissing = true)
  public ServletServerContainerFactoryBean createWebSocketContainer() {
    ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
    container.setMaxSessionIdleTimeout(maxSessionIdleTimeout);
    container.setMaxTextMessageBufferSize(maxTextMessageBufferSize);
    return container;
  }
}
