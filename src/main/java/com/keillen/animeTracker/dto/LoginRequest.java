package com.keillen.animeTracker.dto;
import jakarta.validation.constraints.NotBlank;

/**
 * Data transfer object
 *  What a client may send to /login
 * Only these two fields are accepted, so nobody can set things like id or created.
 */

public record LoginRequest(

    @NotBlank String username,
    @NotBlank String password

){}
