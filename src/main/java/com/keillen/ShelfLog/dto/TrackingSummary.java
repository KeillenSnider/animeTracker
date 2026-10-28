package com.keillen.ShelfLog.dto;
import com.keillen.ShelfLog.entities.Format;

/**
 * returns a tracking summary to display multiple titles
 */

public record TrackingSummary(

    Integer mediaId,
    String title,
    Format format,
    String imageUrl,
    String status,
    Integer currentEpPage,
    Double userRating

){}
