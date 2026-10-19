package com.keillen.ShelfLog.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.keillen.ShelfLog.entities.Tracking;
import com.keillen.ShelfLog.entities.TrackingId;

/**
 * Database access for tracking rows
 * The ID type is TrackingId because the primary key is the (user, media) pair
 */

public interface TrackingRepository extends JpaRepository<Tracking, TrackingId>{
    
}
