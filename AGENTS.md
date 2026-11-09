# GHOOMI — Repository-wide instructions for coding agents

**Applies to:** every directory in this repository. **Specification release:** 1.1 — MySQL edition • **Product stage:** Partition 1 (pre-first-deployment) • **Status:** specification, not implemented software.

> **START HERE.** Read `rules.md`, `memory.md`, `tracker.md`, `prd.md`, `App Flow.md`, `UI-UX design Brief.md`, `trd.md`, `Backend Schema.md`, and `Implementation plan.md` before making broad architectural or feature changes. Re-read only relevant sections for subsequent small tasks. This repository uses filenames with spaces: quote paths when running shell commands.

**Database selection (user decision): MySQL.** Any new Partition 1 schema uses MySQL 8.4 LTS/InnoDB-compatible SQL, after verifying the environment. The previous PostgreSQL reference is superseded; do not silently switch back or overwrite an existing database. See `Backend Schema.md` and `memory.md`.


## Mandatory execution contract

1. **Inspect first.** Check source tree, README, package manifests, environment conventions, existing migrations, provider credentials (names only), tests, and running infrastructure before choosing implementation actions. Never overwrite healthy existing code because a spec proposes a preferred stack.
2. **Scope the work.** Select the next approved `P1-...`/`IMP-...` item from `tracker.md` and `Implementation plan.md`. State the feature IDs, files/components affected, prerequisites and blockers. **Partition 2/3 require explicit user approval.**
3. **Use the latest source of truth.** The 53-page `reference/Ghoomi_Working_Platform_Master.pdf` defines the three product partitions. `prd.md` defines product behavior and release scope; `rules.md` defines trust/safety and working constraints; `trd.md` and `Backend Schema.md` define proposed technical contracts; `App Flow.md` and `UI-UX design Brief.md` define user experiences; `Implementation plan.md` defines sequencing; `tracker.md` records actual verified state. If specs disagree, record the conflict and request a decision rather than silently changing scope.
4. **Deliver vertical slices.** For each feature include usable responsive UI, authenticated API if needed, persistent data and migrations if needed, server-side authorization, input validation, loading/empty/error states, tests, accessibility and user-visible truthful integration status.
5. **Never fabricate real-world outputs.** An AI model cannot generate verified live flight fares, rail seats, hotel rooms, provider confirmation numbers, refunds, transactions, weather warnings or official emergency dispatch. Only provider-confirmed data permits those claims. Test fixtures are labeled and cannot appear as live production results.
6. **Respect provider capability.** `PLANNING_ONLY`, `REDIRECT`, `LIVE_SEARCH_EXTERNAL_CHECKOUT`, `DIRECT_BOOKING` are distinct. A redirect or checkout click is **not** an internal confirmed booking; integration availability depends on authorized credentials and signed/compliant terms.
7. **Protect users.** Enforce object-level authorization and RBAC on every protected endpoint; secure secrets, sensitive documents, contact details and future location data; default location access off; no automatic paid modifications, cancellations or SOS claims.
8. **Verify before completion.** Run tests and report exact commands/results, including failures. Do not set a tracker item to `Done` without measurable evidence; never mark integration or legal/commercial approval completed unless independently verified.
9. **Update the specs.** When a user approves a product/technical decision, update `memory.md` decision log plus affected specs and `tracker.md`. Keep decision history concise and preserve earlier entries.
10. **Keep progress honest.** End every work session with: implemented items, tested evidence, blockers, external approval/credentials needed, docs updated and next approved item. No hidden background work.

## Stage boundary

- **Build now (Partition 1):** accounts; saved travellers; destination discovery; flight/train/bus/cab and own-vehicle discovery; stay comparison; restaurants/activities; trip wizard; real server-backed AI itinerary and manual fallback; deterministic cost breakdown; editing; saved trips; unified hub; authorized partner booking pathways where available; basic payment-status handling only where actually integrated; emergency information; notifications; support/admin essentials.
- **Not in first deployment (Partition 2):** expanded direct booking and cancellation/refunds, full group roles/collaboration, consent-based family monitoring, live tracking, secure booking-documents workspace, advanced partner/admin operations.
- **Long-term (Partition 3):** automated intelligent recovery, Back-to-Home, SOS workflow, advanced assistant, expense splitting, offline wallet, public trips/community, partner marketplace, premium plans.

## Permanent Git Workflow & Quality Protocol

### Branch Protection & Authorization
- **Working Branch:** `integration/rehan-backend`
- **Main Branch (`main`):** PROTECTED.
  - Never push directly to `main`.
  - Never merge into `main` without explicit approval.
  - Never delete remote branches.
  - Never rewrite Git history.
  - Never force push (`--force` or `+branch`).
- **Authorization:** Only completed, tested work may be committed and pushed to `integration/rehan-backend`.

### Pre-Implementation Checklist
1. Run `git status` to verify working tree.
2. Confirm active branch is `integration/rehan-backend`.
3. Fetch latest GitHub changes (`git fetch origin`).
4. Check whether local branch is ahead or behind.
5. Preserve uncommitted changes safely (stash or targeted work); never use destructive commands like `git reset --hard` or `git clean -fd`.
6. Never blindly merge `main` into working branch. Explain conflicts carefully if any occur.

### Post-Implementation Verification & Commit Protocol
1. **Backend Tests:** Run full test suite with active database: `mvn test -DDB_PASSWORD="..."`. All tests must pass (100%).
2. **Frontend Build:** Run `npm run build` or `npx tsc --noEmit` to verify type safety and compilation.
3. **Secret Leak Prevention:** Inspect `git status` and `git diff`. Verify NO `.env`, credentials, secrets, database dumps, logs, or build artifacts (`target/`, `.next/`) are staged.
4. **Targeted Staging:** Stage only explicit, intended source and config files.
5. **Descriptive Commit:** Write conventional, clear commit message documenting scope and tests.
6. **Push:** Push cleanly to `integration/rehan-backend`. If browser OAuth/credential helper prompt appears, request user authentication.

### Anti-Crash & Duplicate Prevention Protocol
Before adding code or dependencies:
- **Duplicate Classes:** Check if an equivalent Java class exists in `backend/src/main/java/com/ghoomi/`.
- **Duplicate Components:** Check if an equivalent React component exists in `frontend/src/`.
- **Duplicate API Endpoints:** Check existing controllers before creating new mapping annotations.
- **Duplicate Migrations:** Check all applied Flyway migrations. Never edit V1 or V2; only add incremented migration files when strictly necessary.
- **Database Safety:** NEVER DROP, TRUNCATE, or reset the database. Preserve user data and historical records.
- **Port Conflicts:** Ensure backend uses 8080 and frontend uses 3000.
- **Dependencies:** Inspect `pom.xml` and `package.json` before adding any new library.

## First instruction to an agent

> Read `AGENTS.md`, `rules.md`, `memory.md`, and `tracker.md`; inspect the existing code without modifying it. Then propose the smallest approved Partition 1 implementation slice with related PRD/flow/API/schema IDs, a test plan and any provider blockers. Implement it after the task is clear, and update the tracker only with verified results.
