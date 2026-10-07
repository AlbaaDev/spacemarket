import { IsoDate } from "../utils/dates";
import { CustomValues } from "./CustomField";

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
