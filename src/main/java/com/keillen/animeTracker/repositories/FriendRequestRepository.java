package com.keillen.animeTracker.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.keillen.animeTracker.entities.FriendRequest;

/**
 * Database access for friend requests
 * Uses only the built-in methods for now
 */

public interface FriendRequestRepository extends JpaRepository<FriendRequest, Integer>{
    
}
