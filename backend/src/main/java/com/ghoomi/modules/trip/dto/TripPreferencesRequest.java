package com.ghoomi.modules.trip.dto;

import com.ghoomi.modules.trip.PlanMode;
import jakarta.validation.constraints.PositiveOrZero;

public class TripPreferencesRequest {

    private PlanMode planMode;

    @PositiveOrZero(message = "Target budget must be positive or zero")
    private Long targetBudgetMinor;

    private Boolean ownVehicle;

    private Boolean alreadyHaveStay;

    private String preferences;

    public TripPreferencesRequest() {}

    public PlanMode getPlanMode() { return planMode; }
    public void setPlanMode(PlanMode planMode) { this.planMode = planMode; }

    public Long getTargetBudgetMinor() { return targetBudgetMinor; }
    public void setTargetBudgetMinor(Long targetBudgetMinor) { this.targetBudgetMinor = targetBudgetMinor; }

    public Boolean getOwnVehicle() { return ownVehicle; }
    public void setOwnVehicle(Boolean ownVehicle) { this.ownVehicle = ownVehicle; }

    public Boolean getAlreadyHaveStay() { return alreadyHaveStay; }
    public void setAlreadyHaveStay(Boolean alreadyHaveStay) { this.alreadyHaveStay = alreadyHaveStay; }

    public String getPreferences() { return preferences; }
    public void setPreferences(String preferences) { this.preferences = preferences; }
}

