# SpaceMarket

A one-person CRM: a solo operator tracks the people and companies they do business with, the deals they pursue, and the exchanges they have along the way.

## Language

### Relationships

**Contact**:
A person the user does business with.
_Avoid_: Lead, client, customer, person

**Company**:
An organization one or more Contacts belong to.
_Avoid_: Account, organization, business

**Interaction**:
A dated exchange between the user and a Contact (a call, an email, a meeting or a message).
_Avoid_: Activity, touchpoint, contact (as a verb or event)

**Interaction type**:
The fixed kind of an Interaction: Call, Email, Meeting or Message.
_Avoid_: Channel, category

**Contacts reached**:
The number of distinct Contacts with at least one Interaction in a Period.
_Avoid_: Contacts made, contacts done

### Deals

**Opportunity**:
A potential deal with a monetary value, optionally for a Company and with any number of Contacts, none included.
_Avoid_: Deal, lead, sale, principal Contact, business name

**Opportunity status**:
Where an Opportunity stands: Open, Won or Lost.
_Avoid_: Stage, state

**Step**:
How far an Opportunity's selection process has gone, in a fixed order: HR, Business manager, Client interview, Client technical interview. An Interaction linked to an Opportunity may record the Step it covers; an Opportunity's Step is the most advanced Step among its Interactions.
_Avoid_: Stage, round, interview type

**Close date**:
The date an Opportunity became Won or Lost.
_Avoid_: End date, closing date

**Revenue**:
The summed value of Opportunities Won with a Close date inside a Period.
_Avoid_: Sales, income, turnover

### Customization

**Custom field**:
A user-defined attribute added to Contacts, Companies or Opportunities, with a type (Text, Number, Date, Single choice or Checkbox).
_Avoid_: Custom column, extra field, property, attribute

**Column**:
A Custom field or built-in attribute shown in a table; showing, hiding or reordering one does not change the data.
_Avoid_: Field (when only display is meant)

**Search**:
Free text typed above a table that narrows it to the rows whose visible Columns contain every word typed.
_Avoid_: Filter, query, lookup

### Reporting

**Period**:
A user-chosen date range, with an explicit start and end, over which dashboard figures are computed.
_Avoid_: Timeframe, window
