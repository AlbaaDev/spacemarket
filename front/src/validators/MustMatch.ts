import { AbstractControl, ValidationErrors, ValidatorFn } from "@angular/forms";

export function MustMatch(controlName: string, matchingControlName: string): ValidatorFn {
  return (controls: AbstractControl): ValidationErrors | null => {
    const control = controls.get(controlName);
    const matchingControl = controls.get(matchingControlName);

    if (matchingControl?.errors && !matchingControl?.errors['mustMatch']) {
      return null;
    }
    if (control?.value !== matchingControl?.value) {
      control?.setErrors({ mustMatch: true });
      matchingControl?.setErrors({ mustMatch: true });
      return { mustMatch: true };
    } else {
      control?.setErrors(null);
      matchingControl?.setErrors(null);
      return null;
    }
  };
}
