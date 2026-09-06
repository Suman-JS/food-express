package com.suman.foodexpress.websocket;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class GenericWebSocketHandler extends TextWebSocketHandler {

  public static final String USER_ID_ATTRIBUTE = "userId";

  private final ConcurrentHashMap<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
  private final ConcurrentHashMap<UUID, Set<String>> userSessions = new ConcurrentHashMap<>();

  @Override
  public void afterConnectionEstablished(WebSocketSession session) {
    sessions.put(session.getId(), session);
    if (session.getAttributes().get(USER_ID_ATTRIBUTE) instanceof UUID userId) {
      userSessions
          .computeIfAbsent(userId, key -> ConcurrentHashMap.newKeySet())
          .add(session.getId());
    }
  }

  @Override
  public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
    sessions.remove(session.getId());
    if (session.getAttributes().get(USER_ID_ATTRIBUTE) instanceof UUID userId) {
      userSessions.computeIfPresent(
          userId,
          (key, ids) -> {
            ids.remove(session.getId());
            return ids.isEmpty() ? null : ids;
          });
    }
  }

  public void sendToAll(String payload) {
    for (WebSocketSession session : sessions.values()) {
      send(session, payload);
    }
  }

  public void sendToUser(UUID userId, String payload) {
    Set<String> ids = userSessions.get(userId);
    if (ids == null) {
      return;
    }
    for (String id : ids) {
      WebSocketSession session = sessions.get(id);
      if (session != null) {
        send(session, payload);
      }
    }
  }

  public void sendToSession(String sessionId, String payload) {
    WebSocketSession session = sessions.get(sessionId);
    if (session != null) {
      send(session, payload);
    }
  }

  private void send(WebSocketSession session, String payload) {
    if (!session.isOpen()) {
      return;
    }
    try {
      session.sendMessage(new TextMessage(payload));
    } catch (Exception ignored) {
      // client disconnected mid-send; other sessions should still be notified
    }
  }
}
