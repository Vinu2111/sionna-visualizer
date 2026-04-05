package com.sionnavisualizer.filter;

import com.sionnavisualizer.service.CustomUserDetailsService;
import com.sionnavisualizer.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Intercepts every single HTTP request coming into the Java backend API.
 * Validates the JWT Bearer token and sets SecurityContext if valid.
 */
@Component
public class JwtFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtFilter.class);

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    public JwtFilter(JwtUtil jwtUtil, CustomUserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Executes immediately whenever a route is requested.
     * Grabs the Authorization Header, extracts the Bearer token safely, and sets the Identity Context.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // FIX 4.1: Skip JWT validation for OPTIONS preflight requests
        // Browser sends OPTIONS before the actual POST/PUT. 
        // These requests never carry an Authorization header, so we must let them through.
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        // Step 1: Read the header out of the request exactly named "Authorization"
        final String authorizationHeader = request.getHeader("Authorization");

        String username = null;
        String jwtToken = null;

        // Step 2: Validate it begins with "Bearer " and slice only the cryptographic blob.
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwtToken = authorizationHeader.substring(7);
            try {
                username = jwtUtil.extractUsername(jwtToken);
            } catch (Exception e) {
                // If tampering occurred, or the token expired, this exception prevents setting user context
                // SECURITY: Log sanitized warning only — never log token content
                log.warn("Invalid or expired JWT token on {}: {}", request.getRequestURI(), e.getClass().getSimpleName());
            }
        }

        // Step 3: If username exists but they are not securely registered in the Context currently:
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            // Step 4: Validate token timestamp vs username mathematically using HMAC Secret Key
            if (jwtUtil.validateToken(jwtToken, userDetails.getUsername())) {
                
                // Set the fully approved login context allowing controllers to run strictly
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // Lastly, continue the request to whichever intended controller the frontend wanted
        filterChain.doFilter(request, response);
    }

    // FIX 4.2: Built-in Spring exclusion for OPTIONS preflight
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return "OPTIONS".equalsIgnoreCase(request.getMethod());
    }
}
