package com.ghoomi.modules.trip.dto;

import com.ghoomi.modules.trip.ItineraryDay;

import java.time.LocalDate;

public class ItineraryDayResponse {

    private String id;
    private LocalDate dayDate;
    private Integer dayIndex;
    private String title;

    public ItineraryDayResponse() {}

    public ItineraryDayResponse(String id, LocalDate dayDate, Integer dayIndex, String title) {
        this.id = id;
        this.dayDate = dayDate;
        this.dayIndex = dayIndex;
        this.title = title;
    }

    public static ItineraryDayResponse from(ItineraryDay day) {
        if (day == null) return null;
        return new ItineraryDayResponse(
                day.getId(),
                day.getDayDate(),
                day.getDayIndex(),
                day.getTitle()
        );
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public LocalDate getDayDate() { return dayDate; }
    public void setDayDate(LocalDate dayDate) { this.dayDate = dayDate; }

    public Integer getDayIndex() { return dayIndex; }
    public void setDayIndex(Integer dayIndex) { this.dayIndex = dayIndex; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
}

