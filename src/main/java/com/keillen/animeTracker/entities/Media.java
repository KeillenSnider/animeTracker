package com.keillen.animeTracker.entities;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

/**
 * Defines a single anime, manga, or light novel
 * Data is imported from AniList
 * refId links back to the AniList entry'
 * refreshedDate records when it was last updated
 */

@Entity
@Table(name = "media")
public class Media{

    @Id 
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String title;

    //Get it to pull from the format file to find out what types are allowed
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Format format;

    @Column(columnDefinition= "TEXT")
    private String description;

    @Column
    private String imageUrl;

    @Column(nullable = false)
    private String refId;

    @Column
    private String source;

    @Column 
    private LocalDate refreshedDate;

    //Make sure it has a range
    @Column
    @Min(0)
    @Max(100)
    private Integer apiRating;

    //Blank Constructor
    public Media(){
    }

    //Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle(){
        return title;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public Format getFormat(){
        return format;
    }

    public void setFormat(Format format){
        this.format = format;
    }

    public String getDescription(){
        return description;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public String getImageUrl(){
        return imageUrl;
    }

    public void setImageUrl(String imageUrl){
        this.imageUrl = imageUrl;
    }

    public String getRefId(){
        return refId;
    }

    public void setRefId(String refId){
        this.refId = refId;
    }

    public String getSource(){
        return source;
    }

    public void setSource(String source){
        this.source = source;
    }

    public LocalDate getRefreshedDate(){
        return refreshedDate;
    }

    public void setRefreshedDate(LocalDate refreshedDate){
        this.refreshedDate = refreshedDate;
    }

    public Integer getApiRating(){
        return apiRating;
    }

    public void setApiRating(Integer apiRating){
        this.apiRating = apiRating;
    }

    //Make a Join table to create a many to many relationship 
    @ManyToMany
    //Foreign key for media that points to genre
    @JoinTable(name = "media_genre", joinColumns = @JoinColumn(name = "media_id"), inverseJoinColumns = @JoinColumn(name = "genre_id"))
    //Make sure it knows it is a list of genres so it has multiple entries and start as empty
    private List<Genre> genres = new ArrayList<>();

    public List<Genre> getGenres(){
        return genres;
    }

    public void setGenres(List<Genre> genres){
        this.genres = genres;
    }

    //Make one for tags
    @ManyToMany
    //Foreign key for media that points to genre
    @JoinTable(name = "media_tag", joinColumns = @JoinColumn(name = "media_id"), inverseJoinColumns = @JoinColumn(name = "tag_id"))
    //Make sure it knows it is a list of genres so it has multiple entries and start as empty
    private List<Tag> tags = new ArrayList<>();

    public List<Tag> getTags(){
        return tags;
    }

    public void setTags(List<Tag> tags){
        this.tags = tags;
    }
}