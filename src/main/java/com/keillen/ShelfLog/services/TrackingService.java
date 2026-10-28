package com.keillen.ShelfLog.services;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.keillen.ShelfLog.dto.PageResponse;
import com.keillen.ShelfLog.dto.TrackingResponse;
import com.keillen.ShelfLog.dto.TrackingSummary;
import com.keillen.ShelfLog.dto.UpdateTrackingRequest;
import com.keillen.ShelfLog.entities.Format;
import com.keillen.ShelfLog.entities.Media;
import com.keillen.ShelfLog.entities.Status;
import com.keillen.ShelfLog.entities.Tracking;
import com.keillen.ShelfLog.entities.TrackingId;
import com.keillen.ShelfLog.repositories.MediaRepository;
import com.keillen.ShelfLog.repositories.StatusRepository;
import com.keillen.ShelfLog.repositories.TrackingRepository;
import com.keillen.ShelfLog.repositories.UserRepository;

/**
 * Business logic for tracking.
 * Lets a user start tracking a title, and returns the existing row if they already are.
 * Lets them update a row
 * Lets them delete a row
 */

//Service is used to mark the class as a Spring-managed service, allowing it to be injected into other components.
@Service 
public class TrackingService {

    private final MediaRepository mediaRepository;
    private final TrackingRepository trackingRepository;
    private final StatusRepository statusRepository;
    private final UserRepository userRepository;


    public TrackingService(MediaRepository mediaRepository, TrackingRepository trackingRepository, 
        StatusRepository statusRepository, UserRepository userRepository){

        this.mediaRepository = mediaRepository;
        this.trackingRepository = trackingRepository;
        this.statusRepository = statusRepository;
        this.userRepository = userRepository;

    }

    //Rating converter
    private Double convertRating(Tracking tracking){

        //If it is null keep it null if not divide by 10 for a 1-10
        return tracking.getUserRating() == null ? null : tracking.getUserRating() / 10.0;

    }

    //Helper for a response
    private TrackingResponse toResponse(Tracking tracking){

        //Created and return a Tracking response
        return new TrackingResponse(tracking.getMedia().getId(), tracking.getMedia().getTitle(), 
            tracking.getMedia().getFormat(), tracking.getStatus().getName(), 
            tracking.getCurrentEpPage(), convertRating(tracking));
    }

    //Helper for a tracking summary
    private TrackingSummary toSummary(Tracking tracking){

        //Create a TrackingSummary
        return new TrackingSummary(tracking.getMedia().getId(), tracking.getMedia().getTitle(),
            tracking.getMedia().getFormat(), tracking.getMedia().getImageUrl(),
            tracking.getStatus().getName(), tracking.getCurrentEpPage(), convertRating(tracking));
            
    }
    

    //No Transactional so that we can check if it has been double pressed and if so then return the row
    //Reason is the insert on Transactional runs after the return not the save
    //Also when it is returned and fails you can not change it at that point
    public TrackingResponse start(Integer userId, Integer mediaId){

        //Check if media is valid or not real
        Media media = mediaRepository.findById(mediaId).orElseThrow(() -> 
            new ResponseStatusException(HttpStatus.NOT_FOUND, "Media not found"));

        //Do the same for Status will be used if no row exists
        Status status = statusRepository.findByName("plan_to_start").orElseThrow(() -> 
            new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Status not found"));
        
        TrackingId trackingId = new TrackingId(userId, mediaId);

        Optional<Tracking> tracking = trackingRepository.findById(trackingId);

        if (tracking.isPresent()){
            //Use .get() because we know there is a vaule in there and it is not empty
            return toResponse(tracking.get());
        }


        //If it does not exist create a new tracking row
        Tracking newTracking = new Tracking();

        //Create a referance to the existing user by ID without loading the full user
        //This links the user to the tracking row
        newTracking.setUser(userRepository.getReferenceById(userId));
        newTracking.setMedia(media);
        newTracking.setStatus(status);
        //Becuase we use it later assign it to a variable so when it runs you use the results
        try{

            //Set the last updated date adn time
            newTracking.setLastUpdated(LocalDateTime.now());

            //Save the contents and insert into the database
            Tracking result = trackingRepository.saveAndFlush(newTracking);

            //Return through the helper
            return toResponse(result);
        } catch(DataIntegrityViolationException e){
            //return the row that already exists by the treacking id and user id used
            return trackingRepository.findById(trackingId).map(this::toResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT, 
                    "Data integrity violation occurred, but row could not be retrieved.", e));
        }

    }

    //Transactional because it will read change then save all together at the end nothing needs to be checked inbetwen
    @Transactional
    public TrackingResponse update(Integer userId, Integer mediaId, UpdateTrackingRequest request){

        //Create a tracking id from the probided id's
        TrackingId trackingId = new TrackingId(userId, mediaId);
        //Look it up and see if it is real if not then throw a 404 Not Found
        Tracking tracking = trackingRepository.findById(trackingId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Row does not exist"));

        //Handles Status
        if(request.status() != null){
            //Find the status that is used in the request
            Status status = statusRepository.findByName(request.status())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status is unknown"));
            //Override and set the status
            tracking.setStatus(status);
        }

        //Handles Current Ep Page
        if(request.currentEpPage() != null){
            //Override and set the new number
            tracking.setCurrentEpPage(request.currentEpPage());
        }

        //Handles user Rating
        if(request.userRating() != null){
            //Convert to 0-100 for the table
            //Casted to an Int
            Integer rating = (int) Math.round(request.userRating() * 10);

            //Override and set the new Rating
            tracking.setUserRating(rating);
        }

        //Update last updated
        tracking.setLastUpdated(LocalDateTime.now());

        //Save the changes the return them to be displayed
        Tracking result = trackingRepository.save(tracking);
        return toResponse(result);
    }


    //Delete a row in tracking
    public void delete(Integer userId, Integer mediaId){
        //Create a trackingid used to delete the row
        TrackingId trackingId = new TrackingId(userId, mediaId);
        // Check if it exists if not throw a 404
        //Better because it does not load the object it just checks if it is there
        if (!trackingRepository.existsById(trackingId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Row does not exist");
        }
        //Delete the row by the Id given
        trackingRepository.deleteById(trackingId);
    }

    //Read only
    @Transactional(readOnly = true)
    //This method will return one page plus how many are avaliable
    public PageResponse<TrackingSummary> list(Integer userId, String status, Format format, int page, int limit){

        //The most items allowed on one page (50 pages)
        int size = Math.min(Math.max(limit, 1), 50);

        //How to sort
        //First by most recently updated and then if there is a tie then by id
        Pageable pageable = PageRequest.of(Math.max(page, 1) - 1, size, Sort.by(Sort.Order.desc("lastUpdated").nullsLast(), Sort.Order.asc("id.mediaId")));

        //The rows on the requested page that match the filters
        Page<Tracking> pageResult = trackingRepository.search(userId, status, format, pageable);

        //Map this to the DTO and then return it
        List<TrackingSummary> summaries = pageResult
            .map(this::toSummary)
            .getContent();

        //Return the pages items plus the totals with the page numbers starting at 1
        return new PageResponse<>(
            summaries,
            pageResult.getTotalElements(),
            pageResult.getTotalPages(),
            pageResult.getNumber() + 1
        );
    }
}
