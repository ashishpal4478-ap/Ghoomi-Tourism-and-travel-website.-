package com.ghoomi.modules.destination.dto;

import com.ghoomi.modules.destination.Place;
import com.ghoomi.modules.destination.PlaceCategory;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PlaceResponse {

    private String id;
    private String destinationId;
    private String destinationName;
    private String name;
    private PlaceCategory category;
    private String addressLabel;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String description;
    private String sourceName;
    private String sourceUrl;
    private LocalDateTime infoVerifiedAt;
    private String openingHours;
    private Long estimatedCostMinor;
    private String currency;
    private boolean active;

    public PlaceResponse() {}

    public static PlaceResponse from(Place place) {
        if (place == null) return null;
        PlaceResponse res = new PlaceResponse();
        res.id = place.getId();
        if (place.getDestination() != null) {
            res.destinationId = place.getDestination().getId();
            res.destinationName = place.getDestination().getName();
        }
        res.name = place.getName();
        res.category = place.getCategory();
        res.addressLabel = place.getAddressLabel();
        res.latitude = place.getLatitude();
        res.longitude = place.getLongitude();
        res.description = place.getDescription();
        res.sourceName = place.getSourceName();
        res.sourceUrl = place.getSourceUrl();
        res.infoVerifiedAt = place.getInfoVerifiedAt();
        res.openingHours = place.getOpeningHours();
        res.estimatedCostMinor = place.getEstimatedCostMinor();
        res.currency = place.getCurrency();
        res.active = place.isActive();
        return res;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getDestinationId() { return destinationId; }
    public void setDestinationId(String destinationId) { this.destinationId = destinationId; }

    public String getDestinationName() { return destinationName; }
    public void setDestinationName(String destinationName) { this.destinationName = destinationName; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public PlaceCategory getCategory() { return category; }
    public void setCategory(PlaceCategory category) { this.category = category; }

    public String getAddressLabel() { return addressLabel; }
    public void setAddressLabel(String addressLabel) { this.addressLabel = addressLabel; }

    public BigDecimal getLatitude() { return latitude; }
    public void setLatitude(BigDecimal latitude) { this.latitude = latitude; }

    public BigDecimal getLongitude() { return longitude; }
    public void setLongitude(BigDecimal longitude) { this.longitude = longitude; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getSourceName() { return sourceName; }
    public void setSourceName(String sourceName) { this.sourceName = sourceName; }

    public String getSourceUrl() { return sourceUrl; }
    public void setSourceUrl(String sourceUrl) { this.sourceUrl = sourceUrl; }

    public LocalDateTime getInfoVerifiedAt() { return infoVerifiedAt; }
    public void setInfoVerifiedAt(LocalDateTime infoVerifiedAt) { this.infoVerifiedAt = infoVerifiedAt; }

    public String getOpeningHours() { return openingHours; }
    public void setOpeningHours(String openingHours) { this.openingHours = openingHours; }

    public Long getEstimatedCostMinor() { return estimatedCostMinor; }
    public void setEstimatedCostMinor(Long estimatedCostMinor) { this.estimatedCostMinor = estimatedCostMinor; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}

