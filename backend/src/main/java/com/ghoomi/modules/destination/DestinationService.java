package com.ghoomi.modules.destination;

import com.ghoomi.common.exception.ResourceNotFoundException;
import com.ghoomi.modules.destination.dto.DestinationResponse;
import com.ghoomi.modules.destination.dto.PlaceResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class DestinationService {

    private final DestinationRepository destinationRepository;
    private final PlaceRepository placeRepository;

    public DestinationService(DestinationRepository destinationRepository, PlaceRepository placeRepository) {
        this.destinationRepository = destinationRepository;
        this.placeRepository = placeRepository;
    }

    public List<DestinationResponse> getDestinations(String query) {
        List<Destination> destinations;
        if (query != null && !query.isBlank()) {
            destinations = destinationRepository.searchDestinations(query.trim());
        } else {
            destinations = destinationRepository.findAllByActiveTrueOrderByNameAsc();
        }
        return destinations.stream()
                .map(DestinationResponse::from)
                .toList();
    }

    public DestinationResponse getDestinationBySlug(String slug) {
        Destination destination = destinationRepository.findBySlugAndActiveTrue(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Destination not found with slug: " + slug));
        return DestinationResponse.from(destination);
    }

    public DestinationResponse getDestinationById(String id) {
        Destination destination = destinationRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Destination not found with id: " + id));
        return DestinationResponse.from(destination);
    }

    public List<PlaceResponse> getPlaces(String destinationId, PlaceCategory category, String query) {
        List<Place> places;
        if (destinationId != null && !destinationId.isBlank()) {
            if (category != null) {
                places = placeRepository.findAllByDestinationIdAndCategoryAndActiveTrue(destinationId, category);
            } else {
                places = placeRepository.findAllByDestinationIdAndActiveTrue(destinationId);
            }
        } else if (category != null && query != null && !query.isBlank()) {
            places = placeRepository.searchPlacesByCategory(category, query.trim());
        } else if (category != null) {
            places = placeRepository.findAllByCategoryAndActiveTrue(category);
        } else if (query != null && !query.isBlank()) {
            places = placeRepository.searchPlaces(query.trim());
        } else {
            places = placeRepository.findAll().stream().filter(Place::isActive).toList();
        }

        return places.stream()
                .map(PlaceResponse::from)
                .toList();
    }

    public PlaceResponse getPlaceById(String id) {
        Place place = placeRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Place not found with id: " + id));
        return PlaceResponse.from(place);
    }
}

