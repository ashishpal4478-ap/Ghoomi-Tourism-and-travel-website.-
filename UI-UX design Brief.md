# GHOOMI — UI/UX Design Brief

**Revision:** 1.0 • **Active stage:** Partition 1, before first deployment • **Source:** 53-page master PDF sections 14–40 and 91. **Visual palette and layout choices are design recommendations, not finalized brand assets.**

## 1. Vision, audience and design principles

Ghoomi should feel like a reliable modern travel marketplace, but its signature experience must be **complete-trip planning**. Brand line: *You Enjoy the Journey. Ghoomi Manages the Journey.* Vision: *From Home to Home.* Design for first-time travelers, students, solo travelers, friends, couples, families and support/admin operators.

- **Search first, plan deeply.** Expose familiar flight/train/bus/cab/hotel tabs and a prominent `Plan my trip with AI` action.
- **Clarity beats cleverness.** Users must understand whether they are planning, viewing a supplier quote, going to an external provider, or actually booking.
- **One trip context.** Carry origin, destination, dates, travelers, selection and estimated budget from discovery to itinerary to dashboard.
- **User agency.** Edit AI suggestions, skip transport/stay when already arranged, control cost level and undo when supported.
- **Trust through content.** Display source, freshness, currency, amount basis, fees/cancellation conditions where verified. Never invent badges, scarcity, reviews or booking confirmations.
- **Accessibility and speed.** Semantic HTML, visible focus, keyboard-friendly controls, responsive composition, reduced motion, optimized licensed imagery.

## 2. Proposed visual system

The product source does not specify an approved palette. These are starter tokens to keep coding-agent screens visually consistent until the user confirms the branding.

| Token | Proposed value | Intended use |
|---|---|---|
| Navy | `#102B46` | Navigation, hero overlay, strong headings |
| Teal | `#087F8C` | Primary button, selected nav and links |
| Teal hover | `#056571` | Interactive hover/pressed states |
| Coral | `#F97355` | Small accents (never sole meaning) |
| Canvas | `#F7FAFC` | Light page background |
| Surface | `#FFFFFF` | Cards, dialogs, forms |
| Text | `#193247` | Normal body text |
| Muted | `#597184` | Metadata with sufficient contrast |
| Border | `#DAE5EB` | Card separators and inputs |
| Success | `#147D54` | Verified status only |
| Warning | `#9B5B10` | Estimated, changed or unverified data |
| Error | `#B93232` | Failure / destructive actions |
| Focus | `#176FCE` | Focus ring |
| Radius | 10px controls / 16px cards | Consistent surface shape |

Typography proposal: Inter or system UI font, 16px body / 1.5 line-height, 14px metadata, 30–44px heading depending on width. Use 8px spacing rhythm, max content width around 1280px and subtle card shadows. Photos must be licensed/relevant and not used as false proof of hotel/service inventory. Color alone never communicates verification.

### 2.1 Responsive and accessibility contracts

| Viewport | Layout guidance |
|---|---|
| 320–599px mobile | Single-column forms/results, prominent primary CTA, scrollable travel tabs, compact progress stepper, mobile-safe filter drawer |
| 600–1023px tablet | Flexible 2-column cards, wrapping search fields, summary moved below planner content when necessary |
| >=1024px desktop | 12-column grid, top global nav, results with filter sidebar, planning workspace with optional budget sidebar |

Target WCAG 2.2 AA, meaningful input labels, error summaries, keyboard-accessible tabs/reordering, polite announcements for result count/budget changes, adequate contrast, reasonable touch targets, skip link and focus retention when dialogs open/close. Don't hide important status in tooltips only.

## 3. Information architecture and navigation

**Public:** `Flights`, `Trains`, `Buses`, `Cabs`, `Hotels`, `Holidays`, `Explore`, `Login`, `Sign Up`. Food and activities may live in Explore/More. **Signed-in:** `Dashboard`, `My Trips`, `Plan a Trip`, `My Bookings` (only with truthful mode), `Notifications`, `Safety`, `Support`, `Profile`. **Admin:** separate role-gated navigation.

Maintain a consistent search form pattern: category → criteria → loading/results → compare/detail → add to trip or supported provider checkout. Maintain persistent `Save draft` and a breadcrumb/stepper in the trip wizard. No dead CTA to unimplemented future pages.

## 4. Reusable UI components and rules

| Component | Required content and interaction |
|---|---|
| `TravelModeTabs` | Icon+label flight/train/bus/cab/stay/holiday tabs; keyboard support and correct fields per tab |
| `SearchForm` | Origin/destination/place autocomplete; dates; travelers; clear validation; preserves inputs on errors |
| `DestinationCard` | Licensed image, place name, short travel context, link to guide or create trip |
| `OfferCard` | Provider name, duration/timing, taxes/fees/price basis, source+freshness, capability mode and next action |
| `SourceBadge` | `Live verified`, `Provider listed`, `Estimate`, `Unknown/Unavailable` with explanatory legend |
| `PriceBreakdown` | Supplier amount, applicable taxes and platform fees, discount if genuine, total, currency, per-person/total |
| `TripStepper` | Progress, back/next/save-exit, current step and descriptive headings |
| `DayCard` | Day/date, activities in order, times, estimated durations, costs and accessible reorder/edit/remove actions |
| `TripBudgetPanel` | Chosen budget, estimated sum, remaining, category breakdown, assumption footnotes |
| `BookingStatusChip` | `External/unverified`, `Pending`, `Confirmed`, `Failed`, `Unknown` with source/time |
| `SafetyCard` | Contacts + official emergency information, no guarantee of rescue or live monitoring |
| `NotificationsList` | Real events, status, link to relevant trip/booking/support ticket |
| `Error/Empty/Loading` | Explicit source-dependent errors, safe retries, helpful next action; no fake fallback inventory |

## 5. Partition 1 page specifications, aligned to master inventory

### 5.1 Public landing page (page 1)

- **Above the fold:** scenic travel imagery with readable overlay, Ghoomi brand, core search tabs and a separate `Plan a Trip with AI` CTA.
- **Main body:** popular destination guides, holiday ideas, benefits of unified planning (editable itinerary, estimated spend, saved trips), supported transport/stay categories and service policies.
- **Footer:** support, contact, privacy/terms, booking responsibility disclosures. Use no invented partner logos, testimonials or offers.
- **Suggested hero:** “One journey. One place to plan it.” Supporting copy: “Explore travel options, create a personalized itinerary and keep your budget organized.”

### 5.2 Identity and user dashboard (pages 2–7)

- Sign Up/Login: clean centered form, strong labels, password visibility, reasonable validation, subtle illustration at larger widths; display async/auth errors without account enumeration.
- Verification/forgot password: expiry, retry limits, safe resending and next steps.
- Profile: basic fields, travel preferences, saved travelers and emergency contacts, clearly optional sensitive fields.
- Signed-in overview: quick `Create trip`, draft/recent trip cards, notifications and truthful provider-service availability.

### 5.3 Destination search and details (pages 8–9)

Search by destination and themes (culture, budget, weekend, adventure). Detail view contains verified/curated overview, photos, attractions, hotels, food, activities and helpful travel tips. Local events/opening hours require source and freshness or `Not verified`. The `Add to Trip` action selects a saved trip or starts a new one.

### 5.4 Trip creation / traveler details / AI planning (pages 10–12)

Recommended wizard: `Route & dates → Travelers → Interests & comfort → Budget & plan mode → Generate → Customize & save`. Each stage validates and persists after explicit save or confirmed autosave. Modes: **Budget, Balanced, Comfort, Custom**; category overrides for transport, stays, meals and activities. `Own Vehicle` and `Already Have Stay` skip unwanted booking.

### 5.5 AI itinerary and customization (pages 13, 15)

Desktop: day tabs at left, editable chronological timeline center, budget summary right. Mobile: day selector and vertically stacked cards with expandable summary. Actions: add/remove place, edit time, reorder, change category, regenerate selected day with scope warning, undo when supported, save. Distinguish suggested vs verified availability and cite any supplier data behind price/time claims. AI failures show `Continue manually` and restore existing plan.

### 5.6 Budget estimate (page 14)

Top summary: `Budget`, `Estimated trip cost`, `Difference`, `Estimated per person` only when mathematically valid. Categories: outbound/return transport, accommodation, meals, local travel, activities, taxes/fees where supported, emergency reserve, other. Changes must recompute real deterministic amounts, not just switch cosmetic chart figures. Show `Estimated for planning, recheck supplier costs before booking`.

### 5.7 My Trips and trip hub (pages 16–17)

My Trips: saved draft/planned/active/completed cards with route, dates, type, budget, action `Continue planning`. Trip Hub: identity of trip, time/date summary, itinerary, selected transport/stay, budget panel, booking status, basic safety and support. Suggested tabs `Overview | Itinerary | Transport | Stay | Budget | Bookings | Safety`. Selected planned items are not assumed booked. No animated live traveler marker in initial launch.

### 5.8 Transport (pages 18–31)

Shared search-result system with header criteria, comparison/filters, source badges and detail page. Vertical-specific details:

- **Flights:** carrier, departure/arrival, stop count, luggage when supplied, terms, total vs per-person fare, supplier timestamp.
- **Trains:** exact stations, schedule, class, seat status only if authorized; booking handoff clearly states supplier/rail authority.
- **Buses:** operator, class, boarding/drop points, date, route, price and supplier terms.
- **Cabs:** pickup/drop, service type (local/airport/outstation), car category, estimated or partner quote and provider responsibility.
- **Own vehicle:** route-planning guidance and user-adjustable estimated fuel/tolls; no checkout.

Each results page requires no-results, unavailable-provider, invalid-criteria and quote-stale states. Filtering/sorting must use underlying record values, not ornamental controls.

### 5.9 Hotels / stays (pages 32–38)

Search by destination/check-in/check-out/guests/rooms; result grid/list with rating source, property images, amenities, location, rate provenance and cancellation terms when verified. Detail page has gallery, room details, guest form only when needed, cost summary and booking capability. If not authorized for direct booking, `Continue at provider` or `Add to trip`; never simulated confirmed booking. Provide `Already have accommodation` mode.

### 5.10 Food and attractions (pages 39–44)

Restaurant and activity lists use location, cuisine/category, approximate expense, photos and reviews only when sourced. Include detail pages with map or route context if licensed mapping exists, and `Add to itinerary`. No implicit food delivery or ticketing when discovery-only.

### 5.11 Booking and payment operations (pages 45–50)

For **authorized direct** checkout: concise stepper `Review → Revalidate → Traveler details → Payment provider → Pending verification → Supplier-confirmed result`. Show total breakdown, explicit terms and separate provider/payment status. On quote change require fresh approval. External redirect clearly discloses provider responsibility. Unknown payment status uses `We're checking your transaction` and a support path, never optimistic success.

### 5.12 Safety, notifications, support, admin (pages 51–55)

- Safety: maintain emergency contacts and source-verified emergency information; no automated SOS dispatch.
- Notifications: read/unread message with event type/status and accurate timestamp.
- Support: support request type, trip/booking association, description, created ticket ID and tracked status.
- Admin: clean role-gated tables for users, destinations, providers, reports and tickets; avoid exposing sensitive PII; require confirmation on destructive actions.
- Partner management in V1: **admin-side integration records only**; full self-service partner interface moves to Partition 2.

## 6. Required microcopy and truth-state labels

| Situation | Use | Do not use |
|---|---|---|
| AI plan | `Suggested itinerary — review before travel` | `Everything is booked` |
| Estimate | `Estimated total (subject to supplier price)` | `Guaranteed final cost` |
| No supplier API | `Live booking not available here` | `Book now` leading to simulated success |
| External checkout | `Continue to [provider] to complete booking` | `Booked with Ghoomi` |
| Unverified external reference | `Reference added, not verified` | `Confirmed` |
| Payment status unknown | `Payment status being verified` | `Successful` without gateway evidence |
| AI failure | `AI plan unavailable. Continue planning manually.` | Empty/false generated plan |
| Emergency info | `Contact official emergency services directly` | `Help has been dispatched` without verified integration |

## 7. State design and design QA

Every page must have: **initial, submitting/loading, populated, no-results, validation error, provider outage (where applicable), auth/permission failure, and success**. Crucial data changes (budget, booking status, saved trip) announce result clearly; errors cannot be toast-only. Use real backend DTOs and explicit source freshness; do not confuse cached places with real-time service availability.

Minimum design handoff: home, signup/login, service search/results/details, trip wizard, AI itinerary/editor, budget, dashboard, supplier redirect or authorized checkout, booking status, safety and support at **desktop ~1440px**, **tablet ~768px** and **mobile ~390px**. Verify keyboard path, focus, form validation, currency formatting and accessible reordering. Future pages reuse established shell/components, not a separate inconsistent visual system.

## 8. Future visual expansion (not active build)

- **Partition 2:** group invitations/member permissions, family monitoring with explicit location grant/revoke, synchronized trip timeline, cancellation/refund status, protected booking documents, partner support and advanced operations UI.
- **Partition 3:** disruptions and alternate itineraries with approval and cost delta, Back-to-Home, SOS with delivery/source truth, travel assistant, verified events/weather, expense splitting, offline read-only caches, Ghoomi Explore, privacy-conscious public trips, community moderation, marketplace and premium plans.

For exact source page inventory and route mapping see `App Flow.md`; for UI acceptance criteria use `prd.md`; for implementation ordering see `Implementation plan.md`.
