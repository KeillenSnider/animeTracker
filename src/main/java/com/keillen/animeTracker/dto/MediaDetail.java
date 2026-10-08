package com.keillen.animeTracker.dto;
import java.time.LocalDate;
import java.util.List;

import com.keillen.animeTracker.entities.Format;

/**
 * Used to hold the data for the media objects in the Details page
 */

public record MediaDetail (
    
    Integer id,
    String title,
    Format format,
    String imageUrl,
    Double rating,
    String description,
    List<String> genres,
    List<String> tags,
    LocalDate refreshedDate

){}
