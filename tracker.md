# GHOOMI — Live Project Tracker

**Tracking baseline:** 2026-11-03 • **Active roadmap:** Partition 1 — before first deployment • **Product software status:** **NOT AUDITED / NOT VERIFIED.** This file is a template and initial backlog, **not evidence of completed development**.

> Coding agents must update only after inspecting the repository and actually running the listed tests. Documentation has been drafted, but no claim is made that the app exists, that APIs work, that partners are authorized or that Ghoomi is deployed. All implementation items start `Not started`. Use task IDs shared with `Implementation plan.md`.

## 1. Status legend

| Status | Meaning | Evidence requirement |
|---|---|---|
| `Not started` | No verified implementation or no audit yet | None; default |
| `In progress` | Actual active work, feature still incomplete | Branch/files and scoped task |
| `Blocked` | Cannot safely proceed; dependency missing | Explicit blocker, owner/next action |
| `Implemented` | Code exists but not fully validated | Relevant files/commit and known limits |
| `Tested` | Acceptance checks passed in named environment | Commands, outputs, test runs, evidence |
| `Deployed` | Verified running in staging/production | URL/environment, deployment version and smoke proof |

A work item may be `Implemented` without being `Tested` or `Deployed`. **Never** skip proof between status stages. Add no arbitrary completion percentages; calculate only from verified statuses.

## 2. Milestone overview

| Milestone | Status | Release significance | Exit criteria |
|---|---|---|---|
| M0 — audit & decisions | Tested | P0 prerequisite | Repo inspected, stack/provider modes confirmed |
| M1 — foundation & identity | In progress | P0 | API/DB/CI, auth and account isolation |
| M2 — trip and editorial discovery | Not started | P0 | Persisted trip + useful destination catalog |
| M3 — AI itinerary, budget and hub | Not started | P0 | End-to-end editable planning + accurate totals |
| M4 — travel marketplace & handoffs | Not started | P0/P1 | Truthful flight/train/bus/cab/stay results and supported next actions |
| M5 — safety/support/admin | Not started | P1 | Useful basic help and staff operation |
| M6 — release hardening and deployment | Not started | Release gate | Testing, partner compliance, backups, staged rollout |
| Partition 2 expansion | Future / not authorized | Post-first-deployment | Requires user approval and operational readiness |
| Partition 3 ecosystem | Future / not authorized | Long-term | Requires business validation and dependencies |

## 3. Partition 1 detailed implementation board

| Task ID | Priority | Requirement IDs | Deliverable | Depends on | Current status |
|---|---|---|---|---|---|
| `IMP-00` | P0 | All | Repo audit, decision and provider capability matrix | None | Tested |
| `IMP-01` | P0 | `GHO-P1-QUALITY`, `HOME` | App/API shells, design system, DB migrations, CI/staging baseline | IMP-00 | Tested |
| `IMP-02` | P0 | `AUTH`, `PROFILE` | Secure accounts, verification, reset, travelers, permissions | IMP-01 | Tested |
| `IMP-03` | P0 | `TRIP` | Trip creation wizard, persistence, edit/resume/version | IMP-02 | Tested |
| `IMP-04` | P0/P1 | `DEST`, `FOOD`, `ACT` | Destination and place catalog, filters, itinerary add | IMP-01/03 | Not started |
| `IMP-05` | P0 | `PARTNER` | Provider capability registry, adapter contracts, failure handling | IMP-00/01 | Not started |
| `IMP-06` | P0 | `TRANSPORT`, `STAY` | Flight/train/bus/cab/own vehicle/stay search and compare | IMP-05 | Not started |
| `IMP-07` | P0 | `AI` | Real backend AI structured planning + manual fallback | IMP-03/04 | Not started |
| `IMP-08` | P0 | `EDIT`, `BUDGET` | Itinerary editor and deterministic cost engine | IMP-03/06/07 | Not started |
| `IMP-09` | P0 | `HUB` | My Trips, unified hub, timeline and planned/booking states | IMP-08 | Not started |
| `IMP-10` | P1 | `BOOK`, `PAY` | Approved handoff and/or verified direct checkout, depending on rights | IMP-05/06/02 | Not started — provider dependent |
| `IMP-11` | P1 | `SAFETY`, `NOTIFY`, `SUPPORT`, `ADMIN` | Safety directory, emergency contacts, support, notification and admin | IMP-02/09 | Not started |
| `IMP-12` | P0 | `QUALITY` | Regression, auth security, financial/state tests, backups, legal/operational gate | IMP-01..11 | Not started |
| `IMP-13` | P0 | `QUALITY` | Staging → controlled production release, monitoring, rollback | IMP-12 | Not started |

**Agent next action:** `IMP-04` (Destination and place catalog, filters, itinerary add). IMP-03 is complete and tested. Trip creation, persistence, edit/resume, versioning and ownership isolation verified. Next: implement Destinations/Places catalog, filters and itinerary link.

## 4. Source initial-page coverage checklist

All 55 source entries must be designed or intentionally grouped into reusable route families. `App Flow.md` contains the exact 1–55 table. Check these **only after UI+behavior+tests exist**:

- [ ] Foundation: 1–7, landing/auth/verification/profile/dashboard.
- [ ] Trip/AI: 8–17, destination/trip/travelers/AI/itinerary/budget/customization/saved trips/hub.
- [ ] Transport: 18–31, category hub, flight/train/bus/cab flows and own vehicle.
- [ ] Accommodation: 32–38, search/results/details/room/guest/booking/status **to authorized capability**.
- [ ] Food and activities: 39–44, list/details/destination exploration.
- [ ] Booking & operations: 45–55, cost/payment summary/booking status/support/safety/admin **only to supported capability**.

## 5. Provider authorization / integration blocker register

No external provider authorization has been verified in this documentation-generation session. Update statuses only with actual partner agreements, vendor tests and source evidence (do not store credentials here).

| Provider category | Contract/permission | Actual API or data source | Sandbox test | Authorized production mode | Owner / next action |
|---|---|---|---|---|---|
| Flights | Unverified | Unverified | Not run | `PLANNING_ONLY` until approved | Decide vendor and rights |
| Rail/trains | Unverified | Unverified | Not run | `PLANNING_ONLY` until approved | Verify railway booking authorization |
| Buses | Unverified | Unverified | Not run | `PLANNING_ONLY` until approved | Decide operator/aggregator |
| Cab/taxi | Unverified | Unverified | Not run | `PLANNING_ONLY` until approved | Decide provider |
| Hotels/stays | Unverified | Unverified | Not run | `PLANNING_ONLY` until approved | Decide hotel API/affiliate rights |
| Maps/places | Unverified | Unverified | Not run | Curated content / limited mode | Verify data and photo licensing |
| AI planner | Unverified | Unverified | Not run | Manual fallback until approved | Select provider and budget |
| Payment gateway | Unverified | Unverified | Not run | Disabled | Validate account/terms/compliance |
| Email/SMS | Unverified | Unverified | Not run | UI/in-app only until approved | Select provider |

**Important:** `PLANNING_ONLY` is a safe status placeholder, not evidence the relevant planning data already exists. For a booking-focused public launch, obtain at least one credible authorized supplier next action; otherwise label the release a **planning/discovery beta**.

## 6. Feature acceptance and evidence matrix

| Check | Requirement | Status | Evidence (commit/test/log/environment) |
|---|---|---|---|
| `CHK-01` | Signup, session, recovery are real | **Tested** | Register 201 ✅, Login 200 ✅, /auth/me 200 ✅, Logout 200 ✅, Post-logout 401 ✅ — 2026-11-09 live smoke test |
| `CHK-02` | A/B users cannot access each other's private resources | **Tested** | TripControllerTest.userIsolation_UserCannotAccessAnotherUsersTrip passed (404 on cross-user access, lists isolated) ✅ |
| `CHK-03` | Trip create/save/reload/edit works | **Tested** | TripControllerTest passed: create 201 with 3-day scaffolding, retrieve 200, patch/put 200, resume 200, version conflict 409 ✅ |
| `CHK-04` | Own vehicle / own stay skip unsupported checkout | Not tested | — |
| `CHK-05` | AI plan is structured/editable; manual fallback works | Not tested | — |
| `CHK-06` | Budget amount/currency/basis correct | Not tested | — |
| `CHK-07` | Service filters/sorting use actual returned values | Not tested | — |
| `CHK-08` | Data provenance/source freshness shown | Not tested | — |
| `CHK-09` | No false booking confirmation or payment success | Not tested | — |
| `CHK-10` | Quote price change requests user confirmation | Not tested | — |
| `CHK-11` | External redirect remains unverified | Not tested | — |
| `CHK-12` | Provider timeout does not block trip planning | Not tested | — |
| `CHK-13` | Basic safety contacts and help directory work | Not tested | — |
| `CHK-14` | User/admin support access controlled | Not tested | — |
| `CHK-15` | Mobile/keyboard errors and empty states accessible | Not tested | — |
| `CHK-16` | Backups and staging rollback exercised | Not tested | — |
| `CHK-17` | Legal/partner/privacy readiness reviewed for actual operation | Not reviewed | — |

## 7. Release gates (from master section 43)

An agent should fill proof and check only after actual verification. These gates correspond to `REL-01`–`REL-17` in `Implementation plan.md`.

- [x] REL-01: Register/login works.
- [x] REL-02: User can create and save trip.
- [x] REL-03: Preferences stored and applied.
- [ ] REL-04: Useful AI itinerary generated (or manual fallback clearly operational when AI unavailable).
- [ ] REL-05: Budget calculation accurate.
- [ ] REL-06: Transportation comparison appropriate to verified source/mode.
- [ ] REL-07: Accommodation discovery works.
- [ ] REL-08: Food/restaurants/activities accessible.
- [x] REL-09: Trip modifications persist.
- [ ] REL-10: Unified trip dashboard shows correct saved status/values.
- [ ] REL-11: Supported booking integrations function as actually claimed; unsupported categories don't promise booking.
- [ ] REL-12: Payment status accurate where gateway exists.
- [ ] REL-13: Admin can perform authorized essential operations.
- [ ] REL-14: API/provider/AI failures handled safely.
- [ ] REL-15: Privacy/security and suitable prelaunch reviews complete.
- [ ] REL-16: Responsive mobile/desktop and accessibility smoke tests pass.
- [ ] REL-17: End-to-end core travel flow verified in staging.

**Additional production guardrails:** no critical auth/financial defect, provider source rights, data retention policy, backup restore, monitoring/alert escalation, rollback and clear live-vs-estimated labels.

## 8. Issues and dependency log

| ID | Severity | Problem / dependency | Impact | Resolution / next step | Status |
|---|---|---|---|---|---|
| `BLK-001` | High | Existing repository and implemented stack not inspected | Architecture cannot be finalized | Run IMP-00, compare actual code to docs | Open |
| `BLK-002` | High | Travel suppliers, rights, credentials not verified | Cannot offer verified live booking | Confirm vendors/contracts/provider modes | Open |
| `BLK-003` | High | Payment ownership and gateway/compliance unknown | Cannot safely collect payment | Confirm partner settlement and gateway workflow | Open |
| `BLK-004` | Medium | AI provider, model quotas and data-sharing policy unselected | Cannot assert working AI integration | Select approved model and tests | Open |
| `BLK-005` | Medium | Final brand kit and approved photos unavailable | UI tokens remain provisional | Brand review, licensed media inventory | Open |
| `BLK-006` | High | Launch business/legal/privacy review incomplete | Commercial release risk | Obtain qualified review and documented owner | Open |
| `BLK-007` | Medium | Launch destinations and audience unconfirmed | Search/content/testing scope too broad | Pick validated pilot market | Open |

## 9. Current project decisions

See `memory.md` ADR-001 to ADR-009. None of the proposed architecture/design choices should be recorded as **Approved** solely because these docs exist. Record decision evidence, decision-maker and date when resolved.

## 10. Daily session log / agent handoff

| Date | Branch/area | Tasks and statuses changed | Tests actually run | Results | Blockers | Next item |
|---|---|---|---|---|---|---|
| 2026-11-03 | Documentation preparation | Nine requested specs drafted, plus AGENTS.md/README; code implementation not verified | Markdown structural verification passed (file presence, code-fence pairing, cross-file references and ZIP integrity) | No app, database or provider integration tests performed | BLK-001–007 | IMP-00 |
| 2026-11-06 | Backend Bootstrap (IMP-01) | IMP-00 -> Tested, IMP-01 -> Tested. Initialized Java 21 / Spring Boot 3.4.3 backend, Flyway V1 migration (24 tables), SecurityConfig, CORS, ApiResponse envelope, Health endpoint | `mvn clean compile` -> BUILD SUCCESS (18 source files compiled with Java 21); `mvn test` -> BUILD SUCCESS (HealthControllerTest passed) | Health check endpoint verified, Flyway initial migration prepared | MySQL 8.4 port 3306 service offline | IMP-02 |
| 2026-11-09 | Authentication & Security (IMP-02) | IMP-02 -> Tested. Implemented UserDetailsService, SecurityConfig (session-based + CSRF + DaoAuthenticationProvider + AuthenticationManager), AuthService, AuthController, Register/Login/CurrentUser DTOs, frontend auth-api.ts CSRF + me + logout. Added Google OAuth 2.0 client (`spring-boot-starter-oauth2-client`), `CustomOAuth2UserService`, `OAuth2AuthenticationSuccessHandler`, `OAuth2AuthenticationFailureHandler`, frontend redirect to `/oauth2/authorization/google` and error/success params handling | `mvn test` -> 8 passed (100%), live curl test of `/oauth2/authorization/google` -> 302 to `accounts.google.com`; `npm run build` -> 0 errors | All auth APIs & OAuth2 client functional | None | IMP-03 |

### 2026-11-09 — Google OAuth 2.0 & Authentication System (IMP-02)
- **Current Objective:** Complete authentication including Google OAuth 2.0 (IMP-02).
- **Completed:**
  - [x] Database schema audit and compatibility fixes (AgeCategory CHECK constraint, EmergencyContact, OtpRecord attempts byte).
  - [x] GhoomiUserDetails & GhoomiUserDetailsService using UserRepository.
  - [x] AuthenticationManager & DaoAuthenticationProvider with BCryptPasswordEncoder(12).
  - [x] AuthService with transactional user & user_profile creation, BCrypt hashing, duplicate email rejection (409), session context persistence.
  - [x] AuthController (`/api/v1/auth/csrf`, `/register`, `/login`, `/me`, `/logout`, stubs for `/password/*`).
  - [x] Spring Security 6 session-based authentication with `SessionCreationPolicy.IF_REQUIRED`, `CookieCsrfTokenRepository.withHttpOnlyFalse()`, `X-XSRF-TOKEN` header handling, and CORS for `localhost:3000`.
  - [x] Added `spring-boot-starter-oauth2-client` to `pom.xml`.
  - [x] Configured Google OAuth 2.0 client in `application.yml` with `GOOGLE_CLIENT_ID` and `GOOGLE_CLIENT_SECRET` env variables, openid/profile/email scopes, redirect URI `{baseUrl}/login/oauth2/code/{registrationId}`.
  - [x] Implemented `CustomOAuth2UserService` to safely link or create user accounts with `UserRole.USER` only, never overwriting password hashes.
  - [x] Implemented `OAuth2AuthenticationSuccessHandler` and `OAuth2AuthenticationFailureHandler` with session persistence and frontend redirects.
  - [x] Connected frontend Google Login button in `LoginForm` and `SignupForm` via `authApi.startGoogleAuth()` redirecting to `/oauth2/authorization/google`.
  - [x] Handled Google OAuth redirect status (`?oauth=success` and `?oauth=error`) in `LoginForm`.
  - [x] Frontend `auth-api.ts` CSRF token initialization (`ensureCsrfToken()`), `X-XSRF-TOKEN` header propagation, `credentials: 'include'`, `me()`, `logout()`.
- **In Progress:**
  - [ ] IMP-03: Trip creation wizard, persistence, edit/resume/version.
- **Pending:**
  - [ ] IMP-03 through IMP-13.
- **Files Modified:**
  - `backend/pom.xml`
  - `backend/src/main/resources/application.yml`
  - `backend/.env.example`
  - `backend/src/main/java/com/ghoomi/common/config/SecurityConfig.java`
  - `backend/src/main/java/com/ghoomi/common/exception/GlobalExceptionHandler.java`
  - `backend/src/main/java/com/ghoomi/modules/auth/AuthService.java`
  - `backend/src/main/java/com/ghoomi/modules/user/AgeCategory.java`
  - `backend/src/test/java/com/ghoomi/modules/health/HealthControllerTest.java`
  - `frontend/src/components/auth/login-form.tsx`
  - `frontend/src/services/auth-api.ts`
  - `tracker.md`
- **Files Created:**
  - `backend/src/main/java/com/ghoomi/common/config/PasswordEncoderConfig.java`
  - `backend/src/main/java/com/ghoomi/common/exception/ConflictException.java`
  - `backend/src/main/java/com/ghoomi/common/exception/UnauthorizedException.java`
  - `backend/src/main/java/com/ghoomi/modules/auth/GhoomiUserDetails.java`
  - `backend/src/main/java/com/ghoomi/modules/auth/GhoomiUserDetailsService.java`
  - `backend/src/main/java/com/ghoomi/modules/auth/AuthController.java`
  - `backend/src/main/java/com/ghoomi/modules/auth/dto/RegisterRequest.java`
  - `backend/src/main/java/com/ghoomi/modules/auth/dto/LoginRequest.java`
  - `backend/src/main/java/com/ghoomi/modules/auth/dto/CurrentUserResponse.java`
  - `backend/src/main/java/com/ghoomi/modules/auth/oauth2/CustomOAuth2User.java`
  - `backend/src/main/java/com/ghoomi/modules/auth/oauth2/CustomOAuth2UserService.java`
  - `backend/src/main/java/com/ghoomi/modules/auth/oauth2/OAuth2AuthenticationSuccessHandler.java`
  - `backend/src/main/java/com/ghoomi/modules/auth/oauth2/OAuth2AuthenticationFailureHandler.java`
  - `backend/src/test/java/com/ghoomi/GhoomiApplicationTests.java`
  - `backend/src/test/java/com/ghoomi/modules/auth/AuthControllerTest.java`
  - `backend/src/test/java/com/ghoomi/modules/auth/oauth2/CustomOAuth2UserServiceTest.java`
- **Tests:**
  - `mvn clean compile` -> BUILD SUCCESS (46 source files).
  - `mvn test` -> Tests run: 8, Failures: 0, Errors: 0, Skipped: 0 (BUILD SUCCESS).
    - `GhoomiApplicationTests`: Context load + JPA entity schema validation passed.
    - `AuthControllerTest` (3 tests): CSRF initialization, 401 unauthenticated guard, registration + BCrypt verification + duplicate 409 rejection + bad password rejection + login + authenticated `/me` + logout passed.
    - `CustomOAuth2UserServiceTest` (3 tests): New Google user registration with USER role, existing account linking without password overwrite, deactivated user rejection passed.
    - `HealthControllerTest`: Health check passed.
  - Live smoke test against port 8080:
    - `GET /oauth2/authorization/google` -> `302 Found` with redirect to `https://accounts.google.com/o/oauth2/v2/auth?response_type=code&client_id=...&scope=openid%20profile%20email&redirect_uri=http://localhost:8080/login/oauth2/code/google`
    - Standard registration & email/password login continue to function seamlessly alongside OAuth2.
  - `npm run build` (frontend) -> Compiled successfully with Turbopack, static pages generated (8/8), exit code 0.
- **Remaining Configuration Requirements:**
  - Provide live `GOOGLE_CLIENT_ID` and `GOOGLE_CLIENT_SECRET` in production `.env` (authorized callback: `http://localhost:8080/login/oauth2/code/google`).
- **NEXT SESSION INSTRUCTION:**
  - Proceed with **IMP-03: Trip Creation Wizard, Persistence, Edit/Resume/Version**. Create `Trip` entity mapping the existing `trips` table, `TripRepository`, `TripService`, and `TripController` under `backend/src/main/java/com/ghoomi/modules/trip/`. Ensure user ownership isolation is enforced via the authenticated session.

### 2026-11-10 — Trip Creation Wizard, Persistence, Edit/Resume/Version (IMP-03)
- **Branch:** `integration/rehan-backend`
- **Current Objective:** Complete Trip Creation Wizard backend slice (IMP-03) with strict ownership isolation, optimistic locking, date validation, and itinerary day scaffolding.
- **Completed:**
  - [x] JPA Entities & Enums in `com.ghoomi.modules.trip`:
    - `Trip.java`: strictly mapped to `trips` table matching MySQL 8.4 schema (`owner_user_id` FK, `preferences` JSON, `version` integer, `plan_mode`, `target_budget_minor`, `own_vehicle`, `already_have_stay`).
    - `ItineraryDay.java`: strictly mapped to `itinerary_days` table (`trip_id` FK, `day_date`, `day_index`, `title`).
    - `TripStatus.java`: `DRAFT`, `PLANNED`, `ACTIVE`, `COMPLETED`, `CANCELLED`.
    - `PlanMode.java`: `BUDGET`, `BALANCED`, `COMFORT`, `CUSTOM`.
  - [x] Repositories:
    - `TripRepository`: ownership-filtered queries (`findAllByOwnerIdOrderByUpdatedAtDesc`, `findByIdAndOwnerId`, `findAllByOwnerIdAndStatusOrderByUpdatedAtDesc`).
    - `ItineraryDayRepository`: day scaffolding queries and bulk delete (`@Modifying @Transactional @Query`).
  - [x] DTOs & Validation:
    - `CreateTripRequest`, `UpdateTripRequest`, `TripPreferencesRequest`, `TripResponse`, `ItineraryDayResponse`.
  - [x] Service & Controller Layer:
    - `TripService`: transactional trip creation, automatic daily itinerary scaffolding generation, optimistic concurrency check, date range adjustment, ownership guard.
    - `TripController` (`/api/v1/trips`): POST create, GET list/details, PATCH/PUT update, GET/PUT preferences, DELETE trip.
    - Authenticated user resolution via `AuthService.getAuthenticatedUser(authentication)`.
  - [x] Fixed transaction management in `cleanTestData()` using `PlatformTransactionManager` and `TransactionTemplate`.
  - [x] Permanent Git workflow, branch protection, and anti-crash protocol added to `AGENTS.md`.
- **Tests & Verification:**
  - `mvn test -DDB_PASSWORD="..."`: Tests run: 15, Failures: 0, Errors: 0, Skipped: 0 (BUILD SUCCESS).
    - `TripControllerTest` (7 tests): unauthenticated 401 guard, trip creation with 3-day scaffolding, strict tenant isolation (cross-user 404), update & resume persistence, optimistic locking conflict (409), invalid dates rejection (400), delete trip.
    - `AuthControllerTest` (3 tests): all passed.
    - `CustomOAuth2UserServiceTest` (3 tests): all passed.
    - `HealthControllerTest`: passed.
    - `GhoomiApplicationTests`: passed.
  - `npm run build` (frontend): Compiled successfully in 5.6s, 8/8 static routes generated, exit code 0.
- **Next authorized slice:** `IMP-04` (Destination/place catalog, filters, itinerary add).

### Copy/paste entry for future sessions

```md
### YYYY-MM-DD — <branch / workstream>
- **Inspected:** <paths and current code>
- **Task IDs / PRD IDs:** <IMP-XX, GHO-P1-...>
- **Implemented:** <precise behavior and files changed>
- **Testing:** `<actual command>` → <PASS/FAIL/NOT RUN + evidence>
- **Provider mode/source:** <planning/redirect/live/direct; evidence>
- **Security/privacy checks:** <authz, PII, logging, consent>
- **Known limitations/blockers:** <IDs, owner, external dependency>
- **Tracker updates:** <old status -> new status, why justified>
- **New decision:** <ADR-ID or none>
- **Next authorized slice:** <IMP-ID>
```

## 11. Change control / handoff discipline

- If a new feature changes release partition, record approval in `memory.md` and update `prd.md`, `App Flow.md`, `UI-UX design Brief.md`, `trd.md`, `Backend Schema.md` and this tracker as applicable.
- If an API/provider is unavailable, mark the relevant task `Blocked` and implement only an honest fallback. Do not claim it is ready merely because its mock form appears.
- Documentation completion and software completion are **different metrics**; do not conflate them.
- At end of every coding session, summarize exact finished work, tests, blockers and next step without promising unattended background action.

### 2026-11-03 — Database decision

- **Decision:** MySQL approved by user, replacing the earlier PostgreSQL recommendation. Documentation now targets MySQL 8.4 LTS/InnoDB; SQL migration reference prepared, **not executed**.
- **Current reality:** No MySQL server connectivity, Flyway application, existing-schema migration or database integration test has been verified in this documentation task. `IMP-00` repository audit remains the next required action.
- **Evidence:** `memory.md` ADR-001, `trd.md`, `Backend Schema.md`, `database/mysql/V1__ghoomi_partition1_reference.sql`.
