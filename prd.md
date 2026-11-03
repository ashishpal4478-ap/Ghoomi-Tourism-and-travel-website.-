# GHOOMI — Product Requirements Document (PRD)

| Field | Value |
|---|---|
| Product | **Ghoomi** — smart travel planning, discovery, authorized booking and management |
| Tagline | **You Enjoy the Journey. Ghoomi Manages the Journey.** |
| Vision | **From Home to Home.** |
| Document revision | 1.0 — 2026-11-03 |
| Source basis | `reference/Ghoomi_Working_Platform_Master.pdf` (53 pages, 95 sections) |
| Current build scope | **Partition 1: Before First Deployment** |
| Status | Proposed implementation specification, **not** confirmation of implemented software |

> **Source fidelity.** The three partitions, named features, core workflows and the scope boundaries below come from Ghoomi's latest 53-page working-platform explanation. The requirement IDs, quantitative success measures, risk register, acceptance checks and launch-mode handling are **engineering/product recommendations** added to make the plan executable. Do not equate a proposed API with partner authorization.

## 1. Executive summary

Ghoomi aims to reduce the fragmented travel experience of researching flights, trains, buses, cabs, hotels, activities, restaurants and complete holiday schedules across many unrelated apps. The **initial release must resemble a credible travel discovery and comparison service** in its core user experience, but also provide genuine differentiators: useful AI-generated itineraries, transparent trip budgeting, easy plan editing and a centralized dashboard. Users can plan a complete journey even when some booking providers are not yet onboarded.

Ghoomi is **not** a guaranteed direct-booking agent for all services on day one. Where supported by authorized integrations, users may proceed to verified reservations or secure partner checkout. Other services must display their actual data/capability state without fictitious pricing, confirmations or checkout flows.

### 1.1 Product principles

1. **Unified journey over disconnected bookings.** A saved trip links departure, transport, stay, places, budget, planned activities and return.
2. **Familiar discovery, differentiated intelligence.** Clear service-specific search and comparison plus itinerary optimization and a trip hub.
3. **Customer controls decisions.** AI proposes, user edits; bookings, changes and costs require appropriate user approval.
4. **Trust is visible.** Source provenance, real/estimated price labels, last checked times, payment and booking status are never blurred.
5. **Progressive expansion.** Partition 1 launches a useful product; Partition 2 extends real operations; Partition 3 unlocks the full ecosystem.
6. **Modular choices.** Own Vehicle / Already Have Stay / no food planning remain valid options.
7. **Secure and accessible from release one.** Privacy, server-side authorization, responsive layout and error handling are foundational, not future premium extras.

## 2. Problems, personas and key jobs

| Persona | Primary job-to-be-done | Important constraints |
|---|---|---|
| Budget traveler / student | Find realistic affordable destination and trip options | Total cost, transport choice, flexibility |
| Solo traveler | Plan independently and see key safety information | Privacy, transparent results, accessible support |
| Friends / trip organizer | Compare options and organize an itinerary for several travelers | Correct traveler count, one saveable plan; full group collaboration later |
| Couple / family | Find suitable stays and activities for a chosen comfort level | Guest/room rules, timing, filters and budget |
| General traveler | Compare providers and return to a saved plan | Consistent sources and reliable last-saved state |
| Admin/support operator | Maintain trusted content, resolve reported issues and observe failures | Strict access control and auditability |
| Provider/partner (initially limited) | Expose permitted inventory or authorized booking flow | Agreement, API rights, provenance and current capability |

### 2.1 User pain points

- Searching multiple vendors repeats the same travel inputs; total trip spend remains unclear.
- Separate bookings are hard to connect with day-wise activity plans and return travel.
- Generic AI travel plans can be impractical, fabricated or not editable.
- Users struggle to compare transport/stay trade-offs and keep selected plans organized.
- Safety information and emergency contacts are often scattered.
- In later phases, itinerary changes, groups, family updates and refunds become complicated.

## 3. North-star experience and primary user journey

**Visitor** → explore destination or service → sign up / sign in for saved trip → provide origin, destination, dates, travelers, budget, preferences → choose Budget / Balanced / Comfort / Custom → generate or manually build itinerary → compare available transport/stay options → add restaurants and activities → edit and reorder the plan → view deterministic cost estimate → save or resume the trip → take **only supported** booking or partner-redirect actions → manage itinerary and confirmed/unverified references in the unified dashboard → travel and return. See `App Flow.md` for branch-by-branch navigation and error states.

### 3.1 Launch-mode classification

| Mode | What user can actually do | What must be shown |
|---|---|---|
| `PLANNING_ONLY` | Research and add a category to a plan based on verified editorial information or clearly labeled indicative estimates | `Planning / estimate only`, no fake Book button |
| `REDIRECT` | Follow authorized listing/affiliate link to provider site to complete booking | `Continue at provider`, clear provider responsibility |
| `LIVE_SEARCH_EXTERNAL_CHECKOUT` | Search live provider inventory, then complete eligible booking at provider | Provenance, quoted terms, last refreshed time |
| `DIRECT_BOOKING` | Complete supplier-authorized reservation through Ghoomi's supported backend | Real recheck, payment policy, confirmation/reference status |

**Do not assign a mode until it is proven by integration tests and legal/provider approval.** Modes may differ by category, market, partner and environment.

## 4. Product scope and priorities

### 4.1 Partition 1 — before first deployment (ACTIVE)

`P0`: essential to a usable first release. `P1`: intended initial-release supporting capabilities; feature can use a restricted truthful launch mode if external rights are not available.

| Requirement ID | Priority | Feature | V1 minimum behavior |
|---|---|---|---|
| `GHO-P1-AUTH` | P0 | Signup, login, recovery and session | Users own and secure their accounts; verification and recovery flows work |
| `GHO-P1-PROFILE` | P0 | Profile, travelers and preferences | Persist profile/traveler preferences with minimal necessary personal data |
| `GHO-P1-HOME` | P0 | Public website and global navigation | Distinct responsive travel discovery entry points and sign-in routes |
| `GHO-P1-DEST` | P0 | Destination discovery | Real/curated destination details, images with rights, place and guide content |
| `GHO-P1-TRIP` | P0 | Create, save, edit and resume trips | Origin, dates, travelers, preferences, budget and clear draft lifecycle |
| `GHO-P1-AI` | P0 | Smart planner and day-wise itinerary | Server-backed AI with structured validation, editable days and manual fallback |
| `GHO-P1-BUDGET` | P0 | Estimate and optimize budgets | Deterministic category totals, source labels, what-if scenarios and trade-offs |
| `GHO-P1-TRANSPORT` | P0 | Transport discovery | Flight, train, bus, cab/taxi, own-vehicle; genuine source/capability labels |
| `GHO-P1-STAY` | P0 | Accommodation discovery | Hotel/stay search, comparisons and available details/terms; existing stay path |
| `GHO-P1-EDIT` | P0 | Trip customization | Add/remove/reorder activities, alter choices/dates and recalc budgets |
| `GHO-P1-HUB` | P0 | My Trips and unified trip dashboard | Saved trip, selected services, itinerary, budget and booking statuses together |
| `GHO-P1-FOOD` | P1 | Restaurants and local food | Discover food options, filters and relevant location information; no fake ordering |
| `GHO-P1-ACT` | P1 | Attractions, activities and guides | Search/filter places and add them to itinerary with known time/cost metadata |
| `GHO-P1-BOOK` | P1 | Initial reservation/handoff | Authorized booking or provider redirect, status provenance and references |
| `GHO-P1-PAY` | P1 | Payment/cost summary | Transparent charges and verified statuses **only for enabled integrations** |
| `GHO-P1-SAFETY` | P1 | Basic safety | Emergency contacts, official emergency directory and travel safety tips |
| `GHO-P1-NOTIFY` | P1 | Basic notifications | Account, trip, booking and support events via configured approved channels |
| `GHO-P1-SUPPORT` | P1 | Customer support | Support form/ticket creation, status and admin review |
| `GHO-P1-ADMIN` | P1 | Admin foundation | Role-protected overview, content, tickets and relevant booking/provider records |
| `GHO-P1-PARTNER` | P1 | Initial provider adapters | Selected authorized integrations, capability matrix and health monitoring |
| `GHO-P1-QUALITY` | P0 | Release trust/reliability | AuthZ, responsive UI, error handling, testing, legal/product transparency, backups |

### 4.2 Partition 2 — after first deployment (NOT IN ACTIVE BUILD)

| ID | Module | Functional outcome / dependency |
|---|---|---|
| `GHO-P2-BOOK` | Expanded direct booking | More providers, stronger booking orchestration; requires real contracts and confirmed bookings |
| `GHO-P2-PAY` | Advanced payments/commissions | Verified gateway events, settlements, reconciliation, financial auditability |
| `GHO-P2-REFUND` | Modification/cancellation/refunds | Provider policies, user approval, confirmed refund statuses and support |
| `GHO-P2-GROUP` | Group coordination | Invites, organizer/member permissions, shared itinerary, updates |
| `GHO-P2-FAMILY` | Family/trusted contacts | Explicit invitations, time-scoped consent, revoke and protected sharing |
| `GHO-P2-LIVE` | Live journey tracking | Verified trip-event feed, last-known location timestamp and no false-live claims |
| `GHO-P2-DOC` | Booking documents | Private uploads, scanning, controlled access and retention |
| `GHO-P2-NOTIFY` | Advanced notifications | Preferences, provider updates, email/SMS/push/WhatsApp where approved |
| `GHO-P2-SUPPORT` | Advanced support | Escalation, booking disputes, payment/refund case management |
| `GHO-P2-PARTNER` | Partner workspace | Onboarding, inventory, booking views, commissions, restricted access |
| `GHO-P2-ADMIN` | Advanced administration | Transactions, audit trail, failed-webhook handling and integration monitoring |

### 4.3 Partition 3 — long-term expansion (NOT IN ACTIVE BUILD)

| ID | Module | High-level goal |
|---|---|---|
| `GHO-P3-RECOVERY` | Intelligent trip recovery | Detect disruption → impact → verified alternatives → cost → approval → changes |
| `GHO-P3-HOME` | Back-to-Home | Coordinate an early return without auto-canceling or auto-paying |
| `GHO-P3-SOS` | Safety centre and SOS | Trusted-contact workflow, official information, incident handling; no false dispatch |
| `GHO-P3-ASSIST` | Advanced AI travel assistant | Contextual support grounded in live approved data and permissions |
| `GHO-P3-EVENTS` | Live events and destination intelligence | Current, source-verified listings and relevance ranking |
| `GHO-P3-WEATHER` | Weather-aware planning | Verified weather, appropriate alerts and activity alternatives |
| `GHO-P3-SPLIT` | Group expenses | Expense ledger, shares and settlement computation |
| `GHO-P3-OFFLINE` | Offline essentials | Previously synchronized travel data with freshness and limits |
| `GHO-P3-WALLET` | Secure document wallet | Protected sensitive documents, authorized access, deletion and audit |
| `GHO-P3-SOCIAL` | Public experiences/community | Explicit public opt-in, memories, reviews, itineraries and moderation |
| `GHO-P3-EXPLORE` | Ghoomi Explore | Curated/community trip discovery and itinerary inspiration |
| `GHO-P3-MARKET` | Partner marketplace | Verified partner listings, inventory/pricing and commissions |
| `GHO-P3-PREMIUM` | Membership | Optional validated premium entitlements and transparent billing |
| `GHO-P3-FINANCE` | Advanced financial operations | Commercial reconciliation and finance reporting subject to agreements |
| `GHO-P3-ANALYTICS` | Intelligence/analytics | Validated aggregate operational and product reporting |

## 5. Partition 1 detailed user stories and acceptance criteria

### 5.1 Identity and users — `GHO-P1-AUTH`, `GHO-P1-PROFILE`

**Stories.** As a visitor, I can create an account and securely return to my plans. As an authenticated traveler, I can maintain preferences and add travelers without exposing those profiles to other accounts.

**Acceptance criteria:**

- [ ] Signup accepts only validated fields; duplicate account handling does not disclose private account state unnecessarily.
- [ ] Login/logout, password reset and email verification function end-to-end with expiry/replay protection appropriate to implementation.
- [ ] Server rejects unauthenticated access to private trip/traveler resources; cross-account UUID requests return no data.
- [ ] User can edit profile, default budget range, food/travel interests and save limited traveler records; booking-specific data is requested only when required.
- [ ] Account recovery, input error, rate limiting and session expiry have clear user-facing states.

### 5.2 Destination, discovery and home — `GHO-P1-HOME`, `GHO-P1-DEST`

**Stories.** As a visitor, I can find destinations, open guides and launch trip planning from useful, filterable content.

- [ ] Navigation contains meaningful entrances to flights, trains, buses, cabs, hotels, holiday planning, food and activities.
- [ ] Destination guide includes overview, attractions, relevant map/location context, useful tips and verified/curated source labels.
- [ ] Search supports no-results, network failures, location ambiguity and date corrections without losing submitted criteria.
- [ ] Images/content have acceptable licensing; unsupported current events/hours are labeled unknown rather than fabricated.

### 5.3 Trip wizard and saved trips — `GHO-P1-TRIP`

**Story.** As a traveler, I can make a trip once and return to it rather than re-entering everything.

- [ ] Inputs: source, destination, start/end, traveler count/types, budget/currency, travel type, pace, interests, transport/stay preferences, optional departure/return times.
- [ ] Start date <= end date; impossible/invalid traveler counts and incompatible options receive actionable validation.
- [ ] Every meaningful change can be saved; reloading or signing in again restores the correct user's data.
- [ ] Own Vehicle and Already Have Stay skip irrelevant external booking steps but still contribute relevant planned costs.
- [ ] State supports `DRAFT`, `PLANNED`, `ACTIVE`, `COMPLETED`, `CANCELLED` with validated transitions and audit metadata.

### 5.4 AI itinerary — `GHO-P1-AI`

**Story.** As a trip planner, I receive a logical, adjustable itinerary that respects duration, preferences and known constraints.

- [ ] Backend sends only authorized minimum trip context to configured AI provider; no API keys/client secrets exposed.
- [ ] Output is structured by day with place/title, proposed start/end, estimated travel time/cost, type and uncertainty/source fields.
- [ ] Validate chronology, date coverage, impossible durations and obviously contradictory activities; if invalid, repair safely or report a recoverable error.
- [ ] Users can accept selected days, reject recommendations, reorder items and regenerate with explicit indication of what may change.
- [ ] AI service error/timeout yields manual template or existing saved itinerary, **not** a blank or fabricated successful response.
- [ ] Never claim an AI suggestion is proof of availability, opening hours, price, reservation or weather.

### 5.5 Budget engine — `GHO-P1-BUDGET`

**Story.** As a budget-conscious traveler, I see how transport, stay, food and activities affect the complete cost.

- [ ] Categories include outbound/return transport, stay, meals, local travel, attractions, taxes/fees if verified, emergency reserve and optional miscellaneous.
- [ ] Compute `estimated_total = sum(estimated_line_items)` with integer paise and clear currency; `remaining = user_budget - estimated_total`.
- [ ] Show amounts as `estimated`, `provider-quoted`, `paid` (where verified) or `user-entered`; never merge states silently.
- [ ] Budget/Balanced/Comfort/Custom plans allow category-level trade-off; explain savings assumptions and impacts.
- [ ] Changes to travelers, dates, offer selection and itinerary recalculate estimates deterministically and preserve a revision trail where practicable.

### 5.6 Transport discovery — `GHO-P1-TRANSPORT`

**Story.** As a user, I can compare relevant transport modes without confusing estimated results with confirmed inventory.

- [ ] Flight inputs: departure/arrival, dates, passengers; results show carrier, times, duration, terms and verified/estimated price provenance.
- [ ] Train inputs: origin/destination stations, date, class; schedules/fares/availability appear only from authorized data. Booking may require authorized rail partnership.
- [ ] Bus inputs: source/destination/date, operator, bus type, boarding/drop points and eligible availability.
- [ ] Cab inputs: pickup/drop, time, type and local/airport/outstation options; show estimated vs partner-confirmed quote accurately.
- [ ] Own Vehicle option creates route/cost planning rather than fake booking.
- [ ] Filters/sorting operate on actual returned attributes and preserve the search criteria; unsupported verticals show actionable honest fallbacks.

### 5.7 Stays and services — `GHO-P1-STAY`, `GHO-P1-FOOD`, `GHO-P1-ACT`

- [ ] Search accommodation by destination, stay dates, guests, rooms and budget; result cards show reliable location, facilities, prices and cancellation conditions where available.
- [ ] Detail and room selection distinguish property editorial descriptions from live room inventory; user can use own/family accommodation.
- [ ] Restaurants: cuisine, location, price band and available provider-derived details; discovery only unless ordering is explicitly integrated.
- [ ] Activities: attractions, experience type, duration and known fees; can add/remove activities from trip without needing a ticketing integration.
- [ ] Plans handle missing reviews, missing open-now data, provider outages and no matching offers gracefully.

### 5.8 Trip customization and hub — `GHO-P1-EDIT`, `GHO-P1-HUB`

- [ ] User can add/remove/rearrange itinerary items and selected services; revisions save and are scoped to the owner.
- [ ] Dashboard aggregates title, dates, travelers, all selected services, cost estimate, saved items, status/confirmed bookings and next planned activity.
- [ ] Clear separation among `planned itinerary`, `external unverified reference`, `provider-confirmed booking` and `paid` amounts.
- [ ] No stale UI after edit, page refresh or resuming from My Trips; long or complex trips remain mobile usable.

### 5.9 Initial booking, payment and operations — `GHO-P1-BOOK`, `GHO-P1-PAY`, `GHO-P1-NOTIFY`, `GHO-P1-SUPPORT`, `GHO-P1-ADMIN`, `GHO-P1-PARTNER`

- [ ] Each enabled provider has documented capability/authorization; quote or availability revalidated before eligible checkout.
- [ ] Redirect mode identifies provider and responsibility. Direct mode records distinct request/processing/confirmed/failed states and supplier reference.
- [ ] Monetary operations are impossible without verified gateway setup, server-side callbacks/webhook verification, idempotency and auditability.
- [ ] User sees provider price, applicable fees, taxes and final amount; changed price requires acceptance.
- [ ] Support tickets save and show status; administrators can process within authorized access; notifications reflect actual events and delivery results.
- [ ] Admin panel protects privileged routes, supports basic content/provider/ticket oversight, and logs important actions.
- [ ] If no partner credentials exist, provide an honest planning or supported redirect experience and explicitly mark provider integration `Blocked`; never simulate production success.

### 5.10 Basic safety and platform quality — `GHO-P1-SAFETY`, `GHO-P1-QUALITY`

- [ ] User can store/revise emergency contacts and view relevant official emergency guidance with source/location context.
- [ ] V1 does **not** promise dispatch, guaranteed live tracking or family monitoring; those are later partitions.
- [ ] UI is keyboard navigable, responsive, usable on mobile, resilient to retries/network failure and properly labeled.
- [ ] Automated auth/account-isolation, money calculation, booking-truth, API schema and E2E user-flow tests pass in staging.
- [ ] Production operations have backups/restore rehearsal, log redaction, alert routes, rollback plan and privacy/legal pre-launch review.

## 6. Functional and nonfunctional boundaries

### In scope for first deployment

A complete **plan → compare → customize → estimate → save → supported checkout or provider handoff → manage trip** journey. Booking and payment operate only to the depth actually authorized; a reliable planning service can launch with some non-bookable categories clearly labeled.

### Explicitly out of scope for first deployment

- Broad direct supplier inventory ownership and third-party checkout circumvention.
- Automated multi-provider refund/cancellation orchestration or guaranteed refund times.
- Live GPS/family dashboard, continuous background location or emergency dispatch.
- Intelligent disruption rebooking / Back-to-Home auto execution.
- Shared group wallet, public social feed, partner ERP/marketplace and premium subscription billing.

### Proposed initial quality targets (validate/replace with real data)

| Dimension | Proposed beta launch gate |
|---|---|
| Account isolation | 0 reproducible cross-account resource access vulnerabilities |
| Saved trip integrity | >=99% successful persistence in controlled beta under nominal test conditions |
| AI fallback | 100% of tested AI-unavailable cases allow manual editing/saving |
| Booking representation | 0 fabricated `CONFIRMED` statuses in tests; every confirmation has provider evidence |
| Responsive UX | Main user flows checked at ~360px, 768px, 1280px widths |
| Accessibility | Automated checks and manual keyboard/screen-reader smoke tests on high-traffic flows |
| Operations | Backup restore, rollback and alert escalation exercises completed |
| Content | Each public price/inventory claim shows source and freshness or clear uncertainty |

These are targets, **not performance claims**.

## 7. Example acceptance scenario

**Journey:** four friends plan a five-day Mumbai-to-Goa trip with an INR 30,000 budget.

1. User registers and creates trip with date range, four travelers and category preferences.
2. User generates a day-wise itinerary and chooses more comfortable accommodation but cheaper transport.
3. Ghoomi explains revised estimated spend, remaining budget, assumptions and which items have genuine supplier quotes.
4. User filters hotels and transport, selects supported options or own transport/stay, and saves the itinerary.
5. Dashboard retains accurate activities and estimate after logout/login.
6. If booking integration is authorized, user reviews rechecked quote, fees and terms, follows correct checkout and receives only verified reservation status. If not, the app shows `Planning only` or clearly describes a provider handoff.
7. User can access relevant emergency information and support without fictitious live monitoring.

This scenario becomes a core automated E2E acceptance test and fixture, clearly labeled as **test data**.

## 8. Business model — planned, not contracted

The intended initial business model is **partner commissions** on eligible completed bookings, with possible disclosed platform/convenience fees where permitted. No commission rate, service charge, GST calculation method or direct payment flow is finalized. Partner agreements and legal/payment review must precede any commercial claims. Additional premium, featured listing and marketplace revenue belong to later validation stages.

## 9. Metrics and product learning

Instrument with privacy-aware analytics: destination searches, search-result usefulness, trip wizard completion, itinerary generation/fallback, saves, edits, budget category adjustments, partner handoffs, **verified** booking confirmations (separate from clicks), failures by provider, support cases and retention. Segment only at appropriate aggregate/privacy-safe levels. Review beta findings before prioritizing Partition 2.

## 10. Risks and mitigations

| Risk | Mitigation / launch decision |
|---|---|
| Provider APIs unavailable, expensive or restricted | Capability matrix; truthful planning/approved redirect mode; begin with 1–2 reliable verticals |
| Railway and hotel integrations require special approval | Secure explicit rights/terms; no unauthorized ticket issuance or room guarantee |
| AI generates impractical itinerary | Structured output, server validation, source attribution, manual edits/fallback |
| Estimates appear as guarantees | Provider source labels, age/terms display, recheck before checkout |
| Payments and bookings mismatch | Independent states, verified events, idempotency, reconciliation and support path |
| Too many initial pages | Group 55 inventory entries into reusable route families and end-to-end slices |
| Safety expectations exceed browser/provider abilities | Basic emergency information first; consent-based future tracking; honest limitations |
| Sensitive traveler data leaked | Data minimization, authorization tests, encryption and audited access |
| Thin support capacity | Pilot geography, controlled rollout, visible support/responsibility boundaries |

## 11. Definition of Partition 1 release-ready

A release is not ready merely because pages visually exist. All **P0** items must have tested user flows, persistence and protection; P1 items must either work as promised or be transparently restricted to the authorized capability. Validate the 17 first-release criteria in the source (section 43), plus rules in `rules.md` and release gates in `Implementation plan.md`. Every gate outcome must have evidence in `tracker.md`.

## 12. Traceability and change policy

- UI routes and detailed behavior: `App Flow.md` and `UI-UX design Brief.md`.
- API/provider contracts and stack: `trd.md`.
- Database entities and migrations: `Backend Schema.md`.
- Execution sequence: `Implementation plan.md`.
- Work/evidence and readiness: `tracker.md`.
- Decision history/unknowns: `memory.md`.
- Binding agent policies: `rules.md` + `AGENTS.md`.

**Scope change:** amend this PRD feature row/ID, related flow/contract/schema and tracker; log the approved decision in `memory.md`. Avoid independent, drifting copies of the feature plan.
