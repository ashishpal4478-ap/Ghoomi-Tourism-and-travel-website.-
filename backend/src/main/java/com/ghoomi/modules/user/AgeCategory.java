package com.ghoomi.modules.user;

/**
 * Age category for saved travellers — maps to the
 * age_category CHECK constraint in the saved_travellers table:
 * CHECK (age_category IN ('CHILD','ADULT','SENIOR','UNSPECIFIED'))
 */
public enum AgeCategory {
    CHILD,
    ADULT,
    SENIOR,
    UNSPECIFIED
}
