package com.suman.foodexpress.restaurants.dto;

import com.suman.foodexpress.restaurants.entity.Restaurant;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record RestaurantListResponse(
    UUID id, String email, String name, String profileImageUrl, Instant createdAt) {

  public static List<RestaurantListResponse> of(List<Restaurant> restaurants) {
    return restaurants.stream()
        .map(
            restaurant ->
                new RestaurantListResponse(
                    restaurant.getId(),
                    restaurant.getEmail(),
                    restaurant.getName(),
                    restaurant.getProfileImageUrl(),
                    restaurant.getCreatedAt()))
        .toList();
  }
}
