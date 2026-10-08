package com.keillen.animeTracker.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Recommend a title to another user
 * Will send the media along with a note if the sender wants to send one
 */

@Entity 
@Table(name = "send_recommendation")
public class SendRecommendation {
    
    public SendRecommendation(){}

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

    @ManyToOne 
    @JoinColumn(name = "media_id", nullable=false)
    private Media media;

    @Column(columnDefinition= "TEXT")
    private String note;

    @Column(nullable=false)
    private boolean seen;

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

    public Media getMedia(){
        return media;
    }

    public void setMedia(Media media){
        this.media = media;
    }

    public String getNote(){
        return note;
    }

    public void setNote(String note){
        this.note = note;
    }

    public boolean isSeen(){
        return seen;
    }

    public void setSeen(boolean seen){
        this.seen = seen;
    }
}
