package com.ghoomi.modules.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for the {@link EmergencyContact} entity.
 */
@Repository
public interface EmergencyContactRepository extends JpaRepository<EmergencyContact, String> {

    /**
     * List all emergency contacts owned by a specific user account.
     */
    List<EmergencyContact> findAllByOwnerIdOrderByCreatedAtAsc(String ownerId);
}

