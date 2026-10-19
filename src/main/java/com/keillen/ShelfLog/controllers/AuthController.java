package com.keillen.ShelfLog.controllers;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;
import com.keillen.ShelfLog.dto.AuthResponse;
import com.keillen.ShelfLog.dto.LoginRequest;
import com.keillen.ShelfLog.dto.RefreshRequest;
import com.keillen.ShelfLog.dto.RegisterRequest;
import com.keillen.ShelfLog.services.AuthService;

import jakarta.validation.Valid;

/**
 * Controller for authentication endpoints
 * Used to handle HTTP requests related to authentication
 */

//Methods are answering HTTP request and retun values become JSON
@RestController 
public class AuthController{

    private final AuthService authService;

    public AuthController(AuthService authService){
        this.authService = authService;
    }

    //Binds the method to /register endpoint, and only accepts POST requests
    @PostMapping("/register")
    //@Valid makes sure the contraints in register request are followed
    //@RequestBody turns the JSON into the record RegisterRequest
    public AuthResponse register(@Valid @RequestBody RegisterRequest request){
        return authService.register(request);
    }

    //Binds the method to /login endpoint, and only accepts POST requests
    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request){
        return authService.login(request);
    }

    @PostMapping("/refresh")
    public AuthResponse refresh(@Valid @RequestBody RefreshRequest request){
        return authService.refresh(request);
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(@Valid @RequestBody RefreshRequest request){
        authService.logout(request);
    }
}