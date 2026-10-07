# Product

<!-- impeccable:product-schema 1 -->

## Platform

web

## Users
Freelancers and solo operators who run their own client relationships. They track clients, deals, documents and their calendar themselves, without a sales team or a CRM admin.

## Product Purpose
SpaceMarket is a CRM that keeps contacts, companies, opportunities, calendar, documents, workflow and reporting in one lightweight tool. Success is a solo operator managing their whole pipeline in one place, without juggling separate tools.

## Positioning
A simple, all-in-one CRM sized for one person, as opposed to team-oriented CRMs such as HubSpot or Pipedrive.

## Operating Context
Authenticated web app (JWT login, sign-up, password recovery). Primary screens: dashboard, contacts (with detail), companies (with detail), opportunities, calendar, workflow, reporting, documents, profile and settings. A public home page exists.

## Capabilities and Constraints
- Frontend: Angular 20 with Angular Material and Bootstrap 5 (`front/`).
- Backend: Java 25, Spring Boot 3.5.3, PostgreSQL (`back/`, `back-utils/`).
- French-language routes and labels are present (e.g. `calendrier`); UI language policy is undecided.
- Deployment pipelines exist for Azure (`azure-pipelines-back.yml`, `azure.pipelines-front.yml`).
- Whether the "Space" name is a binding brand metaphor is undecided.

## Evidence on Hand
- `preview.png` at the repo root shows the current UI.
- No testimonials, customers, benchmarks or pricing exist; future work must not fabricate them.

## Product Principles
- Built for one person: every screen should be usable without setup, roles or team concepts.
- One tool, whole pipeline: contacts, companies, deals, calendar and documents stay connected rather than siloed.
- Simplicity over feature breadth: prefer fewer, clearer actions to configurable complexity.

## Accessibility & Inclusion
No product-specific requirement established.
