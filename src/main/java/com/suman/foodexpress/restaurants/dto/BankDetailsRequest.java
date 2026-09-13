package com.suman.foodexpress.restaurants.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record BankDetailsRequest(
    @NotBlank(message = "Account holder name is required")
        @Size(max = 100, message = "Account holder name must not exceed 100 characters")
        String accountHolderName,
    @NotBlank(message = "Account number is required")
        @Size(max = 34, message = "Account number must not exceed 34 characters")
        String accountNumber,
    @NotBlank(message = "IFSC code is required")
        @Size(max = 11, message = "IFSC code must not exceed 11 characters")
        String ifscCode,
    @NotBlank(message = "Bank name is required")
        @Size(max = 100, message = "Bank name must not exceed 100 characters")
        String bankName) {}
