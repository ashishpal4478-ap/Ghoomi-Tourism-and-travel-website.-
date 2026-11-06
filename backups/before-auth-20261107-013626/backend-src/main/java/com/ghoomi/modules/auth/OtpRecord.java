package com.ghoomi.modules.auth;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * JPA entity mapping the {@code otp_records} table (added in V2 migration).
 *
 * <p>
 * OTP values are stored as a BCrypt hash — never plaintext.
 * A record is consumed (single-use) on successful verification.
 */
@Entity
@Table(name = "otp_records")
public class OtpRecord {

    @Id
    @Column(name = "id", columnDefinition = "CHAR(36)", nullable = false, updatable = false)
    private String id;

    /** The email address this OTP was issued to. */
    @Column(name = "email", length = 254, nullable = false)
    private String email;

    /** BCrypt hash of the 6-digit OTP — never store plaintext. */
    @Column(name = "otp_hash", length = 72, nullable = false)
    private String otpHash;

    /**
     * Purpose discriminator — currently only PASSWORD_RESET is used in Partition 1.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "purpose", length = 30, nullable = false)
    private OtpPurpose purpose;

    /** UTC timestamp after which this OTP must be rejected. */
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    /** Whether this record has already been used. */
    @Column(name = "used", nullable = false)
    private boolean used = false;

    /** Number of verification attempts made against this OTP. */
    @Column(name = "attempts", nullable = false)
    private byte attempts = 0;

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

    protected OtpRecord() {
    }

    public OtpRecord(String email, String otpHash, OtpPurpose purpose, LocalDateTime expiresAt) {
        this.email = email;
        this.otpHash = otpHash;
        this.purpose = purpose;
        this.expiresAt = expiresAt;
    }

    // ── Getters / Setters ────────────────────────────────────────────────────

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getOtpHash() {
        return otpHash;
    }

    public OtpPurpose getPurpose() {
        return purpose;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    public int getAttempts() {
        return attempts;
    }

    public void incrementAttempts() {
        this.attempts++;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
