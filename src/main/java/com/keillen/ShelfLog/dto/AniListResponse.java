package com.keillen.ShelfLog.dto;
import java.util.List;


/**
 * Mirrors the outer layers of AniList's JSON reply (data -> Page -> media[])
 * Jackson fills these records automatically, so each field name must match
 * the JSON key exactly, including the capital P in Page
 * The nested records exist because the JSON is nested: one record per level.
 */

public record AniListResponse(Data data) {
    public record Data(Page Page) {}
    public record Page(List<AniListMedia> media) {}
}