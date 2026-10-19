package com.keillen.ShelfLog.entities;

/**
 * Fixed set of media types the app will support
 * Stored as text in the media table
 * Collapsed from AniList into these categories
 */

//Create the constraints for the format of the media
public enum Format {
    ANIME, MANGA, LIGHT_NOVEL
}
