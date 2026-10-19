package com.keillen.ShelfLog.dto;
import java.util.List;

/**
 * One title as AniList describes it
 * This is AniList's shape it stays separate from the Media entity
 * MediaImportService translates it (format, tags, HTML description) before saving
 * Integer is used for averageScore because AniList sends null for unrated titles
 */


public record AniListMedia(
    Integer id,
    Title title,
    String format,
    Integer averageScore,
    List<String> genres,
    List<Tag> tags,
    String description,
    CoverImage coverImage
) {
    public record Title(String romaji) {}
    public record Tag(String name, Boolean isAdult) {}
    public record CoverImage(String large) {}
}