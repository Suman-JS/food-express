package com.suman.foodexpress.restaurants;

import com.suman.foodexpress.auth.entity.User;
import com.suman.foodexpress.auth.security.Public;
import com.suman.foodexpress.auth.service.UserService;
import com.suman.foodexpress.common.dto.ApiResponse;
import com.suman.foodexpress.common.dto.PaginatedResponse;
import com.suman.foodexpress.common.dto.PaginationMeta;
import com.suman.foodexpress.restaurants.dto.RestaurantDetailResponse;
import com.suman.foodexpress.restaurants.dto.RestaurantListResponse;
import com.suman.foodexpress.restaurants.dto.RestaurantPaginationDto;
import com.suman.foodexpress.restaurants.dto.RestaurantRequest;
import com.suman.foodexpress.restaurants.entity.Restaurant;
import com.suman.foodexpress.restaurants.service.RestaurantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Restaurant Routes")
@RequestMapping("/api/v1/restaurants")
public class RestaurantsController {

  private final RestaurantService restaurantService;
  private final UserService userService;

  public RestaurantsController(RestaurantService restaurantService, UserService userService) {
    this.restaurantService = restaurantService;
    this.userService = userService;
  }

  @GetMapping
  @Public
  @Operation(summary = "Get available restaurant list with pagination")
  public ResponseEntity<ApiResponse<PaginatedResponse<RestaurantListResponse>>> getAll(
      @Valid RestaurantPaginationDto restaurantPaginationDto) {

    Page<Restaurant> restaurants = restaurantService.getAll(restaurantPaginationDto);

    List<RestaurantListResponse> items = RestaurantListResponse.of(restaurants.getContent());

    PaginationMeta meta =
        new PaginationMeta(
            restaurants.getTotalElements(),
            restaurants.getNumber() + 1,
            restaurants.getSize(),
            restaurants.hasNext(),
            restaurants.hasPrevious());

    PaginatedResponse<RestaurantListResponse> data = new PaginatedResponse<>(meta, items);

    return ResponseEntity.ok(ApiResponse.success(data, "Restaurants retrieved successfully."));
  }

  @PostMapping
  @Operation(summary = "Register a restaurant", description = "The calling user becomes the owner.")
  public ResponseEntity<ApiResponse<RestaurantDetailResponse>> create(
      @Valid @RequestBody RestaurantRequest request, Principal principal) {
    User owner = userService.findByEmail(principal.getName());
    Restaurant saved = restaurantService.create(request, owner);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(
            ApiResponse.success(
                RestaurantDetailResponse.of(saved),
                "Restaurant registered successfully.",
                HttpStatus.CREATED));
  }

  @GetMapping("/me")
  @Operation(summary = "Get the current user's restaurant")
  public ResponseEntity<ApiResponse<RestaurantDetailResponse>> getMine(Principal principal) {
    User owner = userService.findByEmail(principal.getName());
    Restaurant restaurant = restaurantService.getByOwner(owner.getId());
    return ResponseEntity.ok(
        ApiResponse.success(RestaurantDetailResponse.of(restaurant), "Restaurant"));
  }
}
