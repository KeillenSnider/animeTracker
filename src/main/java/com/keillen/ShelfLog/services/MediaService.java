package com.keillen.ShelfLog.services;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import com.keillen.ShelfLog.entities.Format;
import com.keillen.ShelfLog.dto.MediaDetail;
import com.keillen.ShelfLog.dto.MediaSummary;
import com.keillen.ShelfLog.dto.PageResponse;
import com.keillen.ShelfLog.entities.Genre;
import com.keillen.ShelfLog.entities.Media;
import com.keillen.ShelfLog.entities.Tag;
import com.keillen.ShelfLog.repositories.MediaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import java.util.List;

/**
 * Used to get the information needed for the pages or details of 1 media
 */

//Service is used to mark the class as a Spring-managed service, allowing it to be injected into other components.
@Service 
public class MediaService {
    
    private final MediaRepository mediaRepository;


    public MediaService(MediaRepository mediaRepository){
        this.mediaRepository = mediaRepository;
    }

    private Double toRating(Integer apiRating){

        return apiRating == null ? null : apiRating / 10.0;

    }


    private MediaSummary toSummary(Media media){

        //Create and return a new DTO
        return new MediaSummary(media.getId(), media.getTitle(),media.getFormat(),media.getImageUrl(),toRating(media.getApiRating()));

    }

    private MediaDetail toDetail(Media media){

        //Create and return a new DTO
        return new MediaDetail(media.getId(), media.getTitle(),media.getFormat(),media.getImageUrl(),toRating(media.getApiRating()), media.getDescription(),
            media.getGenres().stream().map(Genre::getName).toList(),media.getTags().stream().map(Tag::getName).toList(),
            media.getRefreshedDate());

    }

    //Read only so no changes can be made
    @Transactional(readOnly = true)
    public MediaDetail getDetail(Integer id){

        //Get the media if not then throw a not found
        Media media = mediaRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Media not found"));
        //Return the DTO
        return toDetail(media);
    }


    //Read only so no changes can be made
    @Transactional(readOnly = true)
    public PageResponse<MediaSummary> list(String name, Format format, String genre, String tag, int page, int limit){

        //Make the limit of items on a page
        int size = Math.min(Math.max(limit, 1), 50);

        //build the search pattern
        String pattern = (name == null || name.isBlank()) ? null : "%" + name.toLowerCase() + "%";

        //Build the pagable
        //Spring starts at 0 but the API atarts at 1 so substract 1
        Pageable pageable = PageRequest.of(Math.max(page, 1) - 1, size, Sort.by(Sort.Order.desc("apiRating").nullsLast(), Sort.Order.asc("id")));

        //Call the Search Repository from Media repository
        Page<Media> pageResult = mediaRepository.search(pattern, format, genre, tag, pageable);

        //Convert each media entity to a summary DTO
        List<MediaSummary> summaries = pageResult
            //For every media summary DTO use toSummary(media)
            .map(this::toSummary)
            //Pulls the list of results out of the page object
            .getContent();

        //Return the page response
        return new PageResponse<>(
            //List of converted DTOs for the page
            summaries,
            //Total number of matching items
            pageResult.getTotalElements(),
            //Total number of pages based on the results and page size
            pageResult.getTotalPages(),
            //Spring starts at 0 so add one to start the count at 1
            pageResult.getNumber() + 1
        );
    }
}
