package com.keillen.ShelfLog.controllers;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.keillen.ShelfLog.dto.PageResponse;
import com.keillen.ShelfLog.dto.MediaDetail;
import com.keillen.ShelfLog.dto.MediaSummary;
import com.keillen.ShelfLog.services.MediaService;
import com.keillen.ShelfLog.entities.Format;

/**
 * Controller for Media endpoints
 */

//Methods are answering HTTP request and return values become JSON
@RestController
public class MediaController {
    
    private final MediaService mediaService;

    public MediaController(MediaService mediaService){

        this.mediaService = mediaService;

    }

    @GetMapping("/media/{id}")
    //Use pathvariable to get the id from the url to use in the method
    public MediaDetail getMedia(@PathVariable Integer id){
        return mediaService.getDetail(id);
    }

    //Get all the media that is related to the search
    @GetMapping("/media")
    public PageResponse<MediaSummary> listMedia(
        //False = optional
        @RequestParam(required = false) String name,
        @RequestParam(required = false) Format format,
        @RequestParam(required = false) String genre,
        @RequestParam(required = false) String tag,
        //How many media can be on one page
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "30") int limit
    ){
        return mediaService.list(name, format, genre, tag, page, limit);
    }

}
