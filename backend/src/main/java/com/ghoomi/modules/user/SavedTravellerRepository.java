package com.ghoomi.modules.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for the {@link SavedTraveller} entity.
 */
@Repository
public interface SavedTravellerRepository extends JpaRepository<SavedTraveller, String> {

    /**
     * List all saved travellers owned by a specific user account.
     * Ordering is by creation time for a stable UI list.
     */
    List<SavedTraveller> findAllByOwnerIdOrderByCreatedAtAsc(String ownerId);
}

