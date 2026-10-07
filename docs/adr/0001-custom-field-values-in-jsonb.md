# Custom field values live in a jsonb column

Custom field definitions are stored in their own `custom_field` table (name, type, options, target entity, owning user), but their values are stored in a `custom_values` jsonb column on `contact`, `company` and `opportunity` rather than in an entity-attribute-value table. A record and all its values load in one row, with no join per field, which matters more for a one-person CRM than relational purity; sorting by a Custom field happens in the browser today, and PostgreSQL's JSON operators leave room to move it server-side.

## Consequences

- PostgreSQL does not enforce value types; the backend validates each value against its field's type on write.
- Deleting a Custom field, or removing one of its choices, must also strip the matching values from every row's `custom_values`.
