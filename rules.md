# GHOOMI — Enforceable development rules

**Applies:** every agent change, feature, API, query and deployment. **Classification:** product/engineering guardrails; source-aligned safety principles plus implementation recommendations. If a request conflicts with a higher-priority safety/legal obligation, stop the conflicting action and explain the blocker.

## A. Scope and truthfulness

| Rule | Non-negotiable behavior |
|---|---|
| R-01 | Implement **Partition 1** first. Never build a Partition 2/3 feature unless explicitly requested and the user understands dependencies. |
| R-02 | A visible button must perform its described action or be clearly unavailable with a reason. No dead links or pretend checkout. |
| R-03 | No invented fares, available seats/rooms, weather alerts, opening hours, booking IDs, payment results or provider endorsements. |
| R-04 | Label each displayed offer: `verified live`, `provider-listed`, `indicative estimate`, or `unavailable`, together with source and last-checked time where known. Never infer “live” from cached data. |
| R-05 | Show capability mode: `PLANNING_ONLY`, `REDIRECT`, `LIVE_SEARCH_EXTERNAL_CHECKOUT`, `DIRECT_BOOKING`; disable unsupported actions per provider. |
| R-06 | AI output is advisory. Validate schema, dates, trip-length, activity scheduling and location assumptions server-side. Provide manual/deterministic fallback when AI is down. |
| R-07 | User approval is required before charges, paid upgrades, cancellations, policy-impacting changes or any automated rebooking. |
| R-08 | Do not state a reservation is `CONFIRMED` without authoritative provider evidence; payment `SUCCEEDED` requires verified gateway/provider evidence, not client navigation. |
| R-09 | Costs show original provider amount, verified/estimated taxes and fees, discounts where genuine, total currency and per-person-vs-total basis. |
| R-10 | Unknown data remains unknown. An incomplete vendor response is not permission to guess missing information. |

## B. Security and privacy

- **R-11: Auth and authorization.** Passwords hashed with a modern adaptive password hash; secure session lifecycle; every privileged resource endpoint enforces object-level authorization *server-side*. Access by guessing UUID is prohibited.
- **R-12: Minimize PII.** Only collect booking details as required by a real service; identity documents and payment card data must not be stored by default. Use processor tokenization where available.
- **R-13: Consent.** Any future location sharing requires explicit, revocable, scoped consent, recipient identity, purpose, expiry and timestamps; location sharing off by default.
- **R-14: Safety.** V1 has emergency contacts and trusted official information; never display “Help dispatched” without a verified responder relationship. No guaranteed real-time tracking in a normal browser.
- **R-15: Secrets.** Private keys, provider tokens, webhook signing secrets and AI keys stay on backend, never in public web bundles or Markdown. `.env.example` can contain variable names only.
- **R-16: App security.** Validate request payloads; prevent SQL injection, XSS and CSRF as applicable; rate-limit auth, AI and financial operations; restrict CORS; audit high-risk actions; scrub PII/secrets from logs.
- **R-17: Data lifecycle.** Create documented deletion, retention, backup and restoration procedures; verify appropriate India privacy/consumer/travel/payment legal duties before commercial operation.
- **R-18: Content provenance.** Do not scrape or reuse third-party logos, photos, pricing or inventory contrary to licensing/terms; disclose sponsored offers and affiliate relationships.

## C. Provider and payment reliability

- **R-19:** Each provider uses an adapter with a capability matrix, timeout, bounded retry/backoff, rate handling, normalization, observable errors and kill switch.
- **R-20:** Before eligible checkout, request reprice/reavailability and require acceptance if terms or amounts change. Do not turn stale estimates into instant checkout.
- **R-21:** Payment and booking state machines are independent. Webhooks require signature/authenticity verification where available, replay detection and idempotent processing.
- **R-22:** Retry financial operations only when idempotent and safe. Store idempotency keys and reconcile unknown timeouts; never double-charge or double-book.
- **R-23:** Redirect checkout must disclose that the provider processes the reservation, and Ghoomi cannot claim success without proof or a verified integration path.
- **R-24:** On supplier outage, surface clear status and permit unrelated trip planning, manual alternatives and saved itineraries.

## D. Engineering delivery

- **R-25:** Inspect actual code and constraints first; do not rebuild, wipe DB, rewrite dependencies or change repo config destructively without an approved reason.
- **R-26:** Implement end-to-end feature slices with typed contracts, input validation, persistence, authorization, UI states, tests and telemetry; avoid wide shallow mockups.
- **R-27:** **MySQL is the user-selected database.** Use MySQL 8.4 LTS/InnoDB compatible DDL and reversible/versioned Flyway migrations; add explicit indexes/constraints and keep seed data tagged non-production. Do not add PostgreSQL-only syntax, silently switch databases or introduce schema drift.
- **R-28:** A feature is `Done` only with evidence in `tracker.md`: exact test commands, actual results, required acceptance checks and no open critical defect.
- **R-29:** Accessibility is a release requirement: visible focus, semantic controls, keyboard usage, form error announcements, reduced motion, sufficient contrast.
- **R-30:** Maintain docs and traceability: mention `GHO-...` product requirements, API contracts and `IMP-...` plan task IDs in work summaries/PRs.
- **R-31:** Protect production data. Use separate development, staging, production; never run destructive migrations or tests against production without explicit authorization.
- **R-32:** Secure CI and automated tests; run backend validation, UI build/types/lint, DB migration checks, auth negative tests, financial failure cases (if applicable), and at least one complete trip path.
- **R-33:** Roll out behind feature flags for provider-sensitive modules; log incidents and rollback procedures before public launch.
- **R-34:** Use feature IDs and stable interfaces; prefer a modular monolith until measured needs justify microservices.

## E. Agent operating protocol

1. Read `AGENTS.md` → `rules.md` → `memory.md` → `tracker.md` before major work. Locate related acceptance criteria in `prd.md`.
2. State goal, scope, dependencies, potential external approvals and exact tests.
3. Inspect repository and do not assume technology decisions are already implemented.
4. Implement a small vertical slice using the contracts in `trd.md` and `Backend Schema.md`.
5. Run tests; report failures, unrun checks and provider limitations truthfully.
6. Update `tracker.md` item status and evidence; update `memory.md` only for decisions or new durable truths. Commit only when requested/authorized.

## F. Conflict resolution / change control

**Source hierarchy:** user-approved changed requirements → 53-page master partition policy → `prd.md`/`rules.md` → technical designs → tracker. Safety/financial truthfulness are never weakened by mere UI requests. When two docs contradict, record a `DEC-...` in `memory.md`, do not silently choose one for a security-sensitive or scope-changing decision, and keep unfinished work blocked.
