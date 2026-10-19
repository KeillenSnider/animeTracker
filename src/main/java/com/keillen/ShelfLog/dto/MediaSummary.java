package com.keillen.ShelfLog.dto;
import com.keillen.ShelfLog.entities.Format;

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
