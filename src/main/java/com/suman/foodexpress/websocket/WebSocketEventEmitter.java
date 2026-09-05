package com.suman.foodexpress.websocket;

import java.util.UUID;

import org.springframework.stereotype.Component;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@Component
public class WebSocketEventEmitter {

    private final GenericWebSocketHandler handler;
    private final ObjectMapper objectMapper;

    public WebSocketEventEmitter(GenericWebSocketHandler handler, ObjectMapper objectMapper) {
        this.handler = handler;
        this.objectMapper = objectMapper;
    }

    public void emit(String event, Object data) {
        handler.sendToAll(serialize(event, data));
    }

    public void emitToUser(UUID userId, String event, Object data) {
        handler.sendToUser(userId, serialize(event, data));
    }

    public void emitToSession(String sessionId, String event, Object data) {
        handler.sendToSession(sessionId, serialize(event, data));
    }

    private String serialize(String event, Object data) {
        try {
            return objectMapper.writeValueAsString(new WebSocketEvent<>(event, data));
        } catch (JacksonException e) {
            throw new IllegalStateException("Failed to serialize websocket event: " + event, e);
        }
    }
}