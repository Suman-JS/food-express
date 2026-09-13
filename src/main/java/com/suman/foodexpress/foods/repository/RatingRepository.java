package com.suman.foodexpress.foods.repository;

import com.suman.foodexpress.foods.entity.Rating;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RatingRepository extends JpaRepository<Rating, UUID> {

  @Query("SELECT COALESCE(AVG(r.rating), 0.0) FROM Rating r WHERE r.food.id = :foodId")
  double findAverageRating(@Param("foodId") UUID foodId);

  @Query("SELECT r FROM Rating r WHERE r.food.id = :foodId AND r.user.id = :userId")
  Optional<Rating> findByFoodIdAndUserId(
      @Param("foodId") UUID foodId, @Param("userId") UUID userId);
}
