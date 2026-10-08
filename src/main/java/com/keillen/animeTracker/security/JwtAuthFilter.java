package com.keillen.animeTracker.security;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Runs once per request
 * If the request carries a valid Bearer token, it records which user is making the request
 * A bad token is ignored, so the access rules decide
 * No token gets sent through
 */

@Component
//Makes sure it runs only once when called
public class JwtAuthFilter extends OncePerRequestFilter{
    
    private final JwtService jwtService;

    //Inject JwtService.java
    public JwtAuthFilter(JwtService jwtService){
        this.jwtService = jwtService;
    }

    @Override 
    //Protected = visible to this class and subclasses
    //Defines what happens when it runs an HTTP request
    //Where we will handle exceptions
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException{

        //Read the Authorization header
        String header = request.getHeader("Authorization");

        //Check if it is missing the header or it does not start with Bearer
        if(header == null || !header.startsWith("Bearer ")){
            //Push it down the pipeline
            filterChain.doFilter(request, response);
            //exit
            return;
        }

        //Extract the token by removing Bearer
        String token = header.substring(7);

        try{
            //Attempt to validate the Token using JwtService
            Integer userId = jwtService.getUserIdFromToken(token);

            //Creates an Authentication object representing the logged in user.
            // UserId = the identity | null = credentials but not needed because we verified the token 
            // list.of is the list of authorities
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userId, null, List.of());

            //Store auth in SecurityContext. Makes the request as authenticated for the rest of the pipeline
            //Now sees the user as logged in
            SecurityContextHolder.getContext().setAuthentication(auth);
        } catch (JwtException e){
            //Bad token: record no user and let the access rules decide
            //Clears the security context, effectively logging out the user for this request
            SecurityContextHolder.clearContext();
        }

        //Continue the chain after auth
        filterChain.doFilter(request, response);
    }
}
