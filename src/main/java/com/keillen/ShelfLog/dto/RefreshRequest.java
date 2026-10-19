package com.keillen.ShelfLog.dto;
import jakarta.validation.constraints.NotBlank;

/**
 * Data transfer object
 *  What a client may send to /refresh
*/

public record RefreshRequest(

    @NotBlank String refreshToken

){}
