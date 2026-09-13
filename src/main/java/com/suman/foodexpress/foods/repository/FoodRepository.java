package com.suman.foodexpress.foods.repository;

import com.suman.foodexpress.foods.entity.Food;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FoodRepository extends JpaRepository<Food, UUID> {}
