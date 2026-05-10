package com.sionnavisualizer.repository;

import com.sionnavisualizer.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Interface to perform database operations on the User entity.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Finds a user by their exact username. 
    // Returns Optional in case the user does not exist in the database.
    Optional<User> findByUsername(String username);
    
    // Checks if a username is already taken during registration
    boolean existsByUsername(String username);
}
