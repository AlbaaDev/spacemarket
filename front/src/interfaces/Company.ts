import { Contact } from "./Contact";

export interface Company { 
    id: number,
    name: string;
    contacts: Contact[];
    city: string;
    address: string;
    country: string;
    industry : string;
}

export type CompanyKeys = 'name' | 'city' |  'address' | 'country' | 'industry';
