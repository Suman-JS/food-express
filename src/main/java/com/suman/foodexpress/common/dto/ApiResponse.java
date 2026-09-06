package com.suman.foodexpress.common.dto;

import org.springframework.http.HttpStatus;

public record ApiResponse<T>(
    boolean success,
    String message,
    int statusCode,
    T data) {

  public static <T> ApiResponse<T> success(T data, String message, HttpStatus status) {
    return new ApiResponse<>(true, message, status.value(), data);
  }

  public static <T> ApiResponse<T> success(T data, String message) {
    return success(data, message, HttpStatus.OK);
  }

  public static <T> ApiResponse<T> success(T data) {
    return success(data, "Success", HttpStatus.OK);
  }

  public static <T> ApiResponse<T> error(String message, HttpStatus status) {
    return new ApiResponse<>(false, message, status.value(), null);
  }

  public static <T> ApiResponse<T> failure(String message, HttpStatus status, T data) {
    return new ApiResponse<>(false, message, status.value(), data);
  }
}
