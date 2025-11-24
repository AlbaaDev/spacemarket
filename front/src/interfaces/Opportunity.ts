import { Contact } from "./Contact";

export interface Opportunity {
    name : string,
    businessName : string,
    value: number,
    principalContact: string,
    contacts: Contact[],
}