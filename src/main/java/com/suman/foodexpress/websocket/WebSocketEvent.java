package com.suman.foodexpress.websocket;

public record WebSocketEvent<D>(String event, D data) {}
