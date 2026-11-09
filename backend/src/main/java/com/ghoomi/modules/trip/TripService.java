package com.ghoomi.modules.trip;

import com.ghoomi.common.exception.ConflictException;
import com.ghoomi.common.exception.ResourceNotFoundException;
import com.ghoomi.modules.trip.dto.*;
import com.ghoomi.modules.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class TripService {

    private final TripRepository tripRepository;
    private final ItineraryDayRepository itineraryDayRepository;

    public TripService(TripRepository tripRepository, ItineraryDayRepository itineraryDayRepository) {
        this.tripRepository = tripRepository;
        this.itineraryDayRepository = itineraryDayRepository;
    }

    /**
     * Creates a new trip for the authenticated user and initializes daily scaffolding.
     */
    public TripResponse createTrip(CreateTripRequest request, User user) {
        validateDateRange(request.getStartDate(), request.getEndDate());

        Trip trip = new Trip(
                user,
                request.getTitle(),
                request.getOriginLabel(),
                request.getDestinationLabel(),
                request.getStartDate(),
                request.getEndDate(),
                request.getTravelerCount()
        );

        trip.setDestinationId(request.getDestinationId());
        if (request.getTimezone() != null && !request.getTimezone().isBlank()) {
            trip.setTimezone(request.getTimezone());
        }
        if (request.getPlanMode() != null) {
            trip.setPlanMode(request.getPlanMode());
        }
        if (request.getTargetBudgetMinor() != null) {
            trip.setTargetBudgetMinor(request.getTargetBudgetMinor());
        }
        if (request.getCurrency() != null && !request.getCurrency().isBlank()) {
            trip.setCurrency(request.getCurrency());
        }
        trip.setOwnVehicle(request.isOwnVehicle());
        trip.setAlreadyHaveStay(request.isAlreadyHaveStay());
        if (request.getPreferences() != null && !request.getPreferences().isBlank()) {
            trip.setPreferences(request.getPreferences());
        }

        Trip savedTrip = tripRepository.save(trip);

        List<ItineraryDay> days = createDayScaffolding(savedTrip, request.getStartDate(), request.getEndDate());

        return TripResponse.from(savedTrip, days);
    }

    /**
     * Retrieves a single trip by ID, strictly enforcing user ownership.
     */
    @Transactional(readOnly = true)
    public TripResponse getTrip(String tripId, User user) {
        Trip trip = findUserTripOrThrow(tripId, user.getId());
        List<ItineraryDay> days = itineraryDayRepository.findAllByTripIdOrderByDayIndexAsc(tripId);
        return TripResponse.from(trip, days);
    }

    /**
     * Lists all trips owned by the user, optionally filtered by status.
     */
    @Transactional(readOnly = true)
    public List<TripResponse> getUserTrips(User user, TripStatus status) {
        List<Trip> trips;
        if (status != null) {
            trips = tripRepository.findAllByOwnerIdAndStatusOrderByUpdatedAtDesc(user.getId(), status);
        } else {
            trips = tripRepository.findAllByOwnerIdOrderByUpdatedAtDesc(user.getId());
        }
        return trips.stream()
                .map(t -> TripResponse.from(t, null))
                .toList();
    }

    /**
     * Updates an existing trip, checking optimistic lock version and adjusting day scaffolding if dates changed.
     */
    public TripResponse updateTrip(String tripId, UpdateTripRequest request, User user) {
        Trip trip = findUserTripOrThrow(tripId, user.getId());

        // Optimistic concurrency check
        if (request.getVersion() != null && !request.getVersion().equals(trip.getVersion())) {
            throw new ConflictException("Trip version conflict. Expected version "
                    + trip.getVersion() + ", but received " + request.getVersion()
                    + ". Please refresh the trip before saving changes.");
        }

        boolean datesChanged = false;
        LocalDate newStart = request.getStartDate() != null ? request.getStartDate() : trip.getStartDate();
        LocalDate newEnd = request.getEndDate() != null ? request.getEndDate() : trip.getEndDate();

        if (request.getStartDate() != null || request.getEndDate() != null) {
            validateDateRange(newStart, newEnd);
            if (!newStart.equals(trip.getStartDate()) || !newEnd.equals(trip.getEndDate())) {
                datesChanged = true;
                trip.setStartDate(newStart);
                trip.setEndDate(newEnd);
            }
        }

        if (request.getTitle() != null && !request.getTitle().isBlank()) {
            trip.setTitle(request.getTitle());
        }
        if (request.getOriginLabel() != null && !request.getOriginLabel().isBlank()) {
            trip.setOriginLabel(request.getOriginLabel());
        }
        if (request.getDestinationLabel() != null && !request.getDestinationLabel().isBlank()) {
            trip.setDestinationLabel(request.getDestinationLabel());
        }
        if (request.getDestinationId() != null) {
            trip.setDestinationId(request.getDestinationId());
        }
        if (request.getTimezone() != null && !request.getTimezone().isBlank()) {
            trip.setTimezone(request.getTimezone());
        }
        if (request.getTravelerCount() != null) {
            trip.setTravelerCount(request.getTravelerCount());
        }
        if (request.getPlanMode() != null) {
            trip.setPlanMode(request.getPlanMode());
        }
        if (request.getTargetBudgetMinor() != null) {
            trip.setTargetBudgetMinor(request.getTargetBudgetMinor());
        }
        if (request.getCurrency() != null && !request.getCurrency().isBlank()) {
            trip.setCurrency(request.getCurrency());
        }
        if (request.getOwnVehicle() != null) {
            trip.setOwnVehicle(request.getOwnVehicle());
        }
        if (request.getAlreadyHaveStay() != null) {
            trip.setAlreadyHaveStay(request.getAlreadyHaveStay());
        }
        if (request.getPreferences() != null && !request.getPreferences().isBlank()) {
            trip.setPreferences(request.getPreferences());
        }
        if (request.getStatus() != null) {
            trip.setStatus(request.getStatus());
        }

        Trip savedTrip = tripRepository.save(trip);

        List<ItineraryDay> days;
        if (datesChanged) {
            itineraryDayRepository.deleteAllByTripId(tripId);
            itineraryDayRepository.flush();
            days = createDayScaffolding(savedTrip, newStart, newEnd);
        } else {
            days = itineraryDayRepository.findAllByTripIdOrderByDayIndexAsc(tripId);
        }

        return TripResponse.from(savedTrip, days);
    }

    /**
     * Updates trip preferences and plan mode directly.
     */
    public TripResponse updatePreferences(String tripId, TripPreferencesRequest request, User user) {
        Trip trip = findUserTripOrThrow(tripId, user.getId());

        if (request.getPlanMode() != null) {
            trip.setPlanMode(request.getPlanMode());
        }
        if (request.getTargetBudgetMinor() != null) {
            trip.setTargetBudgetMinor(request.getTargetBudgetMinor());
        }
        if (request.getOwnVehicle() != null) {
            trip.setOwnVehicle(request.getOwnVehicle());
        }
        if (request.getAlreadyHaveStay() != null) {
            trip.setAlreadyHaveStay(request.getAlreadyHaveStay());
        }
        if (request.getPreferences() != null && !request.getPreferences().isBlank()) {
            trip.setPreferences(request.getPreferences());
        }

        Trip saved = tripRepository.save(trip);
        List<ItineraryDay> days = itineraryDayRepository.findAllByTripIdOrderByDayIndexAsc(tripId);
        return TripResponse.from(saved, days);
    }

    /**
     * Deletes a trip owned by the authenticated user.
     */
    public void deleteTrip(String tripId, User user) {
        Trip trip = findUserTripOrThrow(tripId, user.getId());
        itineraryDayRepository.deleteAllByTripId(tripId);
        tripRepository.delete(trip);
    }

    // ── Internal Helpers ──────────────────────────────────────────────────────

    private Trip findUserTripOrThrow(String tripId, String userId) {
        return tripRepository.findByIdAndOwnerId(tripId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found or not accessible with id: " + tripId));
    }

    private void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Start date and end date are required.");
        }
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("End date cannot be earlier than start date.");
        }
        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        if (daysBetween > 60) {
            throw new IllegalArgumentException("Trip duration cannot exceed 60 days.");
        }
    }

    private List<ItineraryDay> createDayScaffolding(Trip trip, LocalDate startDate, LocalDate endDate) {
        long totalDays = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        List<ItineraryDay> days = new ArrayList<>((int) totalDays);

        for (int i = 0; i < totalDays; i++) {
            LocalDate currentDate = startDate.plusDays(i);
            int dayIndex = i + 1;
            String dayTitle = "Day " + dayIndex;
            ItineraryDay day = new ItineraryDay(trip, currentDate, dayIndex, dayTitle);
            days.add(day);
        }

        return itineraryDayRepository.saveAll(days);
    }
}

