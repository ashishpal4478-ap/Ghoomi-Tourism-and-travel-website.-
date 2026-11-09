package com.ghoomi.modules.trip;

/**
 * Status of a trip in the Ghoomi platform.
 * Matches check constraint: CHECK (status IN ('DRAFT','PLANNED','ACTIVE','COMPLETED','CANCELLED'))
 */
public enum TripStatus {
    DRAFT,
    PLANNED,
    ACTIVE,
    COMPLETED,
    CANCELLED
}

