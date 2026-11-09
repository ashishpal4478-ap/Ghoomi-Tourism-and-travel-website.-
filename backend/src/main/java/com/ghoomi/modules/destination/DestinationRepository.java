package com.ghoomi.modules.destination;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DestinationRepository extends JpaRepository<Destination, String> {

    List<Destination> findAllByActiveTrueOrderByNameAsc();

    /** Used in test cleanup to find any destination by slug regardless of active flag. */
    Optional<Destination> findBySlug(String slug);

    Optional<Destination> findBySlugAndActiveTrue(String slug);

    Optional<Destination> findByIdAndActiveTrue(String id);

    @Query("SELECT d FROM Destination d WHERE d.active = true AND " +
           "(LOWER(d.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(d.stateRegion) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(d.slug) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<Destination> searchDestinations(@Param("query") String query);
}

