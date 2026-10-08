package com.keillen.animeTracker.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.keillen.animeTracker.clients.AniListClient;
import com.keillen.animeTracker.dto.AniListMedia;
import com.keillen.animeTracker.entities.Format;
import com.keillen.animeTracker.entities.Genre;
import com.keillen.animeTracker.entities.Media;
import com.keillen.animeTracker.entities.Tag;
import com.keillen.animeTracker.repositories.GenreRepository;
import com.keillen.animeTracker.repositories.MediaRepository;
import com.keillen.animeTracker.repositories.TagRepository;


/**
 * Pulls popular titles from AniList and saves them to our database.
 * New titles are inserted, existing ones are updated, nothing is deleted.
 * A failed request or title is logged and skipped so the rest still import.
 */

//Service is used to mark the class as a Spring-managed service, allowing it to be injected into other components.
@Service
public class MediaImportService {

    //Logger is used to log messages for debugging and monitoring purposes
    private static final Logger log = LoggerFactory.getLogger(MediaImportService.class);
    //Gives it 3000 tiles across the 3 types. Can chnage later to get more or less.
    private static final int PAGES_PER_TYPE = 20;
    //The number of items to fetch per page from the API
    private static final int PER_PAGE = 50;
    //Wait between requests so AniList doesn't rate-limit
    private static final long DELAY_MS = 3000;

    //One import job = one kind of title to fetch
    //which media to fetch then which format to include and exclude
    private record ImportJob(String type, String format, String formatNot) {}

    //Make variables for all the repositories and clients that will be used to fetch and save data
    private final AniListClient aniListClient;
    private final MediaRepository mediaRepository;
    private final GenreRepository genreRepository;
    private final TagRepository tagRepository;

    //Inject them into the constructor so they can be used in the service
    public MediaImportService(AniListClient aniListClient, MediaRepository mediaRepository,
                              GenreRepository genreRepository, TagRepository tagRepository) {
        this.aniListClient = aniListClient;
        this.mediaRepository = mediaRepository;
        this.genreRepository = genreRepository;
        this.tagRepository = tagRepository;
    }

    //Map the API format to my format ENUM
    private Format toFormat(String type, String format) {
        if ("ANIME".equals(type)) {
            return Format.ANIME;
        }
        if ("NOVEL".equals(format)) {
            return Format.LIGHT_NOVEL;
        }
        return Format.MANGA;
    }

    //Use the genre if it exists, otherwise create it
    private Genre getOrCreateGenre(String name) {
        return genreRepository.findByName(name).orElseGet(() -> {
            Genre genre = new Genre();
            genre.setName(name);
            return genreRepository.save(genre);
        });
    }

    //Same as Genre
    private Tag getOrCreateTag(String name) {
        return tagRepository.findByName(name).orElseGet(() -> {
            Tag tag = new Tag();
            tag.setName(name);
            return tagRepository.save(tag);
        });
    }

    //AniList's description has HTML, so turn <br> into new lines and strip other tags
    private String cleanDescription(String html) {
        if (html == null) {
            return null;
        }
        return html.replaceAll("(?i)<br\\s*/?>", "\n")
                   .replaceAll("<[^>]+>", "")
                   .trim();
    }

    //Saves a single media item to the database, either by updating an existing record or creating a new one
    private void saveOne(AniListMedia item, String type) {

        //Skip titles AniList marks as adult by tag, and anything labeled Hentai
        boolean hasAdultTag = item.tags() != null && item.tags().stream().anyMatch(t -> Boolean.TRUE.equals(t.isAdult()));
        boolean hasHentai = item.genres() != null && item.genres().contains("Hentai");
        //Skip it
        if (hasAdultTag || hasHentai) {
            return;
        }

        //Convert the AniList ID to a string to use as a reference ID in our database
        String refId = String.valueOf(item.id());

        // Anime and manga and Light novels share ID ranges, so give them different sources to avoid collisions
        String source = "ANIME".equals(type) ? "anilist-anime" : "NOVEL".equals(item.format()) ? "anilist-novel" : "anilist-manga";

        // Reuse existing row if it exists for this specific source
        Media media = mediaRepository.findByRefIdAndSource(refId, source).orElseGet(Media::new);

        //Set all the variables for the media based on the data from the API
        media.setRefId(refId);
        media.setSource(source);
        media.setTitle(item.title().romaji());
        media.setFormat(toFormat(type, item.format()));
        media.setDescription(cleanDescription(item.description()));
        media.setImageUrl(item.coverImage() == null ? null : item.coverImage().large());
        media.setApiRating(item.averageScore());
        media.setRefreshedDate(LocalDate.now());

        //Create a list of genres
        List<Genre> genres = new ArrayList<>();
        //If a genre exists use it if not create it from the API strings
        if (item.genres() != null) {
            //Loop through all genres
            for (String name : item.genres()) {
                genres.add(getOrCreateGenre(name));
            }
        }
        //Save the genres to the object
        media.setGenres(genres);

        //Create a list of tags
        List<Tag> tags = new ArrayList<>();
        //Same as Genre
        if (item.tags() != null) {
            for (AniListMedia.Tag t : item.tags()) {
                tags.add(getOrCreateTag(t.name()));
            }
        }
        //Save the tags to the object
        media.setTags(tags);

        //Save the media to the database
        mediaRepository.save(media);
    }

    //Waits a bit, fetches a page, and retries once after a minute if AniList says no
    private List<AniListMedia> fetchWithRetry(ImportJob job, int page) throws InterruptedException {
        //Have a pause at the beggining of every request
        Thread.sleep(DELAY_MS);
        //Try to fetch a page from the api
        try {
            return aniListClient.fetchPage(job.type(), job.format(), job.formatNot(),
                "POPULARITY_DESC", page, PER_PAGE);
        } catch (Exception e) {
            //Log a error wait a minute then try again
            log.warn("Fetch failed for {} page {}, retrying in 60s: {}", job.type(), page, e.getMessage());
            Thread.sleep(60_000);
            //If it still does not work after a minute then skip this page and go to the next
            return aniListClient.fetchPage(job.type(), job.format(), job.formatNot(), "POPULARITY_DESC", page, PER_PAGE);
        }
    }

    //Import all the titles from AniList and save them to the database
    public void importAll() {

        //Define the import jobs for different media types and formats
        List<ImportJob> jobs = List.of(
            //Fetch anime
            new ImportJob("ANIME", null, null),
            //Fetch manga but exclude novels
            new ImportJob("MANGA", null, "NOVEL"),
            //Fetch novels only
            new ImportJob("MANGA", "NOVEL", null));

        //Loop through each import job and fetch the specified media from AniList
        for (ImportJob job : jobs) {
            //Fetch multiple pages of media for each job, up to the defined number of pages
            for (int page = 1; page <= PAGES_PER_TYPE; page++) {
                //Create a list to hold the fetched media items
                List<AniListMedia> items;
                //Try to run the fetch if it fails then log the error and continue to the next page
                try {
                    items = fetchWithRetry(job, page);
                } catch (InterruptedException e) {
                    //If it gets interupted reset the flag and stop the import
                    Thread.currentThread().interrupt();
                    return;
                } catch (Exception e) {
                    //Skip and keep going 
                    log.warn("Fetch failed for {} page {}: {}", job.type(), page, e.getMessage());
                    continue;
                }

                //Log how many title came back per page
                log.info("Fetched {} titles for {} page {}", items.size(), job.type(), page);

                //Loop through each fetched media item and save it to the database
                for (AniListMedia item : items) {
                    try {
                        saveOne(item, job.type());
                    } catch (Exception e) {
                        //If it fails send error to importAll to handle it
                        log.warn("Could not save title {}: {}", item.id(), e.getMessage());
                    }
                }
            }
        }
    }
}