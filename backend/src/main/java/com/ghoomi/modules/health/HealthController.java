package com.ghoomi.modules.health;

import com.ghoomi.common.response.ApiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/api/v1/health")
public class HealthController {

    @Value("${spring.profiles.active:local}")
    private String activeProfile;

    @GetMapping
    public ResponseEntity<ApiResponse<HealthResponse>> getHealth() {
        HealthResponse health = new HealthResponse(
                "UP",
                "ghoomi-backend",
                "0.1.0-SNAPSHOT",
                activeProfile,
                Instant.now().toString()
        );
        return ResponseEntity.ok(ApiResponse.ok("Ghoomi backend is operational", health));
    }
}

