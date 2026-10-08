package com.keillen.animeTracker.entities;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * A tracking status such as in_progress or completed
 * Kept as a table instead of an enum so new statuses can be added without a schema change
 * Rows are seeded by StatusSeeder
 */

@Entity 
@Table(name = "status")
public class Status {
    
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique= true, nullable= false)
    private String name;

    public Status(){}

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
}
