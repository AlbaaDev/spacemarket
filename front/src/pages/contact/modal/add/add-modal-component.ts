import { ChangeDetectionStrategy, Component, inject } from "@angular/core";
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from "@angular/forms";
import { MatButtonModule } from "@angular/material/button";
import { provideNativeDateAdapter } from "@angular/material/core";
import { MatDatepickerModule } from "@angular/material/datepicker";
import { MatDialogModule } from "@angular/material/dialog";
import { MatFormField, MatInputModule } from "@angular/material/input";
import { ContactService } from "../../../../services/contact/contact.service";
import { dateValidator } from "../../../../validators/DateValidator";

@Component({
  selector: 'add-contact-modal',
  templateUrl: 'add-contact-modal.html',
  styleUrl: 'add-contact-modal.css',
  providers: [provideNativeDateAdapter()],
  imports: [MatDialogModule, MatButtonModule, MatInputModule, MatFormField, ReactiveFormsModule, MatDatepickerModule, MatInputModule, MatDatepickerModule],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AddContactModal {
  private readonly contactService = inject(ContactService);
  private readonly formBuilder = inject(FormBuilder);
  private readonly _currentYear = new Date().getFullYear();
  private readonly _currentMonth = new Date().getMonth();
  private readonly _currentDay = new Date().getDay();
  
  readonly maxDate = new Date();

  contactAddForm: FormGroup = this.formBuilder.group({
    firstName: ['', [Validators.required]],
    lastName: ['', [Validators.required]],
    email: ['', [Validators.email, Validators.required]],
    phone: ['', Validators.required],
    birthDate: ['', [Validators.required, dateValidator]],
    city: ['', Validators.required],
    adress: ['', Validators.required],
    country: ['', Validators.required]
  });

  get email() {
    return this.contactAddForm.get('email');
  }

  get firstName() {
    return this.contactAddForm.get('firstName');
  }

  get lastName() {
    return this.contactAddForm.get('lastName');
  }

  get phone() {
    return this.contactAddForm.get('phone');
  }

  get birthDate() {
    return this.contactAddForm.get('birthDate');
  }

  get city() {
    return this.contactAddForm.get('city');
  }

  get adress() {
    return this.contactAddForm.get('adress');
  }

  get country() {
    return this.contactAddForm.get('country');
  }

  onSubmitContactForm() {
    this.contactService.addContact(this.contactAddForm.value).subscribe({
      next: (contact) => {
      },
      error: (error) => {
        console.error('Error while adding contact', error);
      }
    });
  }

  getErrorMessage(val: string): string {
    const regexPattern = /^(0?[1-9]|1[0-2])\/(0?[1-9]|[12][0-9]|3[01])\/\d{4}$/;
    const value = val.replace(/\s+/g, '');
    const isValid = regexPattern.test(value);
    const date = new Date(value);
    if (!isValid && val !== '') {
      return 'Date of Birth must in the format MM/DD/YYYY';
    } else if (date < this.maxDate) {
      return 'Date must be before the current date';
    }
    return '';
  }
}