package com.keillen.ShelfLog.entities;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

/**
 * More detailed descriptor than a genre
 * Linked to Media through the media_tag join table
 * Owned on Media side
 */

@Entity
@Table(name = "tag")
public class Tag {
    
    @Id 
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique= true, nullable= false)
    private String name;

    public Tag(){
    }

    //Getters and Setters
    public Integer getId(){
        return id;
    }

    public void setId(Integer id){
        this.id = id;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }


    //Says the relationship is defined in media look there for the join table
    @ManyToMany(mappedBy = "tags")
    //Creates a list of media that starts as empty
    private List<Media> mediaList = new ArrayList<>();

    public List<Media> getMediaList(){
        return mediaList;
    }

    public void setMediaList(List<Media> mediaList){
        this.mediaList = mediaList;
    }
}
