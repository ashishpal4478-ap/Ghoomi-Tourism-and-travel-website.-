package com.ghoomi.modules.destination;

import com.ghoomi.common.response.ApiResponse;
import com.ghoomi.modules.destination.dto.DestinationResponse;
import com.ghoomi.modules.destination.dto.PlaceResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Public REST Controller for browsing and searching Destinations, Places, Food & Activities.
 */
@RestController
@RequestMapping("/api/v1")
public class DestinationController {

    private final DestinationService destinationService;

    public DestinationController(DestinationService destinationService) {
        this.destinationService = destinationService;
    }

    /**
     * GET /api/v1/destinations
     * List all active destinations, optionally filtered by keyword query.
     */
    @GetMapping("/destinations")
    public ResponseEntity<ApiResponse<List<DestinationResponse>>> getDestinations(
            @RequestParam(required = false) String query) {
        List<DestinationResponse> destinations = destinationService.getDestinations(query);
        return ResponseEntity.ok(ApiResponse.ok("Destinations retrieved successfully.", destinations));
    }

    /**
     * GET /api/v1/destinations/{slug}
     * Get single destination details by unique slug.
     */
    @GetMapping("/destinations/{slug}")
    public ResponseEntity<ApiResponse<DestinationResponse>> getDestinationBySlug(
            @PathVariable String slug) {
        DestinationResponse destination = destinationService.getDestinationBySlug(slug);
        return ResponseEntity.ok(ApiResponse.ok("Destination retrieved successfully.", destination));
    }

    /**
     * GET /api/v1/destinations/{id}/places
     * Get places belonging to a destination, optionally filtered by category.
     */
    @GetMapping("/destinations/{id}/places")
    public ResponseEntity<ApiResponse<List<PlaceResponse>>> getPlacesByDestination(
            @PathVariable String id,
            @RequestParam(required = false) PlaceCategory category) {
        List<PlaceResponse> places = destinationService.getPlaces(id, category, null);
        return ResponseEntity.ok(ApiResponse.ok("Places retrieved successfully.", places));
    }

    /**
     * GET /api/v1/places
     * Global search and filtering of places by category (ATTRACTION, RESTAURANT, ACTIVITY) or search query.
     */
    @GetMapping("/places")
    public ResponseEntity<ApiResponse<List<PlaceResponse>>> getPlaces(
            @RequestParam(required = false) String destinationId,
            @RequestParam(required = false) PlaceCategory category,
            @RequestParam(required = false) String query) {
        List<PlaceResponse> places = destinationService.getPlaces(destinationId, category, query);
        return ResponseEntity.ok(ApiResponse.ok("Places retrieved successfully.", places));
    }

    /**
     * GET /api/v1/places/{id}
     * Get single place detail by ID.
     */
    @GetMapping("/places/{id}")
    public ResponseEntity<ApiResponse<PlaceResponse>> getPlaceById(
            @PathVariable String id) {
        PlaceResponse place = destinationService.getPlaceById(id);
        return ResponseEntity.ok(ApiResponse.ok("Place retrieved successfully.", place));
    }
}

