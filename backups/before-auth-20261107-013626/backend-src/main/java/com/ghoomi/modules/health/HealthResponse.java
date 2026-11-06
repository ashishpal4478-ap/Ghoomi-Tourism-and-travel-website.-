package com.ghoomi.modules.health;

public record HealthResponse(
        String status,
        String service,
        String version,
        String environment,
        String timestamp
) {}

