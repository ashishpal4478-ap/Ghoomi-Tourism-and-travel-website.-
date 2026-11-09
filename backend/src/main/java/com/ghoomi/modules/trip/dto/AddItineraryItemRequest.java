package com.ghoomi.modules.trip.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalTime;

public class AddItineraryItemRequest {

    private String placeId;

    @NotBlank(message = "Item title is required")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;

    @Size(max = 60, message = "Category must not exceed 60 characters")
    private String category = "ACTIVITY";

    private LocalTime plannedStart;

    private LocalTime plannedEnd;

    private String notes;

    private Long estimateMinor;

    @Size(min = 3, max = 3, message = "Currency must be 3 characters")
    private String currency = "INR";

    public AddItineraryItemRequest() {}

    public AddItineraryItemRequest(String title, String category) {
        this.title = title;
        this.category = category;
    }

    public String getPlaceId() { return placeId; }
    public void setPlaceId(String placeId) { this.placeId = placeId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

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
}

