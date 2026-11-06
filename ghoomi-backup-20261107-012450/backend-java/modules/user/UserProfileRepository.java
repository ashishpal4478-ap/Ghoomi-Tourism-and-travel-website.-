package com.ghoomi.modules.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for the {@link UserProfile} entity.
 */
@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, String> {

    /**
     * Find the profile linked to a specific user.
     */
    Optional<UserProfile> findByUserId(String userId);
}

