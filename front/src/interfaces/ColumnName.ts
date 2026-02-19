export class ContactComponent {
  readonly columns = {
    firstName: 'First name',
    lastName: 'Last name',
    email: 'Email',
    phone: 'Phone number',
    birthDate: 'Birth date',
    city: 'City',
    address: 'Address',
    country: 'Country'
  } as const;
}