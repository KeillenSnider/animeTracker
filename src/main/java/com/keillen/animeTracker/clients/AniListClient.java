package com.keillen.animeTracker.clients;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.keillen.animeTracker.dto.AniListMedia;
import com.keillen.animeTracker.dto.AniListResponse;

/**
 * Calls AniList's GraphQL API and returns its raw reply. It only fetches;
 * translating and saving is MediaImportService's job.
 */

//Component is used to mark the class as a Spring-managed bean, allowing it to be injected into other components.
@Component
public class AniListClient {

    //Used to make HTTP requests to the AniList GraphQL API. The base URL is set to "https://graphql.anilist.co".
    private final RestClient restClient = RestClient.create("https://graphql.anilist.co");

    //$variables are filled in per request, so one query serves all three types
    //Used to define the GraphQL query that will be sent to the AniList API. The query takes several parameters, 
    // including page number, items per page, media type, format, format exclusion, and sorting options. 
    // It retrieves media information such as ID, title, format, average score, genres, tags, description, and cover image.
    private static final String QUERY = """
        query ($page: Int, $perPage: Int, $type: MediaType, $format: MediaFormat,
               $formatNot: MediaFormat, $sort: [MediaSort]) {
          Page(page: $page, perPage: $perPage) {
            media(type: $type, format: $format, format_not: $formatNot, sort: $sort, isAdult: false) {
              id title { romaji } format averageScore genres tags { name isAdult }
              description coverImage { large }
            }
          }
        }
        """;

    //used to fetch a page of media from the AniList API based on the provided parameters. 
    // It constructs a map of variables, sends a POST request to the AniList API with the query and variables, 
    // and retrieves the response. The method returns a list of AniListMedia objects extracted from the response.
    public List<AniListMedia> fetchPage(String type, String format, String formatNot, String sort, int page, int perPage) {
        //Used to store the variables that will be sent with the GraphQL query. The keys correspond to the variable names 
        // in the query, and the values are provided as method parameters.
        Map<String, Object> variables = new HashMap<>();
        variables.put("page", page);
        variables.put("perPage", perPage);
        variables.put("type", type);
        // Only include format if it's provided (null means no filter)
        if (format != null) {
          variables.put("format", format);
        }
        // Only include formatNot if it's provided (null means no exclusion)
        if (formatNot != null) {
          variables.put("formatNot", formatNot);
        }
        // Sort order for the query results
        variables.put("sort", List.of(sort));

        //Send the POST request to the AniList API with the query and variables, and retrieve the response.
        AniListResponse response = restClient.post()
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("query", QUERY, "variables", variables))
                .retrieve()
                //Fill in the record with the data that is given from the API
                .body(AniListResponse.class);

        //return the list of AniListMedia objects extracted from the response. 
        // The response is expected to contain a Page object, which in turn contains a list of media items.
        //If it is empty then fail with a message
        if (response == null || response.data() == null || response.data().Page() == null) {
          throw new IllegalStateException("AniList returned no data");
        }
        return response.data().Page().media();
    }
}