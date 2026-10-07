# Custom field values live in a jsonb column

Custom field definitions are stored in their own `custom_field` table (name, type, options, target entity, owning user), but their values are stored in a `custom_values` jsonb column on `contact`, `company` and `opportunity` rather than in an entity-attribute-value table. Tables load and sort in a single query using PostgreSQL's JSON operators, with no join per field, which matters more for a one-person CRM than relational purity.

## Consequences

- PostgreSQL does not enforce value types; the backend validates each value against its field's type on write.
- Deleting a Custom field must also strip its key from every row's `custom_values`.
