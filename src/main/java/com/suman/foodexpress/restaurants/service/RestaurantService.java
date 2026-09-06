package com.suman.foodexpress.restaurants.service;

import com.suman.foodexpress.restaurants.entity.Restaurant;
import com.suman.foodexpress.restaurants.repository.RestaurantsRepository;
import java.util.List;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class RestaurantService {
  private final RestaurantsRepository restaurantsRepository;

  public RestaurantService(RestaurantsRepository restaurantsRepository) {
    this.restaurantsRepository = restaurantsRepository;
  }

  @Cacheable(cacheNames = "restaurantList")
  public List<Restaurant> getAll() {
    return this.restaurantsRepository.findAll();
  }
}
