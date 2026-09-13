package com.suman.foodexpress.foods;

import com.suman.foodexpress.auth.entity.User;
import com.suman.foodexpress.auth.service.UserService;
import com.suman.foodexpress.common.dto.ApiResponse;
import com.suman.foodexpress.foods.dto.SubmitRatingRequest;
import com.suman.foodexpress.foods.service.RatingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Food Routes")
@RequestMapping("/api/v1/foods")
public class FoodsController {

  private final RatingService ratingService;
  private final UserService userService;

  public FoodsController(RatingService ratingService, UserService userService) {
    this.ratingService = ratingService;
    this.userService = userService;
  }

  @PostMapping("/{foodId}/ratings")
  @Operation(
      summary = "Rate a food",
      description =
          "Submits the current user's rating (0-5, one decimal). The average rating is "
              + "recalculated asynchronously, and the response is returned immediately.")
  public ResponseEntity<ApiResponse<Void>> rateFood(
      @PathVariable UUID foodId,
      @Valid @RequestBody SubmitRatingRequest request,
      Principal principal) {

    User user = userService.findByEmail(principal.getName());
    ratingService.submitRating(foodId, user, request.rating());

    return ResponseEntity.accepted()
        .body(
            ApiResponse.success(
                null, "Rating accepted, recalculation in progress.", HttpStatus.ACCEPTED));
  }
}
