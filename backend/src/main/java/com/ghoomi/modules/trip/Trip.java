package com.ghoomi.modules.trip;

import com.ghoomi.modules.user.User;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

/**
 * JPA entity mapping the {@code trips} table.
 *
 * Column mapping strictly matches {@code V1__ghoomi_partition1_initial_schema.sql}:
 * <pre>
 *   id                  CHAR(36)      PK
 *   owner_user_id       CHAR(36)      FK -> users.id
 *   destination_id      CHAR(36)      FK -> destinations.id (nullable)
 *   title               VARCHAR(255)  NOT NULL
 *   origin_label        VARCHAR(200)  NOT NULL
 *   destination_label   VARCHAR(200)  NOT NULL
 *   start_date          DATE          NOT NULL
 *   end_date            DATE          NOT NULL
 *   timezone            VARCHAR(64)   NOT NULL DEFAULT 'Asia/Kolkata'
 *   traveler_count      INTEGER       NOT NULL CHECK (traveler_count > 0 AND traveler_count <= 50)
 *   plan_mode           VARCHAR(16)   NOT NULL DEFAULT 'BALANCED'
 *   target_budget_minor BIGINT        nullable
 *   currency            CHAR(3)       NOT NULL DEFAULT 'INR'
 *   own_vehicle         TINYINT(1)    NOT NULL DEFAULT FALSE
 *   already_have_stay   TINYINT(1)    NOT NULL DEFAULT FALSE
 *   preferences         JSON          NOT NULL DEFAULT (JSON_OBJECT())
 *   status              VARCHAR(35)   NOT NULL DEFAULT 'DRAFT'
 *   version             INTEGER       NOT NULL DEFAULT 1
 *   created_at          DATETIME(6)   NOT NULL
 *   updated_at          DATETIME(6)   NOT NULL
 * </pre>
 */
@Entity
@Table(name = "trips")
public class Trip {

    @Id
    @Column(name = "id", columnDefinition = "CHAR(36)", nullable = false, updatable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_user_id", columnDefinition = "CHAR(36)", nullable = false)
    private User owner;

    @Column(name = "destination_id", columnDefinition = "CHAR(36)")
    private String destinationId;

    @Column(name = "title", length = 255, nullable = false)
    private String title;

    @Column(name = "origin_label", length = 200, nullable = false)
    private String originLabel;

    @Column(name = "destination_label", length = 200, nullable = false)
    private String destinationLabel;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "timezone", length = 64, nullable = false)
    private String timezone = "Asia/Kolkata";

    @Column(name = "traveler_count", nullable = false)
    private Integer travelerCount = 1;

    @Enumerated(EnumType.STRING)
    @Column(name = "plan_mode", length = 16, nullable = false)
    private PlanMode planMode = PlanMode.BALANCED;

    @Column(name = "target_budget_minor")
    private Long targetBudgetMinor;

    @Column(name = "currency", columnDefinition = "CHAR(3)", length = 3, nullable = false)
    private String currency = "INR";

    @Column(name = "own_vehicle", nullable = false)
    private boolean ownVehicle = false;

    @Column(name = "already_have_stay", nullable = false)
    private boolean alreadyHaveStay = false;

    @Column(name = "preferences", columnDefinition = "json")
    @JdbcTypeCode(SqlTypes.JSON)
    private String preferences = "{}";

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 35, nullable = false)
    private TripStatus status = TripStatus.DRAFT;

    @Version
    @Column(name = "version", nullable = false)
    private Integer version = 1;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (id == null) {
            id = UUID.randomUUID().toString();
        }
        if (createdAt == null) {
            createdAt = LocalDateTime.now(ZoneOffset.UTC);
        }
        if (updatedAt == null) {
            updatedAt = createdAt;
        }
        if (version == null) {
            version = 1;
        }
        if (preferences == null || preferences.isBlank()) {
            preferences = "{}";
        }
        if (timezone == null || timezone.isBlank()) {
            timezone = "Asia/Kolkata";
        }
        if (currency == null || currency.isBlank()) {
            currency = "INR";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now(ZoneOffset.UTC);
    }

    // ── Constructors ──────────────────────────────────────────────────────────

    protected Trip() {}

    public Trip(User owner, String title, String originLabel, String destinationLabel,
                LocalDate startDate, LocalDate endDate, Integer travelerCount) {
        this.owner = owner;
        this.title = title;
        this.originLabel = originLabel;
        this.destinationLabel = destinationLabel;
        this.startDate = startDate;
        this.endDate = endDate;
        this.travelerCount = travelerCount != null ? travelerCount : 1;
        this.status = TripStatus.DRAFT;
        this.planMode = PlanMode.BALANCED;
        this.timezone = "Asia/Kolkata";
        this.currency = "INR";
        this.preferences = "{}";
        this.version = 1;
    }

    // ── Getters and Setters ───────────────────────────────────────────────────

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public User getOwner() { return owner; }
    public void setOwner(User owner) { this.owner = owner; }

    public String getDestinationId() { return destinationId; }
    public void setDestinationId(String destinationId) { this.destinationId = destinationId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getOriginLabel() { return originLabel; }
    public void setOriginLabel(String originLabel) { this.originLabel = originLabel; }

    public String getDestinationLabel() { return destinationLabel; }
    public void setDestinationLabel(String destinationLabel) { this.destinationLabel = destinationLabel; }

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
}

