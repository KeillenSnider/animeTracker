package com.keillen.animeTracker.entities;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * A long lived refresh token issued at login
 * Stored server side so it can be revoked on logout
 */

@Entity 
@Table(name = "refresh_token")
public class RefreshToken {

    public RefreshToken(){}
    
    //Make as a primary key and auto increment
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id",nullable=false)
    private User user;

    @Column(unique=true, nullable=false)
    private String token;

    @Column(nullable=false)
    private LocalDateTime expiresAt;

    @Column (nullable=false)
    private LocalDateTime created;

    @Column (nullable=false)
    private boolean revoked;


    //Getters and setters

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User getUser(){
        return user;
    }

    public void setUser(User user){
        this.user = user;
    }

    public String getToken(){
        return token;
    }

    public void setToken(String token){
        this.token = token;
    }

    public LocalDateTime getExpiresAt(){
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt){
        this.expiresAt = expiresAt;
    }

    public LocalDateTime getCreated(){
        return created;
    }

    public void setCreated(LocalDateTime created){
        this.created = created;
    }

    public boolean isRevoked(){
        return revoked;
    }

    public void setRevoked(boolean revoked){
        this.revoked = revoked;
    }
}
