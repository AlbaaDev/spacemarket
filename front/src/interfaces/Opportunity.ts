import { Contact } from "./Contact";

export interface Opportunity {
    id?: number,
    name : string,
    businessName : string,
    value: number,
    principalContact: string,
    contacts: Contact[],
}