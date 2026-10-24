package com.keillen.ShelfLog.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;

/**
 * Used to input update data into the tracking field without chnaging everything
 */

public record UpdateTrackingRequest(
    String status,
    //Min makes it so no negitaves
    @Min(0)
    Integer currentEpPage,
    //Keep it in the rating range
    @DecimalMin("1.0")
    @DecimalMax("10.0")
    Double userRating
) {}
