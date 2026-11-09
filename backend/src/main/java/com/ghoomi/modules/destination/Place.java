package com.ghoomi.modules.destination;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * JPA entity mapping the {@code places} table.
 * Covers food/restaurants, activities, and attractions.
 */
@Entity
@Table(name = "places")
public class Place {

    @Id
    @Column(name = "id", columnDefinition = "CHAR(36)", nullable = false, updatable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destination_id", columnDefinition = "CHAR(36)")
    private Destination destination;

    @Column(name = "name", length = 200, nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", length = 60, nullable = false)
    private PlaceCategory category;

    @Column(name = "address_label", columnDefinition = "TEXT")
    private String addressLabel;

    @Column(name = "latitude", precision = 9, scale = 6)
    private BigDecimal latitude;

    @Column(name = "longitude", precision = 9, scale = 6)
    private BigDecimal longitude;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "source_name", length = 180)
    private String sourceName;

    @Column(name = "source_url", columnDefinition = "TEXT")
    private String sourceUrl;

    @Column(name = "info_verified_at")
    private LocalDateTime infoVerifiedAt;

    @Column(name = "opening_hours", columnDefinition = "json")
    @JdbcTypeCode(SqlTypes.JSON)
    private String openingHours;

    @Column(name = "estimated_cost_minor")
    private Long estimatedCostMinor;

    @Column(name = "currency", columnDefinition = "CHAR(3)", length = 3)
    private String currency;

    @Column(name = "active", nullable = false)
    private boolean active = true;

    @PrePersist
    protected void onCreate() {
        if (id == null) {
            id = UUID.randomUUID().toString();
        }
    }

    // ── Constructors ──────────────────────────────────────────────────────────

    protected Place() {}

    public Place(Destination destination, String name, PlaceCategory category) {
        this.destination = destination;
        this.name = name;
        this.category = category;
        this.active = true;
    }

    // ── Getters and Setters ───────────────────────────────────────────────────

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Destination getDestination() { return destination; }
    public void setDestination(Destination destination) { this.destination = destination; }

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

