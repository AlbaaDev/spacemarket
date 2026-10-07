import { IsoDate } from "../utils/dates";

export type InteractionType = 'CALL' | 'EMAIL' | 'MEETING' | 'MESSAGE';

export const INTERACTION_TYPES: { value: InteractionType, label: string, icon: string }[] = [
    { value: 'CALL', label: 'Call', icon: 'call' },
    { value: 'EMAIL', label: 'Email', icon: 'mail' },
    { value: 'MEETING', label: 'Meeting', icon: 'groups' },
    { value: 'MESSAGE', label: 'Message', icon: 'chat' },
];

export interface Interaction {
    id: number,
    type: InteractionType,
    occurredOn: IsoDate,
    note: string | null,
    contactId: number,
    opportunityId: number | null,
}

export interface InteractionRequest {
    type: InteractionType,
    occurredOn: IsoDate,
    note: string | null,
    opportunityId: number | null,
}
