package com.ghoomi.modules.destination.dto;

import com.ghoomi.modules.destination.Destination;

import java.math.BigDecimal;

public class DestinationResponse {

    private String id;
    private String slug;
    private String name;
    private String stateRegion;
    private String countryCode;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String summary;
    private String contentSource;
    private String imageLicenseRef;
    private boolean active;

    public DestinationResponse() {}

    public static DestinationResponse from(Destination destination) {
        if (destination == null) return null;
        DestinationResponse res = new DestinationResponse();
        res.id = destination.getId();
        res.slug = destination.getSlug();
        res.name = destination.getName();
        res.stateRegion = destination.getStateRegion();
        res.countryCode = destination.getCountryCode();
        res.latitude = destination.getLatitude();
        res.longitude = destination.getLongitude();
        res.summary = destination.getSummary();
        res.contentSource = destination.getContentSource();
        res.imageLicenseRef = destination.getImageLicenseRef();
        res.active = destination.isActive();
        return res;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getStateRegion() { return stateRegion; }
    public void setStateRegion(String stateRegion) { this.stateRegion = stateRegion; }

    public String getCountryCode() { return countryCode; }
    public void setCountryCode(String countryCode) { this.countryCode = countryCode; }

    public BigDecimal getLatitude() { return latitude; }
    public void setLatitude(BigDecimal latitude) { this.latitude = latitude; }

    public BigDecimal getLongitude() { return longitude; }
    public void setLongitude(BigDecimal longitude) { this.longitude = longitude; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public String getContentSource() { return contentSource; }
    public void setContentSource(String contentSource) { this.contentSource = contentSource; }

    public String getImageLicenseRef() { return imageLicenseRef; }
    public void setImageLicenseRef(String imageLicenseRef) { this.imageLicenseRef = imageLicenseRef; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}

