package com.ghoomi.modules.user;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * JPA entity mapping the {@code users} table.
 *
 * <p>UUID primary key: application-generated via {@link java.util.UUID#randomUUID()},
 * stored as {@code CHAR(36)} ASCII BINARY — no auto-increment.
 *
 * <p>Timestamps: all stored as {@code DATETIME(6)} in UTC.
 *
 * <p>Role: server-side only — never trust role values from client requests.
 */
@Entity
@Table(name = "users")
public class User {

    /** Application-generated UUID stored as CHAR(36). */
    @Id
    @Column(name = "id", columnDefinition = "CHAR(36)", nullable = false, updatable = false)
    private String id;

    @Column(name = "email", length = 254, nullable = false, unique = true)
    private String email;

    /** E.164-normalised phone number, e.g. {@code +919876543210}. */
    @Column(name = "phone_e164", length = 20)
    private String phoneE164;

    /** BCrypt-hashed password (cost factor 12). Never store plaintext. */
    @Column(name = "password_hash", length = 72, nullable = false)
    private String passwordHash;

    @Column(name = "full_name", length = 200, nullable = false)
    private String fullName;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private UserRole role;

    @Enumerated(EnumType.STRING)
    @Column(name = "account_status", nullable = false, length = 30)
    private AccountStatus accountStatus;

    @Column(name = "email_verified", nullable = false)
    private boolean emailVerified = false;

    @Column(name = "phone_verified", nullable = false)
    private boolean phoneVerified = false;

    @Column(name = "account_type", length = 20)
    private String accountType;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /** Set UUID before first persist. */
    @PrePersist
    protected void onCreate() {
        if (id == null) {
            id = java.util.UUID.randomUUID().toString();
        }
        LocalDateTime now = LocalDateTime.now(java.time.ZoneOffset.UTC);
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now(java.time.ZoneOffset.UTC);
    }

    // ── Constructors ─────────────────────────────────────────────────────────

    protected User() {}

    public User(String email, String phoneE164, String passwordHash, String fullName,
                UserRole role, AccountStatus accountStatus, String accountType) {
        this.email = email;
        this.phoneE164 = phoneE164;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
        this.role = role;
        this.accountStatus = accountStatus;
        this.accountType = accountType;
    }

    // ── Getters / Setters ────────────────────────────────────────────────────

    public String getId() { return id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhoneE164() { return phoneE164; }
    public void setPhoneE164(String phoneE164) { this.phoneE164 = phoneE164; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }

    public AccountStatus getAccountStatus() { return accountStatus; }
    public void setAccountStatus(AccountStatus accountStatus) { this.accountStatus = accountStatus; }

    public boolean isEmailVerified() { return emailVerified; }
    public void setEmailVerified(boolean emailVerified) { this.emailVerified = emailVerified; }

    public boolean isPhoneVerified() { return phoneVerified; }
    public void setPhoneVerified(boolean phoneVerified) { this.phoneVerified = phoneVerified; }

    public String getAccountType() { return accountType; }
    public void setAccountType(String accountType) { this.accountType = accountType; }

    public LocalDateTime getLastLoginAt() { return lastLoginAt; }
    public void setLastLoginAt(LocalDateTime lastLoginAt) { this.lastLoginAt = lastLoginAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}

