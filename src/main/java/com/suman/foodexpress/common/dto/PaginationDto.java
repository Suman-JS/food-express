package com.suman.foodexpress.common.dto;

import com.suman.foodexpress.common.enums.SortOrder;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaginationDto {

  @Min(value = 1, message = "Page must be greater than or equal to 1")
  private int page = 1;

  @Min(value = 1, message = "Limit must be greater than or equal to 1")
  @Max(value = 100, message = "Limit must not exceed 100")
  private int limit = 10;

  private SortOrder sortOrder = SortOrder.DESC;

  private String search;
}
