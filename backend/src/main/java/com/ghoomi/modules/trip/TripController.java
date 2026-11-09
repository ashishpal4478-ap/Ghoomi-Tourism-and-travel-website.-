package com.ghoomi.modules.trip;

import com.ghoomi.common.response.ApiResponse;
import com.ghoomi.modules.auth.AuthService;
import com.ghoomi.modules.trip.dto.*;
import com.ghoomi.modules.user.User;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for trip creation wizard, persistence, edit/resume, and version management.
 * All endpoints reside under {@code /api/v1/trips}.
 */
@RestController
@RequestMapping("/api/v1/trips")
public class TripController {

    private final TripService tripService;
    private final AuthService authService;

    public TripController(TripService tripService, AuthService authService) {
        this.tripService = tripService;
        this.authService = authService;
    }

    /**
     * POST /api/v1/trips
     * Creates a new trip with initial daily itinerary scaffolding.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<TripResponse>> createTrip(
            @Valid @RequestBody CreateTripRequest request,
            Authentication authentication) {
        User user = authService.getAuthenticatedUser(authentication);
        TripResponse response = tripService.createTrip(request, user);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Trip created successfully.", response));
    }

    /**
     * GET /api/v1/trips
     * Lists all trips owned by the authenticated user, optionally filtered by status (e.g. DRAFT).
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<TripResponse>>> getUserTrips(
            @RequestParam(required = false) TripStatus status,
            Authentication authentication) {
        User user = authService.getAuthenticatedUser(authentication);
        List<TripResponse> trips = tripService.getUserTrips(user, status);
        return ResponseEntity.ok(ApiResponse.ok(trips));
    }

    /**
     * GET /api/v1/trips/{id}
     * Retrieves full trip details including daily itinerary scaffolding.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TripResponse>> getTrip(
            @PathVariable String id,
            Authentication authentication) {
        User user = authService.getAuthenticatedUser(authentication);
        TripResponse response = tripService.getTrip(id, user);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    /**
     * PATCH /api/v1/trips/{id}
     * Updates an existing trip (incremental update, date modifications, optimistic lock check).
     */
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<TripResponse>> updateTripPatch(
            @PathVariable String id,
            @Valid @RequestBody UpdateTripRequest request,
            Authentication authentication) {
        User user = authService.getAuthenticatedUser(authentication);
        TripResponse response = tripService.updateTrip(id, request, user);
        return ResponseEntity.ok(ApiResponse.ok("Trip updated successfully.", response));
    }

    /**
     * PUT /api/v1/trips/{id}
     * Supports full or idempotent trip update.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TripResponse>> updateTripPut(
            @PathVariable String id,
            @Valid @RequestBody UpdateTripRequest request,
            Authentication authentication) {
        User user = authService.getAuthenticatedUser(authentication);
        TripResponse response = tripService.updateTrip(id, request, user);
        return ResponseEntity.ok(ApiResponse.ok("Trip updated successfully.", response));
    }

    /**
     * GET /api/v1/trips/{id}/preferences
     * Retrieves preferences and plan mode for a trip.
     */
    @GetMapping("/{id}/preferences")
    public ResponseEntity<ApiResponse<TripResponse>> getPreferences(
            @PathVariable String id,
            Authentication authentication) {
        User user = authService.getAuthenticatedUser(authentication);
        TripResponse response = tripService.getTrip(id, user);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    /**
     * PUT /api/v1/trips/{id}/preferences
     * Updates plan mode, budget, and travel preferences.
     */
    @PutMapping("/{id}/preferences")
    public ResponseEntity<ApiResponse<TripResponse>> updatePreferences(
            @PathVariable String id,
            @Valid @RequestBody TripPreferencesRequest request,
            Authentication authentication) {
        User user = authService.getAuthenticatedUser(authentication);
        TripResponse response = tripService.updatePreferences(id, request, user);
        return ResponseEntity.ok(ApiResponse.ok("Preferences updated successfully.", response));
    }

    /**
     * DELETE /api/v1/trips/{id}
     * Deletes a trip owned by the authenticated user.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTrip(
            @PathVariable String id,
            Authentication authentication) {
        User user = authService.getAuthenticatedUser(authentication);
        tripService.deleteTrip(id, user);
        return ResponseEntity.ok(ApiResponse.success("Trip deleted successfully."));
    }
}

