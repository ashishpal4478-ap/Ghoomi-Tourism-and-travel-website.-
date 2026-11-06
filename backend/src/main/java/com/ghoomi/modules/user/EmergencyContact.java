package com.ghoomi.modules.user;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * JPA entity mapping the {@code emergency_contacts} table.
 *
 * <p>Column mapping matches {@code V1__ghoomi_partition1_reference.sql} exactly:
 * <pre>
 *   id                CHAR(36)      PK
 *   owner_user_id     CHAR(36)      FK → users.id  ON DELETE CASCADE
 *   contact_name      VARCHAR(150)  NOT NULL
 *   contact_phone_e164 VARCHAR(20)  NOT NULL
 *   relationship_label VARCHAR(100) nullable
 *   verified_at       DATETIME(6)   nullable
 *   created_at        DATETIME(6)   NOT NULL
 * </pre>
 *
 * <p>No {@code updated_at}, {@code email}, or {@code is_primary} columns exist
 * in the reference schema — those fields are intentionally absent here.
 */
@Entity
@Table(name = "emergency_contacts")
public class EmergencyContact {

    /** Application-generated UUID stored as CHAR(36) ASCII BINARY. */
    @Id
    @Column(name = "id", columnDefinition = "CHAR(36)", nullable = false, updatable = false)
    private String id;

    /** FK → users.id — the account holder who owns this emergency contact. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_user_id", columnDefinition = "CHAR(36)", nullable = false)
    private User owner;

    /** Maps to {@code contact_name VARCHAR(150) NOT NULL}. */
    @Column(name = "contact_name", length = 150, nullable = false)
    private String contactName;

    /** Maps to {@code contact_phone_e164 VARCHAR(20) NOT NULL}. */
    @Column(name = "contact_phone_e164", length = 20, nullable = false)
    private String contactPhoneE164;

    /** Maps to {@code relationship_label VARCHAR(100)} — nullable. */
    @Column(name = "relationship_label", length = 100)
    private String relationshipLabel;

    /** Maps to {@code verified_at DATETIME(6)} — null until the contact is verified. */
    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    /** Maps to {@code created_at DATETIME(6) NOT NULL}. Set once on persist. */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (id == null) {
            id = java.util.UUID.randomUUID().toString();
        }
        createdAt = LocalDateTime.now(java.time.ZoneOffset.UTC);
    }

    // ── Constructors ─────────────────────────────────────────────────────────

    protected EmergencyContact() {}

    /**
     * Convenience constructor for the minimum required fields.
     *
     * @param owner          the owning user account
     * @param contactName    full name of the emergency contact
     * @param contactPhoneE164 E.164 phone number, e.g. {@code +919876543210}
     */
    public EmergencyContact(User owner, String contactName, String contactPhoneE164) {
        this.owner = owner;
        this.contactName = contactName;
        this.contactPhoneE164 = contactPhoneE164;
    }

    // ── Getters / Setters ────────────────────────────────────────────────────

    public String getId() { return id; }

    public User getOwner() { return owner; }
    public void setOwner(User owner) { this.owner = owner; }

    public String getContactName() { return contactName; }
    public void setContactName(String contactName) { this.contactName = contactName; }

    public String getContactPhoneE164() { return contactPhoneE164; }
    public void setContactPhoneE164(String contactPhoneE164) { this.contactPhoneE164 = contactPhoneE164; }

    public String getRelationshipLabel() { return relationshipLabel; }
    public void setRelationshipLabel(String relationshipLabel) { this.relationshipLabel = relationshipLabel; }

    public LocalDateTime getVerifiedAt() { return verifiedAt; }
    public void setVerifiedAt(LocalDateTime verifiedAt) { this.verifiedAt = verifiedAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
}
