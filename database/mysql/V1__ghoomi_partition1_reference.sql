-- Ghoomi Partition 1 reference schema (MySQL 8.4 LTS / InnoDB).
-- Generate UUIDs in Java (UUID.randomUUID().toString()); persist as CHAR(36) ASCII BINARY collation.
-- All DATETIME(6) values are UTC by application contract. Set JDBC connectionTimeZone=UTC and configure UTC session time zone.
-- Flyway should manage versioned migrations. Run only on a fresh development/staging database after review.
-- Do not disable FOREIGN_KEY_CHECKS for normal application writes.
SET time_zone = '+00:00';

CREATE TABLE users (
  id CHAR(36) CHARACTER SET ascii COLLATE ascii_bin PRIMARY KEY,
  email VARCHAR(254) COLLATE utf8mb4_0900_ai_ci NOT NULL UNIQUE,
  password_hash VARCHAR(255) NOT NULL,
  email_verified_at DATETIME(6),
  role VARCHAR(191) NOT NULL DEFAULT 'USER' CHECK (role IN ('USER','SUPPORT','ADMIN','SUPER_ADMIN')),
  is_active TINYINT(1) NOT NULL DEFAULT TRUE,
  created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE user_profiles (
  user_id CHAR(36) CHARACTER SET ascii COLLATE ascii_bin PRIMARY KEY,
  display_name VARCHAR(150) NOT NULL,
  phone_e164 VARCHAR(20),
  travel_preferences JSON NOT NULL DEFAULT (JSON_OBJECT()),
  avatar_object_key TEXT,
  updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  CONSTRAINT fk_user_profiles_user_id FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE saved_travellers (
  id CHAR(36) CHARACTER SET ascii COLLATE ascii_bin PRIMARY KEY,
  owner_user_id CHAR(36) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
  display_name VARCHAR(150) NOT NULL,
  age_category VARCHAR(191) CHECK (age_category IN ('CHILD','ADULT','SENIOR','UNSPECIFIED')),
  notes TEXT,
  created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  CONSTRAINT fk_saved_travellers_owner_user_id FOREIGN KEY (owner_user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE emergency_contacts (
  id CHAR(36) CHARACTER SET ascii COLLATE ascii_bin PRIMARY KEY,
  owner_user_id CHAR(36) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
  contact_name VARCHAR(150) NOT NULL,
  contact_phone_e164 VARCHAR(20) NOT NULL,
  relationship_label VARCHAR(100),
  verified_at DATETIME(6),
  created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  CONSTRAINT fk_emergency_contacts_owner_user_id FOREIGN KEY (owner_user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE destinations (
  id CHAR(36) CHARACTER SET ascii COLLATE ascii_bin PRIMARY KEY,
  slug VARCHAR(160) NOT NULL UNIQUE,
  name VARCHAR(200) NOT NULL,
  state_region VARCHAR(150),
  country_code CHAR(2) NOT NULL,
  latitude DECIMAL(9,6),
  longitude DECIMAL(9,6),
  summary TEXT,
  content_source TEXT,
  image_license_ref TEXT,
  active TINYINT(1) NOT NULL DEFAULT TRUE,
  updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE places (
  id CHAR(36) CHARACTER SET ascii COLLATE ascii_bin PRIMARY KEY,
  destination_id CHAR(36) CHARACTER SET ascii COLLATE ascii_bin,
  name VARCHAR(200) NOT NULL,
  category VARCHAR(60) NOT NULL CHECK (category IN ('ATTRACTION','RESTAURANT','ACTIVITY','OTHER')),
  address_label TEXT,
  latitude DECIMAL(9,6),
  longitude DECIMAL(9,6),
  description TEXT,
  source_name VARCHAR(180),
  source_url TEXT,
  info_verified_at DATETIME(6),
  opening_hours JSON,
  estimated_cost_minor BIGINT CHECK (estimated_cost_minor >= 0),
  currency CHAR(3),
  active TINYINT(1) NOT NULL DEFAULT TRUE,
  CONSTRAINT fk_places_destination_id FOREIGN KEY (destination_id) REFERENCES destinations(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE providers (
  id CHAR(36) CHARACTER SET ascii COLLATE ascii_bin PRIMARY KEY,
  code VARCHAR(80) COLLATE utf8mb4_0900_bin NOT NULL UNIQUE,
  name VARCHAR(200) NOT NULL,
  vertical VARCHAR(32) NOT NULL CHECK (vertical IN ('FLIGHT','TRAIN','BUS','CAB','STAY','ACTIVITY','MAP','PAYMENT')),
  capability VARCHAR(40) NOT NULL DEFAULT 'PLANNING_ONLY'
    CHECK (capability IN ('PLANNING_ONLY','REDIRECT','LIVE_SEARCH_EXTERNAL_CHECKOUT','DIRECT_BOOKING')),
  authorization_status VARCHAR(25) NOT NULL DEFAULT 'UNVERIFIED'
    CHECK (authorization_status IN ('UNVERIFIED','UNDER_REVIEW','APPROVED','SUSPENDED')),
  credentials_ref VARCHAR(255), -- secret-manager reference only
  terms_url TEXT,
  enabled TINYINT(1) NOT NULL DEFAULT FALSE,
  updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE provider_offers (
  id CHAR(36) CHARACTER SET ascii COLLATE ascii_bin PRIMARY KEY,
  provider_id CHAR(36) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
  provider_offer_key VARCHAR(160) COLLATE utf8mb4_0900_bin NOT NULL,
  vertical VARCHAR(32) NOT NULL,
  name VARCHAR(200) NOT NULL,
  details JSON NOT NULL DEFAULT (JSON_OBJECT()),
  origin_label VARCHAR(200),
  destination_label VARCHAR(200),
  starts_at DATETIME(6),
  ends_at DATETIME(6),
  price_minor BIGINT CHECK (price_minor >= 0),
  currency CHAR(3),
  price_basis VARCHAR(24) CHECK (price_basis IN ('PER_PERSON','PER_GROUP','PER_ROOM','TOTAL','UNKNOWN')),
  data_status VARCHAR(24) NOT NULL CHECK (data_status IN ('VERIFIED_LIVE','PROVIDER_LISTED','ESTIMATED','UNAVAILABLE')),
  checked_at DATETIME(6) NOT NULL,
  valid_until DATETIME(6),
  terms_url TEXT,
  redirect_url TEXT,
  created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  UNIQUE(provider_id, provider_offer_key, checked_at),
  CONSTRAINT fk_provider_offers_provider_id FOREIGN KEY (provider_id) REFERENCES providers(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE trips (
  id CHAR(36) CHARACTER SET ascii COLLATE ascii_bin PRIMARY KEY,
  owner_user_id CHAR(36) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
  destination_id CHAR(36) CHARACTER SET ascii COLLATE ascii_bin,
  title VARCHAR(255) NOT NULL,
  origin_label VARCHAR(200) NOT NULL,
  destination_label VARCHAR(200) NOT NULL,
  start_date DATE NOT NULL,
  end_date DATE NOT NULL,
  timezone VARCHAR(64) NOT NULL DEFAULT 'Asia/Kolkata',
  traveler_count INTEGER NOT NULL CHECK (traveler_count > 0 AND traveler_count <= 50),
  plan_mode VARCHAR(16) NOT NULL DEFAULT 'BALANCED'
    CHECK (plan_mode IN ('BUDGET','BALANCED','COMFORT','CUSTOM')),
  target_budget_minor BIGINT CHECK (target_budget_minor >= 0),
  currency CHAR(3) NOT NULL DEFAULT 'INR',
  own_vehicle TINYINT(1) NOT NULL DEFAULT FALSE,
  already_have_stay TINYINT(1) NOT NULL DEFAULT FALSE,
  preferences JSON NOT NULL DEFAULT (JSON_OBJECT()),
  status VARCHAR(35) NOT NULL DEFAULT 'DRAFT'
    CHECK (status IN ('DRAFT','PLANNED','ACTIVE','COMPLETED','CANCELLED')),
  version INTEGER NOT NULL DEFAULT 1 CHECK (version >= 1),
  created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  CHECK (end_date >= start_date),
  CONSTRAINT fk_trips_owner_user_id FOREIGN KEY (owner_user_id) REFERENCES users(id),
  CONSTRAINT fk_trips_destination_id FOREIGN KEY (destination_id) REFERENCES destinations(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE itinerary_days (
  id CHAR(36) CHARACTER SET ascii COLLATE ascii_bin PRIMARY KEY,
  trip_id CHAR(36) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
  day_date DATE NOT NULL,
  day_index INTEGER NOT NULL CHECK (day_index >= 1),
  title VARCHAR(255),
  UNIQUE(trip_id,day_date),
  UNIQUE(trip_id,day_index),
  CONSTRAINT fk_itinerary_days_trip_id FOREIGN KEY (trip_id) REFERENCES trips(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE itinerary_items (
  id CHAR(36) CHARACTER SET ascii COLLATE ascii_bin PRIMARY KEY,
  itinerary_day_id CHAR(36) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
  place_id CHAR(36) CHARACTER SET ascii COLLATE ascii_bin,
  item_order INTEGER NOT NULL CHECK (item_order >= 0),
  category VARCHAR(60) NOT NULL,
  title VARCHAR(255) NOT NULL,
  planned_start TIME,
  planned_end TIME,
  notes TEXT,
  estimate_minor BIGINT CHECK (estimate_minor >= 0),
  currency CHAR(3),
  verification_status VARCHAR(32) NOT NULL DEFAULT 'USER_PLANNED'
    CHECK (verification_status IN ('USER_PLANNED','AI_SUGGESTED','PROVIDER_LISTED','VERIFIED')),
  source_ref TEXT,
  created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  UNIQUE(itinerary_day_id,item_order),
  CONSTRAINT fk_itinerary_items_itinerary_day_id FOREIGN KEY (itinerary_day_id) REFERENCES itinerary_days(id) ON DELETE CASCADE,
  CONSTRAINT fk_itinerary_items_place_id FOREIGN KEY (place_id) REFERENCES places(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE trip_budget_items (
  id CHAR(36) CHARACTER SET ascii COLLATE ascii_bin PRIMARY KEY,
  trip_id CHAR(36) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
  category VARCHAR(60) NOT NULL CHECK (category IN ('TRANSPORT','STAY','FOOD','LOCAL_TRAVEL','ACTIVITY','FEES','EMERGENCY_RESERVE','OTHER')),
  title VARCHAR(255) NOT NULL,
  amount_minor BIGINT NOT NULL CHECK (amount_minor >= 0),
  currency CHAR(3) NOT NULL,
  basis VARCHAR(24) NOT NULL DEFAULT 'TOTAL' CHECK (basis IN ('TOTAL','PER_PERSON','PER_NIGHT','PER_UNIT')),
  price_status VARCHAR(28) NOT NULL DEFAULT 'ESTIMATED'
    CHECK (price_status IN ('ESTIMATED','PROVIDER_QUOTED','USER_ENTERED','PAID_VERIFIED')),
  source_ref TEXT,
  checked_at DATETIME(6),
  updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  CONSTRAINT fk_trip_budget_items_trip_id FOREIGN KEY (trip_id) REFERENCES trips(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE trip_offer_selections (
  id CHAR(36) CHARACTER SET ascii COLLATE ascii_bin PRIMARY KEY,
  trip_id CHAR(36) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
  provider_offer_id CHAR(36) CHARACTER SET ascii COLLATE ascii_bin,
  selection_kind VARCHAR(28) NOT NULL CHECK (selection_kind IN ('SUPPLIER_OFFER','OWN_VEHICLE','OWN_STAY','CUSTOM')),
  vertical VARCHAR(32) NOT NULL,
  title VARCHAR(255) NOT NULL,
  booking_state VARCHAR(32) NOT NULL DEFAULT 'PLANNED'
    CHECK (booking_state IN ('PLANNED','REDIRECTED_EXTERNAL','MANUAL_REFERENCE_UNVERIFIED','CONFIRMED','CANCELLED')),
  created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  CONSTRAINT fk_trip_offer_selections_trip_id FOREIGN KEY (trip_id) REFERENCES trips(id) ON DELETE CASCADE,
  CONSTRAINT fk_trip_offer_selections_provider_offer_id FOREIGN KEY (provider_offer_id) REFERENCES provider_offers(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE ai_plan_runs (
  id CHAR(36) CHARACTER SET ascii COLLATE ascii_bin PRIMARY KEY,
  trip_id CHAR(36) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
  requested_by CHAR(36) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
  run_scope VARCHAR(32) NOT NULL CHECK (run_scope IN ('WHOLE_TRIP','SINGLE_DAY')),
  model_ref VARCHAR(100),
  status VARCHAR(35) NOT NULL CHECK (status IN ('PENDING','SUCCEEDED','FAILED','VALIDATION_REJECTED')),
  request_context_hash VARCHAR(128) COLLATE utf8mb4_0900_bin,
  duration_ms INTEGER,
  error_code VARCHAR(80),
  created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  completed_at DATETIME(6),
  CONSTRAINT fk_ai_plan_runs_trip_id FOREIGN KEY (trip_id) REFERENCES trips(id) ON DELETE CASCADE,
  CONSTRAINT fk_ai_plan_runs_requested_by FOREIGN KEY (requested_by) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE booking_intents (
  id CHAR(36) CHARACTER SET ascii COLLATE ascii_bin PRIMARY KEY,
  user_id CHAR(36) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
  trip_id CHAR(36) CHARACTER SET ascii COLLATE ascii_bin,
  provider_id CHAR(36) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
  offer_id CHAR(36) CHARACTER SET ascii COLLATE ascii_bin,
  idempotency_key VARCHAR(128) COLLATE utf8mb4_0900_bin NOT NULL,
  capability VARCHAR(40) NOT NULL CHECK (capability IN ('REDIRECT','LIVE_SEARCH_EXTERNAL_CHECKOUT','DIRECT_BOOKING')),
  status VARCHAR(35) NOT NULL CHECK (status IN ('DRAFT_INTENT','REDIRECTED_EXTERNAL','PENDING','PROCESSING','CONFIRMED','FAILED','UNKNOWN','CANCELLED')),
  quoted_amount_minor BIGINT CHECK (quoted_amount_minor >= 0),
  currency CHAR(3),
  quote_expires_at DATETIME(6),
  terms_accepted_at DATETIME(6),
  created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  UNIQUE(user_id,idempotency_key),
  CONSTRAINT fk_booking_intents_user_id FOREIGN KEY (user_id) REFERENCES users(id),
  CONSTRAINT fk_booking_intents_trip_id FOREIGN KEY (trip_id) REFERENCES trips(id) ON DELETE SET NULL,
  CONSTRAINT fk_booking_intents_provider_id FOREIGN KEY (provider_id) REFERENCES providers(id),
  CONSTRAINT fk_booking_intents_offer_id FOREIGN KEY (offer_id) REFERENCES provider_offers(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE bookings (
  id CHAR(36) CHARACTER SET ascii COLLATE ascii_bin PRIMARY KEY,
  booking_intent_id CHAR(36) CHARACTER SET ascii COLLATE ascii_bin NOT NULL UNIQUE,
  provider_id CHAR(36) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
  user_id CHAR(36) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
  trip_id CHAR(36) CHARACTER SET ascii COLLATE ascii_bin,
  provider_booking_ref VARCHAR(180) COLLATE utf8mb4_0900_bin,
  status VARCHAR(35) NOT NULL CHECK (status IN ('PENDING','PROCESSING','CONFIRMED','FAILED','CANCELLED','UNKNOWN','COMPLETED')),
  confirmed_at DATETIME(6),
  provider_evidence_ref TEXT,
  created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  CHECK (status <> 'CONFIRMED' OR (provider_booking_ref IS NOT NULL AND provider_evidence_ref IS NOT NULL)),
  CONSTRAINT fk_bookings_booking_intent_id FOREIGN KEY (booking_intent_id) REFERENCES booking_intents(id),
  CONSTRAINT fk_bookings_provider_id FOREIGN KEY (provider_id) REFERENCES providers(id),
  CONSTRAINT fk_bookings_user_id FOREIGN KEY (user_id) REFERENCES users(id),
  CONSTRAINT fk_bookings_trip_id FOREIGN KEY (trip_id) REFERENCES trips(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE booking_events (
  id CHAR(36) CHARACTER SET ascii COLLATE ascii_bin PRIMARY KEY,
  booking_id CHAR(36) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
  provider_event_ref VARCHAR(191) COLLATE utf8mb4_0900_bin,
  event_type VARCHAR(80) NOT NULL,
  source VARCHAR(80) NOT NULL,
  observed_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  safe_summary JSON NOT NULL DEFAULT (JSON_OBJECT()),
  CONSTRAINT fk_booking_events_booking_id FOREIGN KEY (booking_id) REFERENCES bookings(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE payment_attempts (
  id CHAR(36) CHARACTER SET ascii COLLATE ascii_bin PRIMARY KEY,
  booking_intent_id CHAR(36) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
  user_id CHAR(36) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
  gateway_code VARCHAR(60) NOT NULL,
  idempotency_key VARCHAR(128) COLLATE utf8mb4_0900_bin NOT NULL,
  gateway_payment_ref VARCHAR(180) COLLATE utf8mb4_0900_bin,
  amount_minor BIGINT NOT NULL CHECK (amount_minor >= 0),
  currency CHAR(3) NOT NULL,
  status VARCHAR(35) NOT NULL CHECK (status IN ('NOT_STARTED','INITIATED','PENDING','SUCCEEDED','FAILED','UNKNOWN')),
  verified_at DATETIME(6),
  created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  UNIQUE(user_id,idempotency_key),
  CONSTRAINT fk_payment_attempts_booking_intent_id FOREIGN KEY (booking_intent_id) REFERENCES booking_intents(id),
  CONSTRAINT fk_payment_attempts_user_id FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE webhook_receipts (
  id CHAR(36) CHARACTER SET ascii COLLATE ascii_bin PRIMARY KEY,
  source_code VARCHAR(60) NOT NULL,
  external_event_id VARCHAR(180) COLLATE utf8mb4_0900_bin NOT NULL,
  payload_hash VARCHAR(128) COLLATE utf8mb4_0900_bin NOT NULL,
  signature_valid TINYINT(1) NOT NULL DEFAULT FALSE,
  processing_status VARCHAR(24) NOT NULL DEFAULT 'RECEIVED'
    CHECK (processing_status IN ('RECEIVED','PROCESSED','FAILED','IGNORED')),
  received_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  processed_at DATETIME(6),
  UNIQUE(source_code,external_event_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE notifications (
  id CHAR(36) CHARACTER SET ascii COLLATE ascii_bin PRIMARY KEY,
  user_id CHAR(36) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
  trip_id CHAR(36) CHARACTER SET ascii COLLATE ascii_bin,
  category VARCHAR(60) NOT NULL,
  title VARCHAR(255) NOT NULL,
  body TEXT NOT NULL,
  channel VARCHAR(32) NOT NULL DEFAULT 'IN_APP',
  delivery_status VARCHAR(30) NOT NULL DEFAULT 'PENDING'
    CHECK (delivery_status IN ('PENDING','SENT','DELIVERED','FAILED','NOT_APPLICABLE')),
  read_at DATETIME(6),
  created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  CONSTRAINT fk_notifications_user_id FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  CONSTRAINT fk_notifications_trip_id FOREIGN KEY (trip_id) REFERENCES trips(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE support_tickets (
  id CHAR(36) CHARACTER SET ascii COLLATE ascii_bin PRIMARY KEY,
  user_id CHAR(36) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
  trip_id CHAR(36) CHARACTER SET ascii COLLATE ascii_bin,
  booking_id CHAR(36) CHARACTER SET ascii COLLATE ascii_bin,
  assigned_staff_user_id CHAR(36) CHARACTER SET ascii COLLATE ascii_bin,
  category VARCHAR(60) NOT NULL,
  subject VARCHAR(200) NOT NULL,
  description TEXT NOT NULL,
  status VARCHAR(35) NOT NULL DEFAULT 'OPEN'
    CHECK (status IN ('OPEN','IN_REVIEW','WAITING_FOR_USER','RESOLVED','CLOSED')),
  created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  CONSTRAINT fk_support_tickets_user_id FOREIGN KEY (user_id) REFERENCES users(id),
  CONSTRAINT fk_support_tickets_trip_id FOREIGN KEY (trip_id) REFERENCES trips(id) ON DELETE SET NULL,
  CONSTRAINT fk_support_tickets_booking_id FOREIGN KEY (booking_id) REFERENCES bookings(id) ON DELETE SET NULL,
  CONSTRAINT fk_support_tickets_assigned_staff_user_id FOREIGN KEY (assigned_staff_user_id) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE safety_resources (
  id CHAR(36) CHARACTER SET ascii COLLATE ascii_bin PRIMARY KEY,
  country_code CHAR(2) NOT NULL,
  region_label VARCHAR(191),
  resource_type VARCHAR(64) NOT NULL,
  display_name VARCHAR(150) NOT NULL,
  phone_number VARCHAR(25),
  official_url TEXT,
  source_name VARCHAR(180) NOT NULL,
  verified_at DATETIME(6),
  active TINYINT(1) NOT NULL DEFAULT TRUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE user_consents (
  id CHAR(36) CHARACTER SET ascii COLLATE ascii_bin PRIMARY KEY,
  user_id CHAR(36) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
  consent_type VARCHAR(80) NOT NULL,
  document_version VARCHAR(64) NOT NULL,
  purpose VARCHAR(200),
  granted_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  withdrawn_at DATETIME(6),
  client_context JSON NOT NULL DEFAULT (JSON_OBJECT()),
  CONSTRAINT fk_user_consents_user_id FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE audit_logs (
  id CHAR(36) CHARACTER SET ascii COLLATE ascii_bin PRIMARY KEY,
  actor_user_id CHAR(36) CHARACTER SET ascii COLLATE ascii_bin,
  action VARCHAR(100) NOT NULL,
  object_type VARCHAR(80) NOT NULL,
  object_id CHAR(36) CHARACTER SET ascii COLLATE ascii_bin,
  request_id VARCHAR(120) COLLATE utf8mb4_0900_bin,
  safe_metadata JSON NOT NULL DEFAULT (JSON_OBJECT()),
  created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  CONSTRAINT fk_audit_logs_actor_user_id FOREIGN KEY (actor_user_id) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE INDEX saved_travellers_owner_idx ON saved_travellers(owner_user_id);

CREATE INDEX emergency_contacts_owner_idx ON emergency_contacts(owner_user_id);

CREATE INDEX places_destination_cat_idx ON places(destination_id,category);

CREATE INDEX provider_offers_provider_checked_idx ON provider_offers(provider_id,checked_at DESC);

CREATE INDEX trips_owner_updated_idx ON trips(owner_user_id,updated_at DESC);

CREATE INDEX trips_status_start_idx ON trips(status,start_date);

CREATE INDEX itinerary_items_day_idx ON itinerary_items(itinerary_day_id,item_order);

CREATE INDEX trip_budget_items_trip_idx ON trip_budget_items(trip_id);

CREATE INDEX trip_offer_selections_trip_idx ON trip_offer_selections(trip_id);

CREATE INDEX ai_plan_runs_trip_created_idx ON ai_plan_runs(trip_id,created_at DESC);

CREATE INDEX booking_intents_trip_idx ON booking_intents(trip_id);

-- UNIQUE indexes in MySQL permit multiple NULL values; no partial WHERE required.
CREATE UNIQUE INDEX bookings_supplier_reference_uq ON bookings(provider_id,provider_booking_ref);

CREATE INDEX bookings_user_created_idx ON bookings(user_id,created_at DESC);

CREATE INDEX booking_events_booking_idx ON booking_events(booking_id,observed_at DESC);

CREATE UNIQUE INDEX payment_gateway_ref_uq ON payment_attempts(gateway_code,gateway_payment_ref);

CREATE INDEX notifications_user_created_idx ON notifications(user_id,created_at DESC);

CREATE INDEX support_tickets_owner_status_idx ON support_tickets(user_id,status);

CREATE INDEX user_consents_type_idx ON user_consents(user_id,consent_type,granted_at DESC);

CREATE INDEX audit_logs_object_idx ON audit_logs(object_type,object_id,created_at DESC);
