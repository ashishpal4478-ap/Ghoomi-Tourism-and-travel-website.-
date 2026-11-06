package com.ghoomi.modules.user;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Entity
@Table(name = "user_profiles")
public class UserProfile {

    @Id
    @Column(name = "user_id", columnDefinition = "CHAR(36)", nullable = false, updatable = false)
    private String id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id", columnDefinition = "CHAR(36)", nullable = false)
    private User user;

    @Column(name = "display_name", length = 150, nullable = false)
    private String displayName;

    @Column(name = "phone_e164", length = 20)
    private String phoneE164;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "travel_preferences", columnDefinition = "json", nullable = false)
    private String travelPreferences = "{}";

    @Column(name = "avatar_object_key", columnDefinition = "TEXT")
    private String avatarObjectKey;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected UserProfile() {}

    public UserProfile(User user) {
        this.user = user;
        if (user != null) {
            this.displayName = user.getFullName();
            this.phoneE164 = user.getPhoneE164();
        }
    }

    @PrePersist
    @PreUpdate
    protected void updateTimestamp() {
        updatedAt = LocalDateTime.now(ZoneOffset.UTC);
        if (travelPreferences == null) {
            travelPreferences = "{}";
        }
    }

    public String getId() {
        return user != null ? user.getId() : null;
    }

    public User getUser() { return user; }
    public void setUser(User user) {
        this.user = user;
        this.id = user != null ? user.getId() : null;
    }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPhoneE164() { return phoneE164; }
    public void setPhoneE164(String phoneE164) {
        this.phoneE164 = phoneE164;
    }

    public String getTravelPreferences() { return travelPreferences; }
    public void setTravelPreferences(String travelPreferences) {
        this.travelPreferences = travelPreferences;
    }

    public String getAvatarObjectKey() { return avatarObjectKey; }
    public void setAvatarObjectKey(String avatarObjectKey) {
        this.avatarObjectKey = avatarObjectKey;
    }

    // Compatibility aliases for older profile code.
    public String getAvatarUrl() { return avatarObjectKey; }
    public void setAvatarUrl(String avatarUrl) {
        this.avatarObjectKey = avatarUrl;
    }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
}