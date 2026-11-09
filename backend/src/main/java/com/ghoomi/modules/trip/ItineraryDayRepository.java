package com.ghoomi.modules.trip;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItineraryDayRepository extends JpaRepository<ItineraryDay, String> {

    List<ItineraryDay> findAllByTripIdOrderByDayIndexAsc(String tripId);

    Optional<ItineraryDay> findByIdAndTripId(String id, String tripId);

    @Modifying
    @Transactional
    @Query("DELETE FROM ItineraryDay d WHERE d.trip.id = :tripId")
    void deleteAllByTripId(@Param("tripId") String tripId);
}


