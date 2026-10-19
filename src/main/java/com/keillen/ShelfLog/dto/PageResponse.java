package com.keillen.ShelfLog.dto;
import java.util.List;

//Wraps a page of results plus pagination info for list endpoints.
//T means any type
public record PageResponse<T>(List<T> results, long totalResults, int totalPages, int currentPage) {}
