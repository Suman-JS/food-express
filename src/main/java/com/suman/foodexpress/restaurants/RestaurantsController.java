package com.suman.foodexpress.restaurants;

import com.suman.foodexpress.auth.security.Public;
import com.suman.foodexpress.common.dto.ApiResponse;
import com.suman.foodexpress.restaurants.dto.RestaurantListResponse;
import com.suman.foodexpress.restaurants.entity.Restaurant;
import com.suman.foodexpress.restaurants.service.RestaurantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Restaurant Routes")
@RequestMapping("/api/v1/restaurants")
public class RestaurantsController {

  private final RestaurantService restaurantService;

  public RestaurantsController(RestaurantService restaurantService) {
    this.restaurantService = restaurantService;
  }

  @GetMapping()
  @Public
  @Operation(summary = "Get available restaurant list with pagination")
  public ResponseEntity<ApiResponse<List<RestaurantListResponse>>> getAll() {
    List<Restaurant> restaurants = this.restaurantService.getAll();
    return ResponseEntity.ok(
        ApiResponse.success(
            RestaurantListResponse.of(restaurants), "Restaurants retrieved successfully."));
  }
}
