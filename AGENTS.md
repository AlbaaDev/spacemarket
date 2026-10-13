# SpaceMarket

A one-person CRM: a freelancer tracks their contacts, companies, opportunities and the interactions around them in one place. The Spring Boot API lives in `back/`, the Angular app in `front/`.

## Commands

- Backend tests: `./mvnw test` from `back/`. Repository and reporting tests start PostgreSQL through Testcontainers, so Docker has to be running.
- Frontend tests: `npx ng test --watch=false --browsers=ChromeHeadless` from `front/`. Without Google Chrome installed, set `CHROME_BIN` to another Chromium browser.
- Frontend type check, templates included: `npm run build:dev` from `front/`.

## Database

The schema follows the JPA entities through Hibernate `ddl-auto=update`; there are no migrations. Development seed data is in `data.sql`.

## Language and decisions

Name things with the terms in `GLOSSARY.md`, in code, UI copy and pull requests. Past architecture decisions are in `docs/adr/`. For UI work, `PRODUCT.md` describes the users and `DESIGN.md` the design system.
