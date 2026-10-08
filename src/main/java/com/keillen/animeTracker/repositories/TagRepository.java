package com.keillen.animeTracker.repositories;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.keillen.animeTracker.entities.Tag;

/**
 * Database access for tags
 * findByName lets the AniList import/reuse an existing tag instead of inserting a duplicate.
 */

public interface TagRepository extends JpaRepository<Tag, Integer>{
    
    //Finds a tag by its unique name
    Optional<Tag> findByName(String name);
}
