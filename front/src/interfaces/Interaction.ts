import { IsoDate } from "../utils/dates";

export type InteractionType = 'CALL' | 'EMAIL' | 'MEETING' | 'MESSAGE';

export const INTERACTION_TYPES: { value: InteractionType, label: string, icon: string }[] = [
    { value: 'CALL', label: 'Call', icon: 'call' },
    { value: 'EMAIL', label: 'Email', icon: 'mail' },
    { value: 'MEETING', label: 'Meeting', icon: 'groups' },
    { value: 'MESSAGE', label: 'Message', icon: 'chat' },
];

export type Step = 'HR' | 'BUSINESS_MANAGER' | 'CLIENT' | 'CLIENT_TECHNICAL';

/** In selection-process order: a later Step is a more advanced one. */
export const STEPS: { value: Step, label: string }[] = [
    { value: 'HR', label: 'HR' },
    { value: 'BUSINESS_MANAGER', label: 'Business manager' },
    { value: 'CLIENT', label: 'Client interview' },
    { value: 'CLIENT_TECHNICAL', label: 'Client technical interview' },
];

export function stepLabel(step: Step | null): string | undefined {
    return step === null ? undefined : STEPS.find(s => s.value === step)?.label ?? step;
}

export interface Interaction {
    id: number,
    type: InteractionType,
    occurredOn: IsoDate,
    note: string | null,
    contactId: number,
    opportunityId: number | null,
    step: Step | null,
}

export interface InteractionRequest {
    type: InteractionType,
    occurredOn: IsoDate,
    note: string | null,
    opportunityId: number | null,
    step: Step | null,
}

export interface OpportunityInteractionRequest {
    type: InteractionType,
    occurredOn: IsoDate,
    note: string | null,
    contactId: number,
    step: Step | null,
}
