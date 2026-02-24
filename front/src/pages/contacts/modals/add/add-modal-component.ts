import { ChangeDetectionStrategy, Component, inject } from "@angular/core";
import { FormArray, FormBuilder, FormGroup, ReactiveFormsModule, Validators } from "@angular/forms";
import { MatButtonModule } from "@angular/material/button";
import { MatOption, provideNativeDateAdapter } from "@angular/material/core";
import { MatDatepickerModule } from "@angular/material/datepicker";
import { MatDialogModule } from "@angular/material/dialog";
import { MatIcon } from "@angular/material/icon";
import { MatFormField, MatInputModule } from "@angular/material/input";
import { ContactService } from "../../../../services/contact/contact.service";

@Component({
  selector: 'add-contact-modal',
  templateUrl: 'add-contact-modal.html',
  styleUrl: 'add-contact-modal.css',
  providers: [provideNativeDateAdapter()],
  imports: [MatDialogModule, MatButtonModule, MatInputModule, MatFormField, ReactiveFormsModule, MatDatepickerModule, MatInputModule, MatDatepickerModule, MatIcon, MatOption],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AddContactModal {
  private readonly contactService = inject(ContactService);
  private readonly formBuilder = inject(FormBuilder);
  readonly maxDate = new Date();

  protected readonly contactAddForm: FormGroup = this.formBuilder.group({
    firstName: ['', [Validators.required]],
    lastName: ['', [Validators.required]],
    city: [''],
    address: [''],
    country: [''],
    emails: this.formBuilder.array([
      this.createEmailFormGroup()
    ]),
    phones: this.formBuilder.array([
      this.createPhoneFormGroup()
    ])
  });
  toastr: any;
  router: any;

  get firstName() {
    return this.contactAddForm.get('firstName');
  }

  get lastName() {
    return this.contactAddForm.get('lastName');
  }

  get city() {
    return this.contactAddForm.get('city');
  }

  get address() {
    return this.contactAddForm.get('address');
  }

  get country() {
    return this.contactAddForm.get('country');
  }

  get emails(): FormArray {
    return this.contactAddForm.get('emails') as FormArray;
  }

  get phones(): FormArray {
    return this.contactAddForm.get('phones') as FormArray;
  }

  newEmail() {
    this.emails.push(this.createEmailFormGroup());
  }

  removeEmail(index: number) {
    if (this.emails.length > 1) {
      this.emails.removeAt(index);
    }
  }

  newPhone() {
    this.phones.push(this.createPhoneFormGroup());
  }

  removePhone(index: number) {
    if (this.phones.length > 1) {
      this.phones.removeAt(index);
    }
  }

  createEmailFormGroup(): FormGroup {
    return this.formBuilder.group({
      email: ['', [Validators.email]],
      type: ['WORK'],
      isPrimary: [false]
    });
  }

  createPhoneFormGroup(): FormGroup {
    return this.formBuilder.group({
      phone: ['', [Validators.minLength(10), Validators.maxLength(12)]],
      type: ['WORK'],
      isPrimary: [false]
    });
  }

  onSubmitContactForm() {
    if (this.contactAddForm.invalid) {
      return;
    }
    const formValue = { ...this.contactAddForm.value };
    formValue.emails = formValue.emails.filter((element: any) =>
      element.email && element.email.trim() !== ''
    );
    formValue.phones = formValue.phones.filter((element: any) =>
      element.phone && element.phone.trim() !== ''
    );
    this.contactService.addContact(formValue).subscribe({
      next: (contact) => {
      },
      error: (error) => {
      }
    });
  }
}