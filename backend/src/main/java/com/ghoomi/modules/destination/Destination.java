package com.ghoomi.modules.destination;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

/**
 * JPA entity mapping the {@code destinations} table.
 */
@Entity
@Table(name = "destinations")
public class Destination {

    @Id
    @Column(name = "id", columnDefinition = "CHAR(36)", nullable = false, updatable = false)
    private String id;

    @Column(name = "slug", length = 160, nullable = false, unique = true)
    private String slug;

    @Column(name = "name", length = 200, nullable = false)
    private String name;

    @Column(name = "state_region", length = 150)
    private String stateRegion;

    @Column(name = "country_code", columnDefinition = "CHAR(2)", length = 2, nullable = false)
    private String countryCode;

    @Column(name = "latitude", precision = 9, scale = 6)
    private BigDecimal latitude;

    @Column(name = "longitude", precision = 9, scale = 6)
    private BigDecimal longitude;

    @Column(name = "summary", columnDefinition = "TEXT")
    private String summary;

    @Column(name = "content_source", columnDefinition = "TEXT")
    private String contentSource;

    @Column(name = "image_license_ref", columnDefinition = "TEXT")
    private String imageLicenseRef;

    @Column(name = "active", nullable = false)
    private boolean active = true;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (id == null) {
            id = UUID.randomUUID().toString();
        }
        if (updatedAt == null) {
            updatedAt = LocalDateTime.now(ZoneOffset.UTC);
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now(ZoneOffset.UTC);
    }

    // ── Constructors ──────────────────────────────────────────────────────────

    protected Destination() {}

    /** Convenience constructor for tests and minimal seed data (slug + name only). */
    public Destination(String slug, String name) {
        this.slug = slug;
        this.name = name;
        this.active = true;
    }

    public Destination(String slug, String name, String stateRegion, String countryCode) {
        this.slug = slug;
        this.name = name;
        this.stateRegion = stateRegion;
        this.countryCode = countryCode;
        this.active = true;
    }

    // ── Getters and Setters ───────────────────────────────────────────────────

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

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}

