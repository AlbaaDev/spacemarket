export interface Contact { 
    firstName: string;
    lastName: string;
    email: string;
    phoneNumber: string;
    birthDay: string;
    city: string;
    adress: string;
    country: string;
}

export type ContactKeys = 'firstName' | 'lastName' | 'email' | 'phoneNumber' | 'birthDay' | 'city' | 'adress' | 'country';
