package com.suman.foodexpress.restaurants.dto;

import com.suman.foodexpress.common.enums.StatusEnum;
import com.suman.foodexpress.restaurants.entity.BankDetails;
import com.suman.foodexpress.restaurants.entity.Restaurant;
import java.time.Instant;
import java.util.UUID;

public record RestaurantDetailResponse(
    UUID id,
    String email,
    String name,
    String profileImageUrl,
    Owner owner,
    String fssaiNumber,
    String gstNumber,
    Instant contractAcceptedAt,
    Bank bankDetails,
    StatusEnum status,
    Instant approvedAt,
    Instant createdAt) {

  public record Owner(UUID id, String name, String email) {}

  public record Bank(
      String accountHolderName, String accountNumber, String ifscCode, String bankName) {}

  public static RestaurantDetailResponse of(Restaurant restaurant) {
    BankDetails bankDetails = restaurant.getBankDetails();
    Bank bank =
        bankDetails == null
            ? null
            : new Bank(
                bankDetails.getAccountHolderName(),
                bankDetails.getAccountNumber(),
                bankDetails.getIfscCode(),
                bankDetails.getBankName());
    Owner owner =
        new Owner(
            restaurant.getOwner().getId(),
            restaurant.getOwner().getName(),
            restaurant.getOwner().getEmail());
    return new RestaurantDetailResponse(
        restaurant.getId(),
        restaurant.getEmail(),
        restaurant.getName(),
        restaurant.getProfileImageUrl(),
        owner,
        restaurant.getFssaiNumber(),
        restaurant.getGstNumber(),
        restaurant.getContractAcceptedAt(),
        bank,
        restaurant.getStatus(),
        restaurant.getApprovedAt(),
        restaurant.getCreatedAt());
  }
}
