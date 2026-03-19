package com.sionnavisualizer.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

/**
 * Utility class to generate and validate JSON Web Tokens (JWT).
 * A JWT securely transmits the user's identity between the frontend and backend.
 */
@Component
public class JwtUtil {

    // Reads the 256-bit secure key specified in application.yml
    @Value("${jwt.secret}")
    private String secret;

    // Reads the expiration time (24 hours = 86400000ms) specified in application.yml
    @Value("${jwt.expiration}")
    private long expiration;

    /**
     * Converts our secret application key string into a cryptographic Key object
     * used specifically by the HS256 algorithm to sign the tokens securely.
     */
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * Creates a new token containing the user's username (the "subject").
     * The token is marked with an issuance date and an expiration date.
     */
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                // The token expires precisely 24 hours after generation
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Pulls the username securely back out of an encrypted JWT.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Checks two things:
     * 1. Does the username extracted from the token match the requested username?
     * 2. Has the token's 24-hour expiration rule passed?
     */
    public boolean validateToken(String token, String username) {
        final String tokenUsername = extractUsername(token);
        return (tokenUsername.equals(username) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        Date expirationDate = extractClaim(token, Claims::getExpiration);
        return expirationDate.before(new Date());
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claimsResolver.apply(claims);
    }
}
