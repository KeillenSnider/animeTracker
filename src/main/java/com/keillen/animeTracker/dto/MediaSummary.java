package com.keillen.animeTracker.dto;
import com.keillen.animeTracker.entities.Format;

/**
 * Used to hold the data for the media objects in the List page
 */


public record MediaSummary (
    
    Integer id,
    String title,
    Format format,
    String imageUrl,
    Double rating

){}
