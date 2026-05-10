package com.sionnavisualizer.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Task 3: Secondary CORS backup for Spring Web MVC.
 * This ensures that routes even outside of Spring Security's filter chain
 * correctly handle Access-Control-Allow-Origin headers.
 */
@Configuration
public class CorsWebConfig implements WebMvcConfigurer {

    // Read allowed origin from environment — provided by Railway Dashboard
    @Value("${cors.allowed.origin:http://localhost:4200}")
    private String allowedOrigin;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
            // Allow the specific Vercel frontend URL
            .allowedOrigins(allowedOrigin)
            // Allow all HTTP methods required for a REST API
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
            // Allow Authorization and Content-Type headers explicitly
            .allowedHeaders("*")
            // Expose Authorization header so Angular can read the JWT tokens
            .exposedHeaders("Authorization")
            // Allow credentials to be sent with cross-site requests
            .allowCredentials(true)
            // Cache preflight results for 1 hour to optimize performance
            .maxAge(3600);
    }
}
