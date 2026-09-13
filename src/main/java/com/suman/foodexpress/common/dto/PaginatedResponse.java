package com.suman.foodexpress.common.dto;

import java.util.List;

public record PaginatedResponse<T>(PaginationMeta meta, List<T> items) {}
