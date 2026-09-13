package com.suman.foodexpress.restaurants.service;

import com.suman.foodexpress.auth.entity.User;
import com.suman.foodexpress.auth.exception.ApiException;
import com.suman.foodexpress.common.enums.SortOrder;
import com.suman.foodexpress.restaurants.dto.BankDetailsRequest;
import com.suman.foodexpress.restaurants.dto.RestaurantPaginationDto;
import com.suman.foodexpress.restaurants.dto.RestaurantRequest;
import com.suman.foodexpress.restaurants.dto.RestaurantSortBy;
import com.suman.foodexpress.restaurants.entity.BankDetails;
import com.suman.foodexpress.restaurants.entity.Restaurant;
import com.suman.foodexpress.restaurants.repository.RestaurantsRepository;
import java.util.UUID;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@CacheConfig(cacheNames = "restaurantList")
public class RestaurantService {

  private final RestaurantsRepository restaurantsRepository;

  public RestaurantService(RestaurantsRepository restaurantsRepository) {
    this.restaurantsRepository = restaurantsRepository;
  }

  @Cacheable(
      key =
          "#restaurantPaginationDto.page + ':' + #restaurantPaginationDto.limit + ':' +"
              + " #restaurantPaginationDto.search + ':' + #restaurantPaginationDto.sortBy + ':' +"
              + " #restaurantPaginationDto.sortOrder")
  public Page<Restaurant> getAll(RestaurantPaginationDto restaurantPaginationDto) {

    Sort sort =
        Sort.by(
            restaurantPaginationDto.getSortOrder() == SortOrder.ASC
                ? Sort.Direction.ASC
                : Sort.Direction.DESC,
            getSortProperty(restaurantPaginationDto.getSortBy()));

    Pageable pageable =
        PageRequest.of(
            restaurantPaginationDto.getPage() - 1, restaurantPaginationDto.getLimit(), sort);

    return restaurantsRepository.search(restaurantPaginationDto.getSearch(), pageable);
  }

  @Transactional
  @CacheEvict(allEntries = true)
  public Restaurant create(RestaurantRequest request, User owner) {
    if (restaurantsRepository.existsByOwner_Id(owner.getId())) {
      throw new ApiException(HttpStatus.CONFLICT, "User already owns a restaurant");
    }

    Restaurant restaurant =
        Restaurant.builder()
            .email(request.email().trim().toLowerCase())
            .name(request.name().trim())
            .profileImageUrl(request.profileImageUrl())
            .owner(owner)
            .fssaiNumber(request.fssaiNumber())
            .gstNumber(request.gstNumber())
            .contractAcceptedAt(request.contractAcceptedAt())
            .bankDetails(toBankDetails(request.bankDetails()))
            .build();

    return restaurantsRepository.save(restaurant);
  }

  public Restaurant getByOwner(UUID ownerId) {
    return restaurantsRepository
        .findByOwner_Id(ownerId)
        .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Restaurant not found"));
  }

  private BankDetails toBankDetails(BankDetailsRequest request) {
    if (request == null) {
      return null;
    }
    return BankDetails.builder()
        .accountHolderName(request.accountHolderName())
        .accountNumber(request.accountNumber())
        .ifscCode(request.ifscCode())
        .bankName(request.bankName())
        .build();
  }

  private String getSortProperty(RestaurantSortBy sortBy) {
    return switch (sortBy) {
      case NAME -> "name";
      case CREATED_AT -> "createdAt";
    };
  }
}
