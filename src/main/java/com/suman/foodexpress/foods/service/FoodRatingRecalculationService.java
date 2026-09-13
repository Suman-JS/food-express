package com.suman.foodexpress.foods.service;

import com.suman.foodexpress.auth.exception.ApiException;
import com.suman.foodexpress.foods.entity.Food;
import com.suman.foodexpress.foods.repository.FoodRepository;
import com.suman.foodexpress.foods.repository.RatingRepository;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FoodRatingRecalculationService {

  private final RatingRepository ratingRepository;
  private final FoodRepository foodRepository;

  public FoodRatingRecalculationService(
      RatingRepository ratingRepository, FoodRepository foodRepository) {
    this.ratingRepository = ratingRepository;
    this.foodRepository = foodRepository;
  }

  @Async("ratingTaskExecutor")
  @Transactional
  public void recalculateAverageRating(UUID foodId) {
    double average = ratingRepository.findAverageRating(foodId);
    Food food =
        foodRepository
            .findById(foodId)
            .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Food not found"));
    food.setRating(average);
  }
}
