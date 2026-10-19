package com.keillen.ShelfLog.dto;

/**
 * Data transfer object
 * Tokens returned to the client after a successful login or registration.
 */

//Create the record for the response
public record AuthResponse(
    String accessToken,
    String refreshToken
){}
