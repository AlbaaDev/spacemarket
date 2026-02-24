export class ContactComponent {
  readonly columns = {
    firstName: 'First name',
    lastName: 'Last name',
    email: 'Emails',
    phone: 'Phones',
    city: 'City',
    address: 'Address',
    country: 'Country'
  } as const;
}