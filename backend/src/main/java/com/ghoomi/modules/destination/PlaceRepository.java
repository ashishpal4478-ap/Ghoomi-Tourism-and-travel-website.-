package com.ghoomi.modules.destination;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlaceRepository extends JpaRepository<Place, String> {

    List<Place> findAllByDestinationIdAndActiveTrue(String destinationId);

    List<Place> findAllByDestinationIdAndCategoryAndActiveTrue(String destinationId, PlaceCategory category);

    List<Place> findAllByCategoryAndActiveTrue(PlaceCategory category);

    Optional<Place> findByIdAndActiveTrue(String id);

    @Query("SELECT p FROM Place p WHERE p.active = true AND " +
           "(LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<Place> searchPlaces(@Param("query") String query);

    @Query("SELECT p FROM Place p WHERE p.active = true AND p.category = :category AND " +
           "(LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<Place> searchPlacesByCategory(@Param("category") PlaceCategory category, @Param("query") String query);
}

