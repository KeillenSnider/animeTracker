package com.keillen.ShelfLog.entities;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * A registered account
 * Stores login information (passoword is a bycrypt hash, never plain text) and profile info like avatar and bio
 */

//Treats this class as a table in the database
@Entity
@Table(name = "users")
public class User {

    //Make as a primary key and auto increment
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    //Make it an Integer instead of int so it can start as a null value
    private Integer id;
    

    //Make a column that has to be unique and not null
    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(unique = true, nullable = false)
    private String email;

    @Column 
    private String avatarUrl;

    @Column 
    private String bio;

    //Will hold the date that the user was created
    @Column(nullable = false)
    private LocalDate created;

    //The reason for an empty constructor is because it needs a blank object then use the setters to create the object
    public User(){
    }

    //Getters and setters for all the fields

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public LocalDate getCreated() {
        return created;
    }

    public void setCreated(LocalDate created) {
        this.created = created;
    }
}
