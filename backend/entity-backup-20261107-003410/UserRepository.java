package com.ghoomi.modules.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for the {@link User} entity.
 */
@Repository
public interface UserRepository extends JpaRepository<User, String> {

    /**
     * Find a user by their email address.
     * Used by {@link com.ghoomi.modules.auth.GhoomiUserDetailsService}
     * and the registration duplicate-check.
     */
    Optional<User> findByEmail(String email);

    /**
     * Find a user by their E.164-normalised phone number.
     * Used during phone-based login if supported.
     */
    Optional<User> findByPhoneE164(String phoneE164);

    /**
     * Check whether an email is already registered.
     * Avoids loading the full entity for a simple existence check.
     */
    boolean existsByEmail(String email);

    /**
     * Check whether a phone number is already registered.
     */
    boolean existsByPhoneE164(String phoneE164);
}

