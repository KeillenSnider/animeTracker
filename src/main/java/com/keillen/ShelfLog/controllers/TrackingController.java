package com.keillen.ShelfLog.controllers;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.keillen.ShelfLog.dto.TrackingResponse;
import com.keillen.ShelfLog.services.TrackingService;

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

    @PostMapping("/media/{id}/tracking")
    //PathVariable takes the id from the url
    //Auth gets the auths urers id to use here
    public TrackingResponse startTracking(@PathVariable Integer id, @AuthenticationPrincipal Integer userId){
        return trackingService.start(userId, id);
    }
}
