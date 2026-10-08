package com.keillen.animeTracker.entities;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/**
 * A friend request from one user to another
 * The status tracks whether it is pending, accepted, or denied
 */

@Entity 
//Make a rule saying no two rows can have the same combination of sender and receiver
@Table(name = "friend_request",uniqueConstraints = @UniqueConstraint(columnNames = {"sender_id", "receiver_id"}))
public class FriendRequest {

    public FriendRequest(){}
    
    //Make as a primary key and auto increment
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne 
    @JoinColumn(name = "sender_id", nullable=false)
    private User sender;

    @ManyToOne 
    @JoinColumn(name = "receiver_id", nullable=false)
    private User receiver;

    //Get it to pull from the FriendStatus file to find out what types are allowed
    @Enumerated(EnumType.STRING)
    @Column(name = "status",nullable=false)
    //Start it off as pending
    private FriendStatus status = FriendStatus.PENDING;

    @Column(nullable=false)
    private LocalDate created;

    //Getters and Setters

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User getSender(){
        return sender;
    }

    public void setSender(User user){
        this.sender = user;
    }

    public User getReceiver(){
        return receiver;
    }

    public void setReceiver(User user){
        this.receiver = user;
    }

    public FriendStatus getFriendStatus(){
        return status;
    }

    public void setFriendStatus(FriendStatus status){
        this.status = status;
    }

    public LocalDate getCreated(){
        return created;
    }

    public void setCreated(LocalDate created){
        this.created = created;
    }
}
