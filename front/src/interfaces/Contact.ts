export interface Contact { 
    id: number,
    firstName: string;
    lastName: string;
    email: string;
    phone: string;
    birthDate: string;
    city: string;
    adress: string;
    country: string;
}

export type ContactKeys = 'firstName' | 'lastName' | 'email' | 'phone' | 'birthDate' | 'city' | 'adress' | 'country';
