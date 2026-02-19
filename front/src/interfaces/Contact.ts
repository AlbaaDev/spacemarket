export interface Contact { 
    id: number,
    firstName: string;
    lastName: string;
    email: string;
    phone: string;
    birthDate: Date;
    city: string;
    address: string;
    country: string;
}

export type ContactKeys = 'firstName' | 'lastName' | 'email' | 'phone' | 'birthDate' | 'city' | 'address' | 'country';
