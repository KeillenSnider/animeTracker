package com.keillen.ShelfLog.controllers;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.keillen.ShelfLog.dto.PageResponse;
import com.keillen.ShelfLog.dto.TrackingResponse;
import com.keillen.ShelfLog.dto.TrackingSummary;
import com.keillen.ShelfLog.dto.UpdateTrackingRequest;
import com.keillen.ShelfLog.entities.Format;
import com.keillen.ShelfLog.services.TrackingService;

import jakarta.validation.Valid;

/**
 * Controller for Tracking endpoints
 */

//Methods are answering HTTP request and return values become JSON
@RestController
public class TrackingController {
    
    private final TrackingService trackingService;

    public TrackingController(TrackingService trackingService){
        this.trackingService = trackingService;
    }

    //Start Tracking
    @PostMapping("/media/{id}/tracking")
    //PathVariable takes the id from the url
    //Auth gets the auths urers id to use here
    public TrackingResponse startTracking(@PathVariable Integer id, @AuthenticationPrincipal Integer userId){
        return trackingService.start(userId, id);
    }

    //Update
    @PatchMapping("/media/{id}/tracking")
    //@Vaild to inforce the constraints in the DTO
    public TrackingResponse updateTracking(@PathVariable Integer id, @AuthenticationPrincipal Integer userId, 
        @Valid @RequestBody UpdateTrackingRequest request){
            return trackingService.update(userId, id, request);
    }
    
    //Delete
    @DeleteMapping("/media/{id}/tracking")
    //Change it from a 200 OK to 204 No conetent because it deleted and there is nothing to be sent back
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTracking(@PathVariable Integer id, @AuthenticationPrincipal Integer userId){
        //Run the delete
        trackingService.delete(userId, id);
    }

    //Get the page requested and also how many are avaliable
    @GetMapping("/my-tracking")
    public PageResponse<TrackingSummary> listTracking(
        //False = optional
        //pass the userId from the token
        @AuthenticationPrincipal Integer userId,
        @RequestParam(required = false) String status,
        @RequestParam(required = false) Format format,
        //How many media can be on one page
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "30") int limit
    ){
        //Call the method in service
        return trackingService.list(userId, status, format, page, limit);
    }
}
