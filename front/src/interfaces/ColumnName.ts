// export type ColumnName =
//     'firstName'
//     | 'lastName'
//     | 'email'
//     | 'phoneNumber'
//     | 'birthDay'
//     | 'city'
//     | 'adress'
//     | 'country'
export class ContactComponent {
  readonly columns = {
    firstName: 'Prénom',
    lastName: 'Nom',
    email: 'Email',
    phoneNumber: 'Téléphone',
    birthDay: 'Date de naissance',
    city: 'Ville',
    adress: 'Adresse',
    country: 'Pays'
  } as const;
}