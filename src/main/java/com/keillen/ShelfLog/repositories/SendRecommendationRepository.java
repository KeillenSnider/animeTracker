package com.keillen.ShelfLog.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.keillen.ShelfLog.entities.SendRecommendation;

/**
 * Database access for recommendations sent between users
 * Uses only the built-in methods for now
 */

public interface SendRecommendationRepository extends JpaRepository<SendRecommendation, Integer>{
    
}
