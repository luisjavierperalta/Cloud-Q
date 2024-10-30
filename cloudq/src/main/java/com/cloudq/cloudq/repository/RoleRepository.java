package com.cloudq.cloudq.repository;

import com.cloudq.cloudq.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    /**
     * Checks if a role with the given name exists.
     *
     * @param name the name of the role
     * @return true if the role exists, false otherwise
     */
    boolean existsByName(String name);

    /**
     * Finds a role by its name.
     *
     * @param name the name of the role
     * @return an Optional containing the found role, or empty if not found
     */
    Optional<Role> findByName(String name);

    /**
     * Finds a role by its ID with custom query.
     *
     * @param id the ID of the role
     * @return an Optional containing the found role, or empty if not found
     */
    @Query("SELECT r FROM Role r WHERE r.id = :id")
    Optional<Role> findRoleById(Long id);
}