import { IsoDate } from "../utils/dates";
import { CustomValues } from "./CustomField";
import { Step } from "./Interaction";

export type OpportunityStatus = 'OPEN' | 'WON' | 'LOST';

export const OPPORTUNITY_STATUS_LABELS: Record<OpportunityStatus, string> = {
    OPEN: 'Open',
    WON: 'Won',
    LOST: 'Lost',
};

export interface OpportunityContact {
    id: number,
    firstName: string,
    lastName: string,
}

export interface Opportunity {
    id: number,
    name: string,
    company: { id: number, name: string } | null,
    value: number,
    status: OpportunityStatus,
    closeDate: IsoDate | null,
    /** Sorted by last name; may be empty. */
    contacts: OpportunityContact[],
    customValues: CustomValues,
    /** The most advanced Step among its Interactions; computed, never sent back. */
    step: Step | null,
}

export interface OpportunityRequest {
    name: string,
    companyId: number | null,
    value: number,
    status: OpportunityStatus,
    closeDate: IsoDate | null,
    contactIds: number[],
    customValues: CustomValues,
}

export function contactFullName(contact: OpportunityContact): string {
    return `${contact.firstName} ${contact.lastName}`.trim();
}
