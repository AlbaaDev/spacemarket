# Liquibase owns the schema

The schema used to follow the JPA entities through Hibernate `ddl-auto=update`, which adds tables and columns but never drops a column, relaxes a `NOT NULL` or moves data, so a change like removing an Opportunity's principal Contact cannot be made on an existing database. Liquibase now owns the schema: every change is a changeset in `back/src/main/resources/db/changelog/`, run on startup through `spring-boot-starter-liquibase`, and Hibernate only checks the entities against it (`ddl-auto=validate`). We chose Liquibase over Flyway because its changesets describe changes in a database-independent format, which keeps a move away from PostgreSQL possible.

## Considered Options

- **Flyway**: plain SQL migrations, simpler to learn, but each one is written for a single database.
- **Manual SQL scripts next to `ddl-auto=update`**: no new tool, but nothing records which script ran on which database.

## Consequences

- Changesets use Liquibase change types (YAML), not raw SQL, unless a change has no portable form; that is the only way the database-independence argument holds.
- The first changeset reproduces the current schema. The existing development and Azure databases are marked as already at that point (`changelog-sync`) instead of replaying it. The changelog left over from the earlier Liquibase attempt (August 2025) no longer matches the entities and is replaced.
- The demo seed in `data.sql`, loaded by the `demo` profile of `TestBackApplication`, becomes a changeset with the `demo` context, since Spring Boot advises against mixing `data.sql` with a migration tool.
- Repository and reporting tests run the changelog against their Testcontainers PostgreSQL, so the schema is tested too.
- Changing database is still more than changing the changelog: Custom field values rely on PostgreSQL `jsonb` (ADR 0001).
- Since 5.0, Liquibase Community is under the Functional Source License rather than Apache 2.0. Using it in production stays free; each release becomes Apache 2.0 two years after it ships.
