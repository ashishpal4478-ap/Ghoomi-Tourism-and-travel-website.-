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
public interface ItineraryItemRepository extends JpaRepository<ItineraryItem, String> {

    List<ItineraryItem> findAllByItineraryDayIdOrderByItemOrderAsc(String dayId);

    Optional<ItineraryItem> findTopByItineraryDayIdOrderByItemOrderDesc(String dayId);

    @Modifying
    @Transactional
    @Query("DELETE FROM ItineraryItem i WHERE i.itineraryDay.id = :dayId")
    void deleteAllByItineraryDayId(@Param("dayId") String dayId);
}

