package com.keillen.ShelfLog.dto;

import com.keillen.ShelfLog.entities.Format;

/**
 * TrackingResponse is a data transfer object
 * returns a tracked row
 */

public record TrackingResponse(
    Integer mediaId,
    String title,
    Format format,
    String status,
    Integer currentEpPage,
    Double userRating
){}