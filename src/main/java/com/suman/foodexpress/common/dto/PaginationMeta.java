package com.suman.foodexpress.common.dto;

public record PaginationMeta(
    long total, int page, int limit, boolean hasNextPage, boolean hasPreviousPage) {}
