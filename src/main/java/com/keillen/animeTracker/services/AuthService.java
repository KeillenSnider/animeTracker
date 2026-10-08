package com.keillen.animeTracker.services;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.keillen.animeTracker.dto.AuthResponse;
import com.keillen.animeTracker.dto.LoginRequest;
import com.keillen.animeTracker.dto.RefreshRequest;
import com.keillen.animeTracker.dto.RegisterRequest;
import com.keillen.animeTracker.entities.RefreshToken;
import com.keillen.animeTracker.entities.User;
import com.keillen.animeTracker.repositories.RefreshTokenRepository;
import com.keillen.animeTracker.repositories.UserRepository;
import com.keillen.animeTracker.security.JwtService;

/**
 * Service for authentication logic
 * Used to separate the business logic from the controller
 */


//Service is for business logic, and is used to separate the controller from the logic
@Service 
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    //Create a dummy
    private final String dummyHash;

    //How many days till a refresh token expires, read from application.properties
    private final int refreshExpirationDays;

    //Inject dependencies into the service
    public AuthService(UserRepository userRepository, RefreshTokenRepository refreshTokenRepository, 
        PasswordEncoder passwordEncoder, JwtService jwtService, @Value("${jwt.refresh.expiration-days}") int refreshExpirationDays) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.refreshExpirationDays = refreshExpirationDays;
        this.dummyHash = passwordEncoder.encode("dummypassword");
    }

    //Create a method to generate tokens
    private AuthResponse generateTokens(User user){

        //Make the access token
        String accessToken = jwtService.generateAccessToken(user.getId());

        //Generate a long string for the refresh token
        String refreshTokenString = UUID.randomUUID().toString();
        //Create a new refresh token
        RefreshToken refreshToken = new RefreshToken();
        //The user it belongs to
        refreshToken.setUser(user);
        //The String of the token
        refreshToken.setToken(refreshTokenString);
        //Timestamp of when it was created
        refreshToken.setCreated(LocalDateTime.now());
        //When it expires
        refreshToken.setExpiresAt(LocalDateTime.now().plusDays(refreshExpirationDays));
        //Set as not revoked
        refreshToken.setRevoked(false);
        //Save the token to the database
        refreshTokenRepository.save(refreshToken);

        //return the tokens to the client
        return new AuthResponse(accessToken, refreshTokenString);
    }

    //Used because we want to make sure the database operations are atomic, and if one fails, the other will be rolled back
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        
        //Check for duplicates
        if(userRepository.existsByUsername(request.username())){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");
        }
        if(userRepository.existsByEmail(request.email())){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        }

        //If not a duplicate hash the password
        String hashedPassword = passwordEncoder.encode(request.password());

        //Create a new user entity and save it to the database
        User user = new User();
        user.setUsername(request.username());
        user.setPassword(hashedPassword);
        user.setEmail(request.email());
        user.setCreated(LocalDate.now());
        //Catch if a user is already in the database with the same username or email
        try{
            userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username or email already exists");
        }
        

        return generateTokens(user);
    }

    //Login method, similar to register but checks the password instead of creating a new user
    @Transactional
    public AuthResponse login(LoginRequest request){

        //Make a new user and find it in the database.
        User user = userRepository.findByUsername(request.username()).orElse(null);
        //If the user is null, use a dummy hash to prevent timing attacks. This way, the time taken to check the password is consistent regardless of whether the user exists or not.
        //Else get the password hash from the user object
        String hash = (user == null) ? dummyHash : user.getPassword();
        //Check if the password matches the hash
        boolean ok = passwordEncoder.matches(request.password(), hash);
        //If the user is null or the password does not match, return a 401 Unauthorized error
        if (user == null || !ok) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
        }
        //If the user exists and the password matches, generate tokens and return them to the client
        return generateTokens(user);
    }

    @Transactional 
    public AuthResponse refresh(RefreshRequest request){

        //Find the refresh token in the database, if it does not exist return an error
        RefreshToken refreshToken = refreshTokenRepository.findByToken(request.refreshToken())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token"));

        //Check if the token is revoked or expired, if so return an error
        if(refreshToken.isRevoked() || refreshToken.getExpiresAt().isBefore(LocalDateTime.now())){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token");
        }

        //Get the user from the token
        User user = refreshToken.getUser();

        //Revoke the old token
        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);

        //Generate new tokens and return them to the client
        return generateTokens(user);
    }


    @Transactional
    public void logout(RefreshRequest request){

        //Find the refresh token in the database. If not found return nothing to not give any information to a potential attacker
        RefreshToken refreshToken = refreshTokenRepository.findByToken(request.refreshToken()).orElse(null);

        //If the token is found, revoke it
        if(refreshToken != null){
            refreshToken.setRevoked(true);
            refreshTokenRepository.save(refreshToken);
        }
    }
}
