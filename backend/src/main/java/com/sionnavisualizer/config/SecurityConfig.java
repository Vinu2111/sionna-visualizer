package com.sionnavisualizer.config;

import com.sionnavisualizer.filter.JwtFilter;
import com.sionnavisualizer.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.beans.factory.annotation.Value;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final JwtFilter jwtFilter;

    // FIX 1.1: Map CORS origin from environment or fallback to local Angular dev
    @Value("${cors.allowed.origin:http://localhost:4200}")
    private String allowedOrigin;

    public SecurityConfig(CustomUserDetailsService userDetailsService, JwtFilter jwtFilter) {
        this.userDetailsService = userDetailsService;
        this.jwtFilter = jwtFilter;
    }

    /**
     * Builds the main Security Wall configurations.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            // FIX 1.2: CORS must be first in the chain
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            // State is entirely carried securely in the JSON Token; CSRF is for stateful architecture
            .csrf(AbstractHttpConfigurer::disable)
            // Permit /auth universally so users can register, and our demo natively locally without a token
            .authorizeHttpRequests(auth -> auth
                // OPTIONS preflight MUST be explicitly permitted before any other check
                .requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers(
                    "/api/auth/**",
                    "/api/simulate/demo",
                    "/api/simulations/colormaps",
                    "/api/simulations/estimate",
                    "/api/share/**",
                    "/api/health",
                    "/api/health/ping",
                    "/v1/api/**"
                ).permitAll()
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/gallery/**").permitAll()
                // All other endpoints natively require strict Authorization Bearer approval
                .anyRequest().authenticated()
            )
            // Sessions are not tracked in RAM. Every HTTP request requires re-transmission of the JWT Header
            .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // Inserts our custom filter precisely before the regular generic password check
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }

    /**
     * Allows cross-origin requests from Vercel (frontend) and localhost (local dev).
     * Without this, the browser blocks every API call from the Angular frontend.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        
        // FIX 1.3: Allow the production Vercel frontend origin securely
        config.setAllowedOrigins(List.of(allowedOrigin));
        
        // Allow all HTTP methods including OPTIONS for preflight
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        
        // Headers required for JWT authentication
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept", "Origin", "X-Requested-With"));
        
        // Expose Authorization header so frontend can read the tokens
        config.setExposedHeaders(List.of("Authorization"));
        
        config.setAllowCredentials(true);
        config.setMaxAge(3600L); // Cache preflight for 1 hour to reduce OPTIONS calls
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    /**
     * Creates the hashing object required to encrypt/decrypt BCrypt.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Exposes the overarching Manager required by AuthController.java to process the plaintext login logic.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Maps our SQL PostgreSQL database query layer properly into Spring's native API.
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        // This stops plaintext passwords from being saved
        authProvider.setPasswordEncoder(passwordEncoder()); 
        return authProvider;
    }
}
