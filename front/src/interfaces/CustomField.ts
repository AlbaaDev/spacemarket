export type CustomFieldTarget = 'CONTACT' | 'COMPANY' | 'OPPORTUNITY';
export type CustomFieldType = 'TEXT' | 'NUMBER' | 'DATE' | 'SINGLE_CHOICE' | 'CHECKBOX';

export const CUSTOM_FIELD_TYPES: { value: CustomFieldType, label: string, icon: string }[] = [
    { value: 'TEXT', label: 'Text', icon: 'short_text' },
    { value: 'NUMBER', label: 'Number', icon: 'tag' },
    { value: 'DATE', label: 'Date', icon: 'event' },
    { value: 'SINGLE_CHOICE', label: 'Single choice', icon: 'arrow_drop_down_circle' },
    { value: 'CHECKBOX', label: 'Checkbox', icon: 'check_box' },
];

export interface CustomField {
    id: number,
    name: string,
    target: CustomFieldTarget,
    type: CustomFieldType,
    options: string[],
}

export interface CustomFieldRequest {
    name: string,
    target: CustomFieldTarget,
    type: CustomFieldType,
    options: string[],
}

/** Values of a record's Custom fields, keyed by field id. Dates are YYYY-MM-DD. */
export type CustomValues = Record<string, string | number | boolean>;
