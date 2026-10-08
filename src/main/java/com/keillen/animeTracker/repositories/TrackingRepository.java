package com.keillen.animeTracker.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.keillen.animeTracker.entities.Tracking;
import com.keillen.animeTracker.entities.TrackingId;

/**
 * Database access for tracking rows
 * The ID type is TrackingId because the primary key is the (user, media) pair
 */

public interface TrackingRepository extends JpaRepository<Tracking, TrackingId>{
    
}
