package com.ghoomi.modules.trip;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TripRepository extends JpaRepository<Trip, String> {

    List<Trip> findAllByOwnerIdOrderByUpdatedAtDesc(String ownerId);

    Optional<Trip> findByIdAndOwnerId(String id, String ownerId);

    List<Trip> findAllByOwnerIdAndStatusOrderByUpdatedAtDesc(String ownerId, TripStatus status);

    boolean existsByIdAndOwnerId(String id, String ownerId);
}

