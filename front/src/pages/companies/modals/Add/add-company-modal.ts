import { AsyncPipe } from "@angular/common";
import { ChangeDetectionStrategy, Component, inject } from "@angular/core";
import { FormArray, FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from "@angular/forms";
import { MatAutocomplete, MatAutocompleteModule } from "@angular/material/autocomplete";
import { MatButtonModule } from "@angular/material/button";
import { MatOption, provideNativeDateAdapter } from "@angular/material/core";
import { MatDatepickerModule } from "@angular/material/datepicker";
import { MatDialogModule } from "@angular/material/dialog";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatIcon } from "@angular/material/icon";
import { MatFormField, MatInputModule } from "@angular/material/input";
import { map, Observable, startWith } from "rxjs";
import { Company } from "../../../../interfaces/Company";
import { Contact } from "../../../../interfaces/Contact";
import { CompanyService } from "../../../../services/company/company.service";
import { ContactService } from "../../../../services/contact/contact.service";

@Component({
  selector: 'add-company-modal',
  templateUrl: 'add-company-modal.html',
  styleUrl: 'add-company-modal.css',
  providers: [provideNativeDateAdapter()],
  imports: [AsyncPipe, FormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatAutocompleteModule,
    ReactiveFormsModule, MatDialogModule, MatButtonModule, MatInputModule,
    MatFormField, ReactiveFormsModule, MatDatepickerModule, MatInputModule, MatDatepickerModule, MatIcon, MatAutocomplete, MatOption],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AddCompanyModal {
  private readonly companyService = inject(CompanyService);
  private readonly contactService = inject(ContactService);
  private readonly formBuilder = inject(FormBuilder);
  readonly maxDate = new Date();

  private readonly allContacts: Contact[] = this.contactService.contacts();
  private readonly allCompanies: Company[] = this.companyService.companies();

  filteredContacts: Observable<Contact[]>[] = [];
  filteredCompanies: Observable<Company[]>[] = [];

  protected readonly companyAddForm: FormGroup = this.formBuilder.group({
    name: ['', [Validators.required]],
    city: [''],
    address: [''],
    country: [''],
    contacts: this.formBuilder.array([
      this.createContactFormGroup()
    ]),
    companies: this.formBuilder.array([
      this.createCompanyFormGroup()
    ]),
    industry: [''],
  });

  constructor() {
    this.addFilterForIndex(0);
  }

  private addFilterForIndex(index: number): void {
    const contactControl = this.contacts.at(index).get('contact')!;
    this.filteredContacts[index] = contactControl.valueChanges.pipe(
      startWith(''),
      map(value => this._filterContacts(typeof value === 'string'
        ? value
        : value?.firstName ?? ''))
    );

    const companyControl = this.companies.at(index).get('company')!;
    this.filteredCompanies[index] = companyControl.valueChanges.pipe(
      startWith(''),
      map(value => this._filterCompanies(typeof value === 'string'
        ? value
        : value?.name ?? ''))
    );
  }

  private _filterContacts(value: string): Contact[] {
    // if (!value) { return this.allContacts; }
    const search = value.toLowerCase();
    return this.allContacts.filter(c =>
      c.firstName.toLowerCase().includes(search) ||
      c.lastName.toLowerCase().includes(search)
    );
  }

  private _filterCompanies(value: string): Company[] {
    // if (!value) { return this.allCompanies; }
    const search = value.toLowerCase();
    return this.allCompanies.filter(c =>
      c.name.toLowerCase().includes(search) ||
      c.industry.toLowerCase().includes(search)
    );
  }

  displayContact(contact: Contact): string {
    return contact ? `${contact.firstName} ${contact.lastName}` : '';
  }

  displayCompany(company: Company): string {
    return company ? company.name : '';
  }

  get city() {
    return this.companyAddForm.get('city');
  }

  get address() {
    return this.companyAddForm.get('address');
  }

  get country() {
    return this.companyAddForm.get('country');
  }

  get contacts(): FormArray {
    return this.companyAddForm.get('contacts') as FormArray;
  }

  get companies(): FormArray {
    return this.companyAddForm.get('companies') as FormArray;
  }

  createContactFormGroup(): FormGroup {
    return this.formBuilder.group({
      contact: [''],
    });
  }

  createCompanyFormGroup(): FormGroup {
    return this.formBuilder.group({
      company: [''],
    });
  }

  addContact(): void {
    const index = this.contacts.length;
    this.contacts.push(this.createContactFormGroup());
    this.addFilterForIndex(index);
  }

  addCompany(): void {
    const index = this.companies.length;
    this.companies.push(this.createCompanyFormGroup());
    this.addFilterForIndex(index);
  }

  removeContact(index: number): void {
    if (this.contacts.length > 1) {
      this.contacts.removeAt(index);
      this.filteredContacts.splice(index, 1);
    }
  }

  removeCompany(index: number): void {
    if (this.companies.length > 1) {
      this.companies.removeAt(index);
      this.filteredCompanies.splice(index, 1);
    }
  }

  onSubmitCompanyForm(): void {
    if (this.companyAddForm.invalid) return;
    const formValue = { ...this.companyAddForm.value };
    formValue.contacts = formValue.contacts
      .map((c: any) => c.contact)
      .filter((c: Contact) => c && c.id);
    this.companyService.addCompany(formValue).subscribe({
      next: (company) => { },
      error: (error) => { }
    });
  }
}