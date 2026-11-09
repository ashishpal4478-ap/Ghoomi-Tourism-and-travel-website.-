package com.ghoomi.modules.trip.dto;

import com.ghoomi.modules.trip.ItineraryDay;
import com.ghoomi.modules.trip.PlanMode;
import com.ghoomi.modules.trip.Trip;
import com.ghoomi.modules.trip.TripStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TripResponse {

    private String id;
    private String ownerUserId;
    private String title;
    private String originLabel;
    private String destinationLabel;
    private String destinationId;
    private LocalDate startDate;
    private LocalDate endDate;
    private String timezone;
    private Integer travelerCount;
    private PlanMode planMode;
    private Long targetBudgetMinor;
    private String currency;
    private boolean ownVehicle;
    private boolean alreadyHaveStay;
    private String preferences;
    private TripStatus status;
    private Integer version;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<ItineraryDayResponse> days = new ArrayList<>();

    public TripResponse() {}

    public static TripResponse from(Trip trip, List<ItineraryDay> days) {
        if (trip == null) return null;
        TripResponse response = new TripResponse();
        response.id = trip.getId();
        response.ownerUserId = trip.getOwner() != null ? trip.getOwner().getId() : null;
        response.title = trip.getTitle();
        response.originLabel = trip.getOriginLabel();
        response.destinationLabel = trip.getDestinationLabel();
        response.destinationId = trip.getDestinationId();
        response.startDate = trip.getStartDate();
        response.endDate = trip.getEndDate();
        response.timezone = trip.getTimezone();
        response.travelerCount = trip.getTravelerCount();
        response.planMode = trip.getPlanMode();
        response.targetBudgetMinor = trip.getTargetBudgetMinor();
        response.currency = trip.getCurrency();
        response.ownVehicle = trip.isOwnVehicle();
        response.alreadyHaveStay = trip.isAlreadyHaveStay();
        response.preferences = trip.getPreferences();
        response.status = trip.getStatus();
        response.version = trip.getVersion();
        response.createdAt = trip.getCreatedAt();
        response.updatedAt = trip.getUpdatedAt();

        if (days != null && !days.isEmpty()) {
            response.days = days.stream()
                    .map(ItineraryDayResponse::from)
                    .toList();
        } else {
            response.days = Collections.emptyList();
        }
        return response;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getOwnerUserId() { return ownerUserId; }
    public void setOwnerUserId(String ownerUserId) { this.ownerUserId = ownerUserId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getOriginLabel() { return originLabel; }
    public void setOriginLabel(String originLabel) { this.originLabel = originLabel; }

    public String getDestinationLabel() { return destinationLabel; }
    public void setDestinationLabel(String destinationLabel) { this.destinationLabel = destinationLabel; }

    public String getDestinationId() { return destinationId; }
    public void setDestinationId(String destinationId) { this.destinationId = destinationId; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public String getTimezone() { return timezone; }
    public void setTimezone(String timezone) { this.timezone = timezone; }

    public Integer getTravelerCount() { return travelerCount; }
    public void setTravelerCount(Integer travelerCount) { this.travelerCount = travelerCount; }

    public PlanMode getPlanMode() { return planMode; }
    public void setPlanMode(PlanMode planMode) { this.planMode = planMode; }

    public Long getTargetBudgetMinor() { return targetBudgetMinor; }
    public void setTargetBudgetMinor(Long targetBudgetMinor) { this.targetBudgetMinor = targetBudgetMinor; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public boolean isOwnVehicle() { return ownVehicle; }
    public void setOwnVehicle(boolean ownVehicle) { this.ownVehicle = ownVehicle; }

    public boolean isAlreadyHaveStay() { return alreadyHaveStay; }
    public void setAlreadyHaveStay(boolean alreadyHaveStay) { this.alreadyHaveStay = alreadyHaveStay; }

    public String getPreferences() { return preferences; }
    public void setPreferences(String preferences) { this.preferences = preferences; }

    public TripStatus getStatus() { return status; }
    public void setStatus(TripStatus status) { this.status = status; }

    public Integer getVersion() { return version; }
    public void setVersion(Integer version) { this.version = version; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public List<ItineraryDayResponse> getDays() { return days; }
    public void setDays(List<ItineraryDayResponse> days) { this.days = days; }
}

