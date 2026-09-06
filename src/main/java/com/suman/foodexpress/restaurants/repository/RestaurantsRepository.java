package com.suman.foodexpress.restaurants.repository;

import com.suman.foodexpress.restaurants.entity.Restaurant;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RestaurantsRepository extends JpaRepository<Restaurant, UUID> {}
