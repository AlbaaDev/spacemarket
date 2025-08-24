import { AbstractControl } from "@angular/forms";

export function dateValidator(control: AbstractControl): { [key: string]: boolean } | null {
    const regexPattern = /^(0?[1-9]|1[0-2])\/(0?[1-9]|[12][0-9]|3[01])\/\d{4}$/;
    const val = control.value;
    let dateStr: string;
    if (val instanceof Date) {
        const month = (val.getMonth() + 1).toString().padStart(2, '0');
        const day = val.getDate().toString().padStart(2, '0');
        const year = val.getFullYear().toString();
        dateStr = `${month}/${day}/${year}`;
    } else {
        dateStr = val;
    }
    const isValid = regexPattern.test(dateStr);
    const dateInput = new Date(dateStr);
    const currentDate = new Date();
    if (!isValid) {
        return { wrongFormat: true };
    } else if (dateInput > currentDate) {
        return { maxDAte: true }
    }
    return null;
}