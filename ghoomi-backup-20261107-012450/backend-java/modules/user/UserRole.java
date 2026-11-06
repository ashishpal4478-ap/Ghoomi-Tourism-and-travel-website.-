package com.ghoomi.modules.user;

/**
 * Application-level roles enforced server-side only.
 * Never trust roles from client requests.
 * Partner registration always creates a USER role.
 */
public enum UserRole {
    USER,
    SUPPORT,
    ADMIN,
    SUPER_ADMIN
}

