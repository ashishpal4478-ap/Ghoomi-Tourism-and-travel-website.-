package com.ghoomi.modules.trip;

import com.ghoomi.modules.destination.Place;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.UUID;

/**
 * JPA entity mapping the {@code itinerary_items} table.
 */
@Entity
@Table(name = "itinerary_items", uniqueConstraints = {
        @UniqueConstraint(name = "uk_day_item_order", columnNames = {"itinerary_day_id", "item_order"})
})
public class ItineraryItem {

    @Id
    @Column(name = "id", columnDefinition = "CHAR(36)", nullable = false, updatable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "itinerary_day_id", columnDefinition = "CHAR(36)", nullable = false)
    private ItineraryDay itineraryDay;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id", columnDefinition = "CHAR(36)")
    private Place place;

    @Column(name = "item_order", nullable = false)
    private Integer itemOrder = 0;

    @Column(name = "category", length = 60, nullable = false)
    private String category;

    @Column(name = "title", length = 255, nullable = false)
    private String title;

    @Column(name = "planned_start")
    private LocalTime plannedStart;

    @Column(name = "planned_end")
    private LocalTime plannedEnd;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "estimate_minor")
    private Long estimateMinor;

    @Column(name = "currency", columnDefinition = "CHAR(3)", length = 3)
    private String currency;

    @Column(name = "verification_status", length = 32, nullable = false)
    private String verificationStatus = "USER_PLANNED";

    @Column(name = "source_ref", columnDefinition = "TEXT")
    private String sourceRef;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (id == null) {
            id = UUID.randomUUID().toString();
        }
        if (createdAt == null) {
            createdAt = LocalDateTime.now(ZoneOffset.UTC);
        }
        if (verificationStatus == null) {
            verificationStatus = "USER_PLANNED";
        }
    }

    // ── Constructors ──────────────────────────────────────────────────────────

    protected ItineraryItem() {}

    public ItineraryItem(ItineraryDay itineraryDay, Place place, Integer itemOrder,
                         String category, String title) {
        this.itineraryDay = itineraryDay;
        this.place = place;
        this.itemOrder = itemOrder != null ? itemOrder : 0;
        this.category = category;
        this.title = title;
        this.verificationStatus = "USER_PLANNED";
    }

    // ── Getters and Setters ───────────────────────────────────────────────────

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public ItineraryDay getItineraryDay() { return itineraryDay; }
    public void setItineraryDay(ItineraryDay itineraryDay) { this.itineraryDay = itineraryDay; }

    public Place getPlace() { return place; }
    public void setPlace(Place place) { this.place = place; }

    public Integer getItemOrder() { return itemOrder; }
    public void setItemOrder(Integer itemOrder) { this.itemOrder = itemOrder; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public LocalTime getPlannedStart() { return plannedStart; }
    public void setPlannedStart(LocalTime plannedStart) { this.plannedStart = plannedStart; }

    public LocalTime getPlannedEnd() { return plannedEnd; }
    public void setPlannedEnd(LocalTime plannedEnd) { this.plannedEnd = plannedEnd; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Long getEstimateMinor() { return estimateMinor; }
    public void setEstimateMinor(Long estimateMinor) { this.estimateMinor = estimateMinor; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getVerificationStatus() { return verificationStatus; }
    public void setVerificationStatus(String verificationStatus) { this.verificationStatus = verificationStatus; }

    public String getSourceRef() { return sourceRef; }
    public void setSourceRef(String sourceRef) { this.sourceRef = sourceRef; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

