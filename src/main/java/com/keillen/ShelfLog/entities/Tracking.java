package com.keillen.ShelfLog.entities;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

/**
 * Tracks one user's progress on one media
 *Identified by the (user, media) pair
 */

@Entity 
@Table (name = "tracking")
public class Tracking {
    
    //Constructor
    public Tracking(){}

    //The primary key is the whole TrackingId object
    @EmbeddedId 
    //Makes an empty key to fill in later
    private TrackingId id = new TrackingId();

    //Many Tracking rows belong to one user
    //Creates a foreign key
    @ManyToOne 
    //Says this variable is the userId part of the key
    @MapsId("userId")
    //Name the foreign key column
    @JoinColumn(name = "user_id")
    //Reference the User table as the foreign key
    private User user;

    //Same thing as above
    @ManyToOne 
    @MapsId("mediaId")
    @JoinColumn(name = "media_id")
    private Media media;

    //Make a foreign key to status
    @ManyToOne 
    @JoinColumn(name = "status_id", nullable = false)
    private Status status;

    @Column
    private Integer currentEpPage;

    @Column 
    @Min(1)
    @Max(100)
    private Integer userRating;


    //Getters and Setters

    public TrackingId getId(){
        return id;
    }

    public void setId(TrackingId id){
        this.id = id;
    }

    public User getUser(){
        return user;
    }

    public void setUser(User user){
        this.user = user;
    }

    public Media getMedia(){
        return media;
    }

    public void setMedia(Media media){
        this.media = media;
    }

    public Status getStatus(){
        return status;
    }

    public void setStatus(Status status){
        this.status = status;
    }

    public Integer getCurrentEpPage(){
        return currentEpPage;
    }
    
    public void setCurrentEpPage(Integer currentEpPage){
        this.currentEpPage = currentEpPage;
    }

    public Integer getUserRating(){
        return userRating;
    }

    public void setUserRating(Integer userRating){
        this.userRating = userRating;
    }
}
