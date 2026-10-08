package com.keillen.animeTracker.config;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.keillen.animeTracker.entities.Status;
import com.keillen.animeTracker.repositories.StatusRepository;


/**
 * Seeds the default status values into the database at startup.
 * Only inserts the ones that are missing, so it is safe to run on every restart.
 */

//Tells Spring to create one of these at startup and manage it
@Component
//CommandLine Runner is a Springboot interface that runs code automatically at startup
public class StatusSeeder implements CommandLineRunner{

    //Used to read/write Status rows in the database
    private final StatusRepository statusRepository;

    //Constructor
    //Injects the repository into the config to give access to the database
    public StatusSeeder(StatusRepository statusRepository){
        this.statusRepository = statusRepository;
    }

    //Override the run
    @Override 
    //Creates an input of multiple Strings
    public void run(String... args){
        //Make a list of allowed statuses for the app to use
        List<String> names = List.of("in_progress", "completed", "dropped", "plan_to_start", "waiting");

        //Loop through all the names
        for(String name : names){
            //Check the database if this name is already in the database
            //findByName(name) returns Optional<Status>
            if(statusRepository.findByName(name).isEmpty()){
                //If it is create a new Status Object
                Status status = new Status();
                //Input the name into it's field
                status.setName(name);
                //Save the status in the table
                statusRepository.save(status);
            }
        }
    }
}