package com.sionnavisualizer.model;

import jakarta.persistence.*;

/**
 * JPA entity mapping to the "users" table.
 * Stores credentials for logging into the Sionna Visualizer dashboard.
 */
@Entity
@Table(name = "users")
public class User {

    // Auto-generated primary key
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Unique username for login
    @Column(unique = true, nullable = false)
    private String username;

    // Email address
    @Column(unique = true)
    private String email;

    // Hashed password using BCrypt — never store plaintext!
    @Column(nullable = false)
    private String password;

    // User's authorization role (e.g., "ROLE_USER" or "ROLE_ADMIN")
    private String role;

    // --- Getters and Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
