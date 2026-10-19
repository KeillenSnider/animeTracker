package com.keillen.ShelfLog.repositories;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.keillen.ShelfLog.entities.User;

/**
 * Database access for user accounts
 * findByUsername is used by login
 * the exists methods let register reject duplicate usernames and emails.
 */

public interface UserRepository extends JpaRepository<User, Integer>{

    //Finds a user by username
    Optional<User> findByUsername(String username);

    //True if the username is taken
    boolean existsByUsername(String username);

    //True if the email is taken
    boolean existsByEmail(String email);
}