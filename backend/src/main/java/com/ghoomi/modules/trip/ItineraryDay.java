package com.ghoomi.modules.trip;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.UUID;

/**
 * JPA entity mapping the {@code itinerary_days} table.
 *
 * Column mapping matches {@code V1__ghoomi_partition1_initial_schema.sql}:
 * <pre>
 *   id         CHAR(36)      PK
 *   trip_id    CHAR(36)      FK -> trips.id ON DELETE CASCADE
 *   day_date   DATE          NOT NULL
 *   day_index  INTEGER       NOT NULL CHECK (day_index >= 1)
 *   title      VARCHAR(255)  nullable
 * </pre>
 */
@Entity
@Table(name = "itinerary_days", uniqueConstraints = {
        @UniqueConstraint(name = "uk_trip_day_date", columnNames = {"trip_id", "day_date"}),
        @UniqueConstraint(name = "uk_trip_day_index", columnNames = {"trip_id", "day_index"})
})
public class ItineraryDay {

    @Id
    @Column(name = "id", columnDefinition = "CHAR(36)", nullable = false, updatable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "trip_id", columnDefinition = "CHAR(36)", nullable = false)
    private Trip trip;

    @Column(name = "day_date", nullable = false)
    private LocalDate dayDate;

    @Column(name = "day_index", nullable = false)
    private Integer dayIndex;

    @Column(name = "title", length = 255)
    private String title;

    @PrePersist
    protected void onCreate() {
        if (id == null) {
            id = UUID.randomUUID().toString();
        }
    }

    // ── Constructors ──────────────────────────────────────────────────────────

    protected ItineraryDay() {}

    public ItineraryDay(Trip trip, LocalDate dayDate, Integer dayIndex, String title) {
        this.trip = trip;
        this.dayDate = dayDate;
        this.dayIndex = dayIndex;
        this.title = title;
    }

    // ── Getters and Setters ───────────────────────────────────────────────────

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Trip getTrip() { return trip; }
    public void setTrip(Trip trip) { this.trip = trip; }

    public LocalDate getDayDate() { return dayDate; }
    public void setDayDate(LocalDate dayDate) { this.dayDate = dayDate; }

    public Integer getDayIndex() { return dayIndex; }
    public void setDayIndex(Integer dayIndex) { this.dayIndex = dayIndex; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
}

