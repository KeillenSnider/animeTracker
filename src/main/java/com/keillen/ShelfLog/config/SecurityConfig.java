package com.keillen.ShelfLog.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.keillen.ShelfLog.security.JwtAuthFilter;


/**
 * Central Spring Security configuration for the application
 * Defines which endpoints are public vs protected
 */

//Makes the class a configuration class for Spring Security and that it has a bean that Spring will manage
@Configuration
public class SecurityConfig {

    //Variable for the Filter to be used
    private final JwtAuthFilter jwtAuthFilter;

    //Constructor to inject the JwtAuthFilter into this class so it can use it
    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    //Tells Spring to run this method once and hold the results to give out when needed
    @Bean
    //Method to configure the security filter chain for the application
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        //Configures the object to define security rules for the application
        http
            //Disables CSRF protection since the application is stateless and uses JWT for authentication
            .csrf(csrf -> csrf.disable())
            //Sets the session management policy to stateless, meaning no session will be created or used by Spring Security
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            //Defines the authorization rules for incoming HTTP requests
            .authorizeHttpRequests(auth -> auth
                //Allows unauthenticated access to the /register, /login, and /refresh endpoints for POST requests
                .requestMatchers(HttpMethod.POST, "/register", "/login", "/refresh", "/logout").permitAll()
                //Allows unauthenticated access to the /media/** and /media endpoints for GET requests
                .requestMatchers(HttpMethod.GET, "/media/**", "/media").permitAll()
                //Allows unauthenticated access to the /error endpoint, which is typically used for error handling
                .requestMatchers("/error").permitAll()
                //Requires authentication for any other requests not explicitly allowed above
                .anyRequest().authenticated())
            //Used to disable the default logout functionality provided by Spring Security
            .logout(logout -> logout.disable())
            //Configures the exception handling for authentication failures, returning a 401 Unauthorized status code when authentication fails
            .exceptionHandling(e -> e.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
            //Adds the custom JWT authentication filter before the default UsernamePasswordAuthenticationFilter in the filter chain
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        //Builds the configured SecurityFilterChain and returns it
        return http.build();
    }

    @Bean
    //Method to create a PasswordEncoder bean that uses BCrypt for hashing passwords
    public PasswordEncoder passwordEncoder() {
        //Returns a new instance of BCryptPasswordEncoder, which will be used for encoding passwords in the application
        return new BCryptPasswordEncoder();
    }
}