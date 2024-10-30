package com.cloudq.cloudq.repository;

import com.cloudq.cloudq.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find a User by their username.
     *
     * @param username the username of the User to search for
     * @return an Optional containing the found User, or empty if not found
     */
    Optional<User> findByUsername(String username);

    /**
     * Check if a User exists by their username.
     *
     * @param username the username of the User to check
     * @return true if the User exists, false otherwise
     */
    boolean existsByUsername(String username);
}