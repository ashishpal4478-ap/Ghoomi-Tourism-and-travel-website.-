package com.ghoomi.modules.destination;

/**
 * Category of a place in Ghoomi.
 * Matches SQL check constraint: CHECK (category IN ('ATTRACTION','RESTAURANT','ACTIVITY','OTHER'))
 */
public enum PlaceCategory {
    ATTRACTION,
    RESTAURANT,
    ACTIVITY,
    OTHER
}

