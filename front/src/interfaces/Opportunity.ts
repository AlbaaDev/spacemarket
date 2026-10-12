import { IsoDate } from "../utils/dates";
import { CustomValues } from "./CustomField";
import { Step } from "./Interaction";

export type OpportunityStatus = 'OPEN' | 'WON' | 'LOST';

export const OPPORTUNITY_STATUS_LABELS: Record<OpportunityStatus, string> = {
    OPEN: 'Open',
    WON: 'Won',
    LOST: 'Lost',
};

export interface Opportunity {
    id: number,
    name: string,
    businessName: string,
    value: number,
    status: OpportunityStatus,
    closeDate: IsoDate | null,
    principalContact: { id: number, firstName: string, lastName: string },
    customValues: CustomValues,
    /** The most advanced Step among its Interactions; computed, never sent back. */
    step: Step | null,
}

export interface OpportunityRequest {
    name: string,
    businessName: string,
    value: number,
    status: OpportunityStatus,
    closeDate: IsoDate | null,
    principalContactId: number,
    customValues: CustomValues,
}
