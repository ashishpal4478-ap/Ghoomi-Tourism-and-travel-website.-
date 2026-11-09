package com.ghoomi.modules.trip;

/**
 * Plan mode style for trip generation.
 * Matches check constraint: CHECK (plan_mode IN ('BUDGET','BALANCED','COMFORT','CUSTOM'))
 */
public enum PlanMode {
    BUDGET,
    BALANCED,
    COMFORT,
    CUSTOM
}

