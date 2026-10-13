# An Opportunity belongs to the user, not to a Contact

An Opportunity used to reach its user through a mandatory principal Contact and named its company in free text. A Contact could not be deleted while it was principal of an Opportunity, and the free-text name could disagree with the Companies the user keeps. An Opportunity now carries its own user, an optional Company and zero or more Contacts, all chosen when it is created; it outlives the deletion of any of them.

## Considered Options

- **Keep a mandatory principal Contact and block or cascade its deletion**: either the user cannot delete a Contact, or deleting one silently deletes Opportunities.
- **Rename the "Business name" label to "Company" and keep free text**: one word would mean both a Company record and text matching none.

## Consequences

- Every Opportunity query filters on the Opportunity's own user; assigning a Contact or a Company checks that it belongs to the same user.
- The Opportunity side owns the `contact_opportunity` join table, since Contacts are chosen from the Opportunity.
- Migrating existing data: each principal Contact becomes one of the Opportunity's Contacts, and each business name is linked to the user's Company of the same name, created when none exists.
- An Interaction still needs a Contact, so an Opportunity without Contacts cannot log Interactions, and its Step cannot move, until one is assigned.
