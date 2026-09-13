package com.suman.foodexpress.foods.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

public record SubmitRatingRequest(
    @NotNull(message = "Rating is required")
        @DecimalMin(value = "0.0", message = "Rating must be at least 0")
        @DecimalMax(value = "5.0", message = "Rating must not exceed 5")
        @Digits(integer = 1, fraction = 1, message = "Rating must have at most one decimal place")
        Double rating) {}
