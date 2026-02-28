import { AsyncPipe } from "@angular/common";
import { ChangeDetectionStrategy, Component, inject } from "@angular/core";
import { FormArray, FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from "@angular/forms";
import { MatAutocomplete, MatAutocompleteModule } from "@angular/material/autocomplete";
import { MatButtonModule } from "@angular/material/button";
import { MatOption, provideNativeDateAdapter } from "@angular/material/core";
import { MatDatepickerModule } from "@angular/material/datepicker";
import { MatDialogModule, MatDialogRef } from "@angular/material/dialog";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatIcon } from "@angular/material/icon";
import { MatFormField, MatInputModule } from "@angular/material/input";
import { map, Observable, startWith } from "rxjs";
import { Company } from "../../../../interfaces/Company";
import { CompanyService } from "../../../../services/company/company.service";
import { ContactService } from "../../../../services/contact/contact.service";
import { Router } from "@angular/router";

@Component({
  selector: 'add-contact-modal',
  templateUrl: 'add-contact-modal.html',
  styleUrl: 'add-contact-modal.css',
  providers: [provideNativeDateAdapter()],
  imports: [AsyncPipe, FormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatAutocompleteModule,
    ReactiveFormsModule, MatDialogModule, MatButtonModule, MatInputModule,
    MatFormField, ReactiveFormsModule, MatDatepickerModule, MatInputModule, MatDatepickerModule, MatIcon, MatAutocomplete, MatOption],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AddContactModal {
  private readonly dialogRef = inject(MatDialogRef<AddContactModal>);
  private readonly contactService = inject(ContactService);
  private readonly companyService = inject(CompanyService);
  private readonly formBuilder = inject(FormBuilder);
  private readonly router = inject(Router);

  readonly maxDate = new Date();

  companies: Company[] = this.companyService.companies();
  filteredCompanies: Observable<Company[]>;

  constructor() {
    this.filteredCompanies = this.contactAddForm.get('company')!.valueChanges.pipe(
      map(value => this._filter(value || '')),
    );
  }

  private _filter(value: any): Company[] {
    const search = typeof value === 'string' ? value : value?.name ?? '';
    // if (!search) return this.companyService.companies();
    return this.companyService.companies().filter(c =>
      c.name.toLowerCase().includes(search.toLowerCase())
    );
  }

  protected readonly contactAddForm: FormGroup = this.formBuilder.group({
    firstName: ['', [Validators.required]],
    lastName: ['', [Validators.required]],
    city: [''],
    address: [''],
    country: [''],
    company: [''],
    emails: this.formBuilder.array([
      this.createEmailFormGroup()
    ]),
    phones: this.formBuilder.array([
      this.createPhoneFormGroup()
    ])
  });
  get firstName() {
    return this.contactAddForm.get('firstName');
  }

  get lastName() {
    return this.contactAddForm.get('lastName');
  }

  get city() {
    return this.contactAddForm.get('city');
  }

  get company() {
    return this.contactAddForm.get('company');
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

  displayCompany(c: Company): string { return c?.name ?? ''; }


  onSubmitContactForm() {
    if (this.contactAddForm.invalid) {
      return;
    }
    const formValue = { ...this.contactAddForm.value };
    if (typeof formValue.company === 'string' && formValue.company.trim()) {
      formValue.company = { name: formValue.company.trim() };
    } else if (!formValue.company?.id) {
      formValue.company = null;
    }
    formValue.emails = formValue.emails.filter((element: any) =>
      element.email && element.email.trim() !== ''
    );
    formValue.phones = formValue.phones.filter((element: any) =>
      element.phone && element.phone.trim() !== ''
    );
    if (!formValue.company || typeof formValue.company === 'string' && !formValue.company.trim()) {
      formValue.company = null;
    }
    this.contactService.addContact(formValue).subscribe({
      next: (contact) => {
      },
      error: (error) => {
      }
    });
  }

  goToPageCompanies() {
    this.dialogRef.close();
    this.router.navigate(['/companies']);
  }

  noCompanyFound(): boolean {
    const value = this.company?.value;
    if (!value || typeof value !== 'string') return false;
    return this.companyService.companies().filter(c =>
        c.name.toLowerCase().includes(value.toLowerCase())
    ).length === 0;
  }
}