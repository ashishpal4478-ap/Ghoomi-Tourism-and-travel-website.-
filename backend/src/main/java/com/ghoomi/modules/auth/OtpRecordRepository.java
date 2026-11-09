package com.ghoomi.modules.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Spring Data JPA repository for {@link OtpRecord}.
 */
@Repository
public interface OtpRecordRepository extends JpaRepository<OtpRecord, String> {

    /**
     * Find the latest unused, unexpired OTP for a given email and purpose.
     * Ordered newest-first so that any re-issued OTP supersedes the previous one.
     */
    Optional<OtpRecord> findTopByEmailAndPurposeAndUsedFalseAndExpiresAtAfterOrderByCreatedAtDesc(
            String email, OtpPurpose purpose, LocalDateTime now);

    long countByEmailAndPurpose(String email, OtpPurpose purpose);

    /**
     * Delete all OTP records for an email+purpose pair — called after a
     * successful reset to leave no dangling records.
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM OtpRecord o WHERE o.email = :email AND o.purpose = :purpose")
    void deleteAllByEmailAndPurpose(String email, OtpPurpose purpose);
}

