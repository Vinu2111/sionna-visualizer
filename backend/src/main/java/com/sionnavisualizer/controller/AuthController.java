package com.sionnavisualizer.controller;

import com.sionnavisualizer.dto.AuthRequest;
import com.sionnavisualizer.dto.AuthResponse;
import com.sionnavisualizer.model.User;
import com.sionnavisualizer.repository.UserRepository;
import com.sionnavisualizer.service.CustomUserDetailsService;
import com.sionnavisualizer.util.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(originPatterns = {"https://*.vercel.app", "http://localhost:4200"})
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManager authenticationManager, 
                          CustomUserDetailsService userDetailsService, 
                          JwtUtil jwtUtil, 
                          UserRepository userRepository,
                          PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Registers a new user inside the PostgreSQL instance.
     * Takes plaintext string sent across wire, immediately hashes using BCrypt layer.
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody AuthRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            return ResponseEntity.badRequest().body(new AuthResponse(null, null, "Username is already taken!"));
        }

        User newUser = new User();
        newUser.setUsername(request.getUsername());
        
        // NEVER STORE PLAIN TEXT — Use the bcrypt hash securely initialized by Spring Security
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setRole("ROLE_USER");
        
        userRepository.save(newUser);
        
        return ResponseEntity.ok(new AuthResponse(null, request.getUsername(), "User registered successfully"));
    }

    /**
     * Authenticates an existing user and issues back a 24-hr JWT Bearer.
     */
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody AuthRequest request) {
        try {
            // Checks if the unhashed text matches the previously seeded bcrypt SQL column automatically
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).body(new AuthResponse(null, null, "Incorrect username or password"));
        }

        // It is secure! Let's build the identity JSON Web Token.
        final UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        final String jwt = jwtUtil.generateToken(userDetails.getUsername());

        return ResponseEntity.ok(new AuthResponse(jwt, userDetails.getUsername(), "Login successful"));
    }
}
