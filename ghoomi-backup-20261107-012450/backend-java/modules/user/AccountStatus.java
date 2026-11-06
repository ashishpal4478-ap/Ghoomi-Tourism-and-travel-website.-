package com.ghoomi.modules.user;

/**
 * Account lifecycle status — maps to the account_status
 * ENUM column in the users table.
 */
public enum AccountStatus {
    ACTIVE,
    SUSPENDED,
    DEACTIVATED,
    PENDING_VERIFICATION
}

