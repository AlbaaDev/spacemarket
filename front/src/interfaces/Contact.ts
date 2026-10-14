import { ContactEmail } from "./ContactEmail";
import { ContactPhone } from "./ContactPhone";
import { CustomValues } from "./CustomField";

export interface Contact { 
    id: number,
    firstName: string;
    lastName: string;
    emails: ContactEmail[];
    phones: ContactPhone[];
    city: string;
    address: string;
    country: string;
    company?: { id: number, name: string } | null;
    customValues?: CustomValues;
}

export type ContactKeys = 'firstName' | 'lastName' | 'emails' | 'phones' | 'city' | 'address' | 'country';
