package com.sionnavisualizer.service;

import com.sionnavisualizer.model.User;
import com.sionnavisualizer.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

/**
 * Standard explicitly required interface by Spring Security.
 * This class simply bridges the gap between Spring's authentication manager
 * and our custom PostgreSQL "users" table.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Finds a user by their username in PostgreSQL.
     * Maps our custom User entity to Spring's UserDetails.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User completely unknown to system: " + username));
        
        // Return a standard Spring Security User wrapper wrapping our DB credentials
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(), 
                user.getPassword(), 
                new ArrayList<>() // Empty list of authorities/granted permissions for simple dashboard
        );
    }
}
