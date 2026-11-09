package com.ghoomi.modules.trip.dto;

import com.ghoomi.modules.trip.ItineraryItem;

import java.time.LocalTime;

public class ItineraryItemResponse {

    private String id;
    private String itineraryDayId;
    private String placeId;
    private String placeName;
    private Integer itemOrder;
    private String category;
    private String title;
    private LocalTime plannedStart;
    private LocalTime plannedEnd;
    private String notes;
    private Long estimateMinor;
    private String currency;
    private String verificationStatus;

    public ItineraryItemResponse() {}

    public static ItineraryItemResponse from(ItineraryItem item) {
        if (item == null) return null;
        ItineraryItemResponse res = new ItineraryItemResponse();
        res.id = item.getId();
        if (item.getItineraryDay() != null) {
            res.itineraryDayId = item.getItineraryDay().getId();
        }
        if (item.getPlace() != null) {
            res.placeId = item.getPlace().getId();
            res.placeName = item.getPlace().getName();
        }
        res.itemOrder = item.getItemOrder();
        res.category = item.getCategory();
        res.title = item.getTitle();
        res.plannedStart = item.getPlannedStart();
        res.plannedEnd = item.getPlannedEnd();
        res.notes = item.getNotes();
        res.estimateMinor = item.getEstimateMinor();
        res.currency = item.getCurrency();
        res.verificationStatus = item.getVerificationStatus();
        return res;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getItineraryDayId() { return itineraryDayId; }
    public void setItineraryDayId(String itineraryDayId) { this.itineraryDayId = itineraryDayId; }

    public String getPlaceId() { return placeId; }
    public void setPlaceId(String placeId) { this.placeId = placeId; }

    public String getPlaceName() { return placeName; }
    public void setPlaceName(String placeName) { this.placeName = placeName; }

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
}

