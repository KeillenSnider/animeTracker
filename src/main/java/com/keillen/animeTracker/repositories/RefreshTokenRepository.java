package com.keillen.animeTracker.repositories;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.keillen.animeTracker.entities.RefreshToken;

/**
 * Database access for refresh tokens
 * findByToken is how /refresh and /logout locate the stored token from the string the client sends.
 */

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Integer>{
    
    //Finds the stored row for a token string
    Optional<RefreshToken> findByToken(String token);
}
