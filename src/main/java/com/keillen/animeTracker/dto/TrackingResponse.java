package com.keillen.animeTracker.dto;

import com.keillen.animeTracker.entities.Format;
import com.keillen.animeTracker.entities.Status;

/**
 * TrackingResponse is a data transfer object
 */

public record TrackingResponse(
    String mediaId,
    String title,
    Format format,
    Status status,
    Integer currentEpPage,
    Integer userRating
){}