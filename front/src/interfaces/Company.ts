import { Contact } from "./Contact";
import { CustomValues } from "./CustomField";

export interface Company { 
    id: number,
    name: string;
    contacts: Contact[];
    city: string;
    address: string;
    country: string;
    industry : string;
    customValues?: CustomValues;
}

export type CompanyKeys = 'name' | 'city' |  'address' | 'country' | 'industry';
