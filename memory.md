# GHOOMI — Persistent project memory and decision log

**Role:** durable project-context handoff for coding agents across sessions, *not* a substitute for source control, deployment logs or user approval. **Created:** 2026-11-03. **Stage:** specification only; all software/partner integrations are unverified until inspected.

## 1. Identity and objective

- **Name:** Ghoomi.
- **Category:** smart travel planning, discovery, authorized booking and trip-management platform.
- **Brand statement:** “You Enjoy the Journey. Ghoomi Manages the Journey.”
- **Vision:** “From Home to Home.”
- **Initial positioning:** familiar MakeMyTrip-like transport/hotel/holiday search and comparison, plus AI-generated and editable itineraries, transparent budgets and a centralized trip dashboard **in version 1**.
- **Long-term objective:** help travelers coordinate an entire journey, not merely complete individual service bookings.

## 2. Scope is partitioned, not a collection of separate products

| Partition | When | Approved product direction |
|---|---|---|
| **1 — Initial release** | Before first public deployment | Working accounts, destination/trip/AI/budget/discovery/hub, basic booking pathways and payment status *only if authorized*, basic safety information, support/admin foundation |
| **2 — Operational expansion** | After deployment | Expanded bookings/payment/refund operations, collaboration, consent-based family tracking, live updates, documents, richer notification/support/partner modules |
| **3 — Ecosystem** | Long term | Intelligent recovery, Back-to-Home, advanced SOS, contextual AI, weather/events, expense split, offline, public trips, community, marketplace, premium |

**Never mistake a provider-dependent desired feature for an implemented integration.** `PLANNING_ONLY` is a legitimate V1 mode where live booking is not yet authorized, with accurate labeling and a real next action such as manual planning; do not market it as bookable.

## 3. Stable product decisions

- Users control plan changes, budget trade-offs and paid actions; AI recommends but does not book independently.
- Optional **Own Vehicle** and **Already Have Accommodation** paths avoid unwanted booking steps.
- Trip planner accepts origin, destination, dates, traveler count, budget, pace/comfort, modes, stay/food/activity preferences; output is day-wise, editable and persisted.
- Budget arithmetic is deterministic. AI may recommend savings but cannot authoritatively calculate payment totals or invent offers. Store monetary amounts as integer paise with ISO currency.
- Results and quotes must show data provenance, timestamp, status, taxes/fees, cancellation conditions where applicable and the actual next-action mode.
- Provider booking confirmation and payment-provider confirmation are distinct authoritative events. No false confirmed states.
- Safety starts with emergency-contact storage and relevant emergency-service information; continuous live location/family monitoring and automated SOS are **not** V1 requirements.
- Mobile-first responsive experience; accessible keyboard navigation, form errors and zero dead-end clickable controls.
- User security, role enforcement, privacy, backups and error handling exist from the first release.

## 4. Proposed engineering baseline — not in original source as a final selection

The 53-page master lists **React/Next.js**, **Java Spring Boot**, **MySQL/PostgreSQL**, **Spring Security/JWT**, official provider APIs, an AI API, maps and payment integrations as options. This pack proposes a concrete default for a **new, empty** repository: **Next.js (React + TypeScript + Tailwind) frontend; Java Spring Boot modular-monolith REST API; MySQL 8.4 LTS/InnoDB (user-selected); Flyway migrations; Spring Security server-managed HTTP-only sessions or carefully managed token auth; secure object storage if required.** Do not replatform an existing project without a specific approved migration decision. This is recorded in `ADR-001`: **the database is approved as MySQL**; the remaining stack choices are still proposed pending repository inspection.

### Database decision update — 2026-11-03

The user explicitly changed the database from PostgreSQL to **MySQL**. Treat this as a binding project requirement for new work. Use **MySQL 8.4 LTS + InnoDB** as the proposed server baseline (actual provider/version remains to be verified), `CHAR(36)` UUID strings generated application-side, UTC `DATETIME(6)`, MySQL `JSON`, `CHECK`, foreign keys, and versioned Flyway migrations. Do **not** claim migrations were executed or existing project data converted; audit the real repository and any existing database first.

## 5. Source hierarchy and interpretation

1. **Latest primary source:** `reference/Ghoomi_Working_Platform_Master.pdf`, 53 pages, *COMPLETE PRODUCT VISION, FUNCTIONAL REQUIREMENTS & THREE-PART DEVELOPMENT ROADMAP*. It defines 95 topical sections and 55 initial-release page/module entries; those entries need not all become separate URL routes.
2. **Translated implementation specs:** nine Markdown files at project root plus `AGENTS.md` and `README.md`. They add IDs, testable acceptance criteria, recommended architecture and safeguards; these additions are clearly **proposals** unless approved/implemented.
3. **Prior concept documents:** broader context for future Ghoomi ambition; do not silently pull long-term features into Partition 1.
4. **Reality:** inspected code, real partner permissions, signed agreements, running tests, observed provider responses and legal review override any unsupported implementation claim.

## 6. Decision register — edit after real decisions

| Decision | Status | Current position | To resolve |
|---|---|---|---|
| ADR-001 — stack | **Partially approved** | **MySQL 8.4 LTS selected by user**; Next.js TS / Spring Boot still proposed | Inspect repo; confirm remaining stack without replacing working code |
| ADR-002 — geography | **Proposed** | India-first user experience; INR primary | Confirm first cities and supported routes |
| ADR-003 — integration depth | **Open** | Per vertical choose approved provider mode | Confirm partners, APIs, contracts and keys |
| ADR-004 — payment ownership | **Open** | Provider checkout unless authorized in-platform rails | Legal/payment partner review |
| ADR-005 — authentication details | **Proposed** | Spring Security, secure HTTP-only session | Confirm app domains, login mechanism |
| ADR-006 — AI provider | **Open** | Backend-only structured response API | Vendor/quotas/pricing/data-retention review |
| ADR-007 — design tokens | **Proposed** | Deep navy, teal, warm accent, white surfaces | UX review or brand assets |
| ADR-008 — beta audience | **Open** | Controlled India-first pilot | Choose destinations, user cohort, support capacity |
| ADR-009 — cancellation responsibility | **Open** | Provider-specific; no fake refunds | Partner agreements and support workflow |

## 7. Unresolved inputs — do not invent

- Credentials, provider contracts and rail-ticket booking permissions.
- Real inventory, fares, hotel availability, cancellation terms, and partner commission percentages.
- Payment gateway account and applicable tax/fee structures.
- Brand logo, final colors, production domain, official support phone/email, admin operator identity.
- Final destination catalog and vendor licensing for photos/content.
- User retention/deletion policies, business registration, legal review and privacy notices appropriate to the actual launch model.

When blocked, show the relevant module with an accurate **not configured / unavailable / planning-only** state rather than a misleading mock success. Test data must live in tests/dev fixtures and never be silently exposed as verified inventory.

## 8. Session handoff template

Copy this section forward *after actual work*:

- **Date and branch:** not started.
- **What shipped / changed:** not started.
- **Tests run and actual output:** none.
- **Provider access/configuration:** unverified.
- **Schema migrations applied:** none.
- **New ADRs / approved scope changes:** none.
- **Open blockers:** see Section 7.
- **Next approved item:** begin with repository audit `IMP-00`; use MySQL for all new DB work and resolve remaining stack/partner-mode decisions.

## 9. Memory maintenance

Edit only when supported by approved user decisions or verified repository evidence. Distinguish **planned**, **in progress**, **implemented**, **tested**, **deployed** and **provider-authorized**; these states are not interchangeable. Never store real API keys, personal identity information, user location histories or financial account data here.
