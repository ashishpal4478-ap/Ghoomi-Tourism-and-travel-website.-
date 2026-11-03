# GHOOMI — Agent-ready product specification pack

**Version:** 1.1 — MySQL edition | **Prepared:** 2026-11-03 | **Current build target:** Partition 1 — before first deployment.

This folder turns the uploaded *Ghoomi working platform Idea explanation* (53-page master) into a source-aligned, actionable project documentation set. All proposed architecture, design colors, numerical targets and task estimates are **recommendations**, not evidence of implemented features, production readiness or active provider partnerships.

## Files

| File | Read for |
|---|---|
| `AGENTS.md` | Automatic repository-wide execution policy for AI coding agents |
| `rules.md` | Binding safety, scope, quality and provider truthfulness constraints |
| `memory.md` | Durable project decisions, assumptions, unknowns and handoff notes |
| `tracker.md` | Feature/task progress, evidence, blockers and release readiness |
| `prd.md` | Product requirements, acceptance criteria, priorities and phases |
| `App Flow.md` | Screen inventory, Mermaid flows, routes, states and exception journeys |
| `UI-UX design Brief.md` | Design tokens, responsive components, detailed page/interaction UX |
| `trd.md` | Proposed stack, architecture, provider adapters, APIs, NFR and security |
| `Backend Schema.md` | Relational data model, sample MySQL 8.4 DDL, indexes, migrations |
| `Implementation plan.md` | Phased implementation checklist, dependencies and gates |
| `database/mysql/V1__ghoomi_partition1_reference.sql` | Reference MySQL 8.4 LTS DDL for reviewed Flyway migration (not yet executed) |
| `reference/Ghoomi_Working_Platform_Master.pdf` | Original source of product partitioning and feature inventory |

## Start coding

1. Put this folder's files at your repository root (including `AGENTS.md`) and open the root in Antigravity, Codex or another compatible agent.
2. Ask the agent to inspect the existing repository; don't assume it is empty.
3. Respect the confirmed **MySQL** database choice; resolve remaining essential decisions in `memory.md` (other stack components if new repo, initial provider modes, launch market).
4. Start the next incomplete `IMP-...` item in `tracker.md`; work in small tested slices.
5. Never mark tickets or a release gate passed without evidence. `tracker.md` starts entirely unverified by design.

### Suggested first prompt

> Read `AGENTS.md`, `rules.md`, `memory.md`, `tracker.md`, and the Partition 1 sections of `prd.md`, `trd.md`, `Backend Schema.md`, `App Flow.md`, `UI-UX design Brief.md`, and `Implementation plan.md`. Inspect my existing codebase first. Tell me what already exists, any gaps/conflicts, the first approved P0 slice and its test plan; then implement the smallest complete slice. Update the tracker with measured evidence only. Do not fabricate integrations, live prices, bookings or payments.

## Naming and IDs

- `GHO-P1-...`, `GHO-P2-...`, `GHO-P3-...`: user-facing requirement IDs by partition.
- `IMP-...`: implementation tasks and gates.
- `API-...`: API contract group.
- `DB-...`: data entity/behavior.
- `ADR-...`: architecture decision record in `memory.md`.

**Scope note:** The master names 55 initial pages/modules and 22 additional pages/modules each for Partitions 2 and 3. These are inventory entries for design coverage, **not** a requirement to build 99 independent pages or deploy all integrations at once.
