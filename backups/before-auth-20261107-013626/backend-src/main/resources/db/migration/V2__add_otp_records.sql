-- ============================================================
-- V2: Add otp_records table for password-reset OTP flow
-- Author : Backend Team
-- Applied: Partition 1 / IMP-02
-- Rules  : Flyway migrations are APPEND-ONLY. Never alter applied files.
-- ============================================================

CREATE TABLE IF NOT EXISTS otp_records (
    id          CHAR(36)     NOT NULL           COMMENT 'Application-generated UUID',
    email       VARCHAR(254) NOT NULL           COMMENT 'Target email address',
    otp_hash    VARCHAR(72)  NOT NULL           COMMENT 'BCrypt hash of the 6-digit OTP',
    purpose     ENUM('PASSWORD_RESET') NOT NULL COMMENT 'OTP use-case discriminator',
    expires_at  DATETIME(6)  NOT NULL           COMMENT 'UTC expiry timestamp',
    used        TINYINT(1)   NOT NULL DEFAULT 0 COMMENT 'Consumed flag (single-use)',
    attempts    TINYINT      NOT NULL DEFAULT 0 COMMENT 'Failed verification count',
    created_at  DATETIME(6)  NOT NULL           COMMENT 'UTC creation timestamp',

    PRIMARY KEY (id),
    INDEX idx_otp_email_purpose (email, purpose),
    INDEX idx_otp_expires_at    (expires_at)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_0900_ai_ci
  COMMENT='Single-use OTP records for password reset';

