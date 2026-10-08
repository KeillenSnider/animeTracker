package com.keillen.animeTracker.security;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

/**
 * Creates and verifies JWT access tokens
 * The signing key is built once at startup from the jwt.secret property
 */

//Spring creates one shared instance of this class at startup
@Component 
public class JwtService {

    private final int accessExpirationMinutes;
    
    //Make a key once and store it in key
    private final SecretKey key;

    //Constructor
    //@Value reads jwt.secret from application.properties and injects it as the String "secret"
    public JwtService(@Value("${jwt.secret}") String secret, @Value("${jwt.access.expiration-minutes}") int expirationMinutes){
        
        //Decoders takes the String and converts it into bytes
        //Keys.hmac takes the bytes and creates a secret key
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));

        this.accessExpirationMinutes = expirationMinutes;
    }


    //Used to generate a access token for a user
    public String generateAccessToken(Integer userId){

        //Create a Date object
        Date now = new Date();

        //Calculate when it expires and convert the time to milliseconds
        Date expires = new Date(now.getTime() + accessExpirationMinutes * 60L * 1000L);

        //Subject is for who this token is about
        return Jwts.builder()
        .subject(String.valueOf(userId))
        .issuedAt(now)
        .expiration(expires)
        .signWith(key)
        .compact();

    }

    //Check if the token in valid and extract the user ID
    public Integer getUserIdFromToken(String token) {

        //Used to read and validate a JWT
        String subject = Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .getPayload()
            .getSubject();
        //Return userId
        return Integer.valueOf(subject);
    }
}
