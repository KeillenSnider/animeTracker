package com.keillen.ShelfLog.dto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Data transfer object
 *  What a client may send to /register
 * Only these three fields are accepted, so nobody can set things like id or created.
 */

  
//Create the records for the request and response
public record RegisterRequest(
    @NotBlank @Size(min = 3, max = 30) String username,
    @NotBlank @Size(min = 8, max = 72) String password,
    @NotBlank @Email String email
){}
