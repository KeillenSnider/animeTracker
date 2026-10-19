package com.keillen.ShelfLog.repositories;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.keillen.ShelfLog.entities.Genre;

/**
 * Database access for genres
 * findByName lets the AniList import/reuse an existing genre instead of inserting a duplicate
 */

public interface GenreRepository extends JpaRepository<Genre, Integer>{
    
    //Finds a genre by its unique name
    Optional<Genre> findByName(String name);
}
