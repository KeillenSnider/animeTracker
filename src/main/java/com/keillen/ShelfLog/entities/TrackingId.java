package com.keillen.ShelfLog.entities;
import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

/**
 * The composite primary key for tracking
 * one row per (user, media) pair
 */

//Makes this class as a composite key
//This is not an entity but a value object that will be embedded inside another entity
@Embeddable 
public class TrackingId implements Serializable{
    
    //Make the name stated explicitly so they will always match because if not it might be random

    @Column (name = "user_id")
    private Integer userId;

    @Column (name = "media_id")
    private Integer mediaId;

    //Constructors
    public TrackingId(){}

    public TrackingId(Integer userId, Integer mediaId){
        this.userId = userId;
        this.mediaId = mediaId;
    }

    //Getters and Setter

    public Integer getUserId(){
        return userId;
    }

    public void setUserId(Integer userId){
        this.userId = userId;
    }

    public Integer getMediaId(){
        return mediaId;
    }

    public void setMediaId(Integer mediaId){
        this.mediaId = mediaId;
    }



    //Hash is used first to see if there is anything that matches in memory then equals to confirm

    //Instead of checking if the memory address is the same we want to check if the userId and mediaId is the same as another row
    //This is why we need to override equals and hashcode because hibernate uses them to check if they are the same row
    @Override
    //Receive an object to compare with this Tracking Id
    //Example id1.equals(id2) = this.equals(that) = this.equals(object o)
    public boolean equals(Object o) {
        //If they are at the same place in memory they are the saem so return true
        if (this == o) return true;
        //If it is not an instance of TrackingID they can not be the same so return false
        //If it is a TrackingId then cast it to TrackingId with the name that
        //Casting is when we take the object we are given and chnage it to type Tracking Id
        if (!(o instanceof TrackingId that)) return false;
        //Return if they match or do not
        return Objects.equals(userId, that.userId) && Objects.equals(mediaId, that.mediaId);
    }

    //Override so that objects that are equal have the same hash value
    @Override
    public int hashCode() {
        //generate a hash based on both fields
        return Objects.hash(userId, mediaId);
    }
}
