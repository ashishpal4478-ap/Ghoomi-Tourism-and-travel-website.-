package com.ghoomi.modules.trip.dto;

import com.ghoomi.modules.trip.PlanMode;
import com.ghoomi.modules.trip.TripStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class UpdateTripRequest {

    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;

    @Size(max = 200, message = "Origin label must not exceed 200 characters")
    private String originLabel;

    @Size(max = 200, message = "Destination label must not exceed 200 characters")
    private String destinationLabel;

    @Size(max = 36, message = "Destination ID must be a valid UUID")
    private String destinationId;

    private LocalDate startDate;

    private LocalDate endDate;

    @Size(max = 64, message = "Timezone must not exceed 64 characters")
    private String timezone;

    @Min(value = 1, message = "Traveler count must be at least 1")
    @Max(value = 50, message = "Traveler count cannot exceed 50")
    private Integer travelerCount;

    private PlanMode planMode;

    @PositiveOrZero(message = "Target budget must be positive or zero")
    private Long targetBudgetMinor;

    @Size(min = 3, max = 3, message = "Currency must be a 3-letter ISO code")
    private String currency;

    private Boolean ownVehicle;

    private Boolean alreadyHaveStay;

    private String preferences;

    private TripStatus status;

    private Integer version;

    public UpdateTripRequest() {}

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

    public Boolean getOwnVehicle() { return ownVehicle; }
    public void setOwnVehicle(Boolean ownVehicle) { this.ownVehicle = ownVehicle; }

    public Boolean getAlreadyHaveStay() { return alreadyHaveStay; }
    public void setAlreadyHaveStay(Boolean alreadyHaveStay) { this.alreadyHaveStay = alreadyHaveStay; }

    public String getPreferences() { return preferences; }
    public void setPreferences(String preferences) { this.preferences = preferences; }

    public TripStatus getStatus() { return status; }
    public void setStatus(TripStatus status) { this.status = status; }

    public Integer getVersion() { return version; }
    public void setVersion(Integer version) { this.version = version; }
}

