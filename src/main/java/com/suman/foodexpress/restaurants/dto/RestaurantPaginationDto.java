package com.suman.foodexpress.restaurants.dto;

import com.suman.foodexpress.common.dto.PaginationDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RestaurantPaginationDto extends PaginationDto {

  private RestaurantSortBy sortBy = RestaurantSortBy.CREATED_AT;
}
