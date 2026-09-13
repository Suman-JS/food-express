package com.suman.foodexpress.restaurants.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.Instant;

public record RestaurantRequest(
    @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        @Size(max = 100, message = "Email must not exceed 100 characters")
        String email,
    @NotBlank(message = "Name is required")
        @Size(max = 100, message = "Name must not exceed 100 characters")
        String name,
    @Size(max = 512, message = "Profile image URL must not exceed 512 characters")
        String profileImageUrl,
    @Size(max = 15, message = "FSSAI number must not exceed 15 characters") String fssaiNumber,
    @Size(max = 15, message = "GST number must not exceed 15 characters") String gstNumber,
    Instant contractAcceptedAt,
    @Valid BankDetailsRequest bankDetails) {}
