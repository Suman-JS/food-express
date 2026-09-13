package com.suman.foodexpress.foods.service;

import com.suman.foodexpress.auth.entity.User;
import com.suman.foodexpress.auth.exception.ApiException;
import com.suman.foodexpress.foods.entity.Food;
import com.suman.foodexpress.foods.entity.Rating;
import com.suman.foodexpress.foods.repository.FoodRepository;
import com.suman.foodexpress.foods.repository.RatingRepository;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
public class RatingService {

  private final RatingRepository ratingRepository;
  private final FoodRepository foodRepository;
  private final FoodRatingRecalculationService recalculationService;

  public RatingService(
      RatingRepository ratingRepository,
      FoodRepository foodRepository,
      FoodRatingRecalculationService recalculationService) {
    this.ratingRepository = ratingRepository;
    this.foodRepository = foodRepository;
    this.recalculationService = recalculationService;
  }

  @Transactional
  public UUID submitRating(UUID foodId, User user, double rating) {
    Food food =
        foodRepository
            .findById(foodId)
            .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Food not found"));

    Rating ratingEntity =
        ratingRepository
            .findByFoodIdAndUserId(foodId, user.getId())
            .orElseGet(() -> Rating.builder().food(food).user(user).build());
    ratingEntity.setRating(rating);
    ratingRepository.save(ratingEntity);

    scheduleRecalculation(foodId);

    return ratingEntity.getId();
  }

  public double getAverageRating(UUID foodId) {
    return ratingRepository.findAverageRating(foodId);
  }

  private void scheduleRecalculation(UUID foodId) {
    if (TransactionSynchronizationManager.isSynchronizationActive()) {
      TransactionSynchronizationManager.registerSynchronization(
          new TransactionSynchronization() {
            @Override
            public void afterCommit() {
              recalculationService.recalculateAverageRating(foodId);
            }
          });
    } else {
      recalculationService.recalculateAverageRating(foodId);
    }
  }
}
