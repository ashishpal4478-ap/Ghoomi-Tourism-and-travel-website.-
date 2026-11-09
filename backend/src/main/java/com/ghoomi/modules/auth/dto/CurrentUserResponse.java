package com.ghoomi.modules.auth.dto;

/**
 * Safe user representation returned by /api/v1/auth/me and /api/v1/auth/login.
 * Never includes passwordHash or any credential.
 */
public class CurrentUserResponse {

    private String id;
    private String email;
    private String role;
    private String displayName;
    private String phoneE164;
    private String accountType;
    private boolean emailVerified;
    private String createdAt;

    // ── Factory ──────────────────────────────────────────────────────────────

    public static CurrentUserResponse from(com.ghoomi.modules.user.User user,
                                           com.ghoomi.modules.user.UserProfile profile) {
        CurrentUserResponse r = new CurrentUserResponse();
        r.id = user.getId();
        r.email = user.getEmail();
        r.role = user.getRole().name();
        r.emailVerified = user.isEmailVerified();
        r.accountType = user.getAccountType();
        r.createdAt = user.getCreatedAt() != null ? user.getCreatedAt().toString() : null;
        if (profile != null) {
            r.displayName = profile.getDisplayName();
            r.phoneE164 = profile.getPhoneE164();
        }
        return r;
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    public String getId() { return id; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public String getDisplayName() { return displayName; }
    public String getPhoneE164() { return phoneE164; }
    public String getAccountType() { return accountType; }
    public boolean isEmailVerified() { return emailVerified; }
    public String getCreatedAt() { return createdAt; }
}

