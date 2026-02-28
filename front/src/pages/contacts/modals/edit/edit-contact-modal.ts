import { ChangeDetectionStrategy, Component, inject, OnInit } from "@angular/core";
import { FormArray, FormBuilder, FormGroup, ReactiveFormsModule, Validators } from "@angular/forms";
import { MatButtonModule } from "@angular/material/button";
import { provideNativeDateAdapter, MatOption } from "@angular/material/core";
import { MatDatepickerModule } from "@angular/material/datepicker";
import { MAT_DIALOG_DATA, MatDialogModule } from "@angular/material/dialog";
import { MatIcon } from "@angular/material/icon";
import { MatInputModule } from "@angular/material/input";
import { ContactService } from "../../../../services/contact/contact.service";
import { MatAutocomplete, MatAutocompleteModule } from "@angular/material/autocomplete";
import { Company } from "../../../../interfaces/Company";
import { map, merge, Observable, startWith } from "rxjs";
import { AsyncPipe } from "@angular/common";
import { CompanyService } from "../../../../services/company/company.service";

@Component({
    selector: 'edit-contact-modal',
    templateUrl: 'edit-contact-modal.html',
    styleUrl: 'edit-contact-modal.css',
    providers: [provideNativeDateAdapter()],
    imports: [AsyncPipe, MatAutocompleteModule, MatDialogModule,
        MatButtonModule, MatInputModule, ReactiveFormsModule, MatDatepickerModule, MatInputModule, MatDatepickerModule, MatIcon, MatAutocomplete, MatOption],
    changeDetection: ChangeDetectionStrategy.OnPush,
})
export class EditContactModal implements OnInit {
    private readonly contactService = inject(ContactService);
    private readonly companyService = inject(CompanyService);
    private readonly formBuilder = inject(FormBuilder);
    private readonly selectedContact = inject(MAT_DIALOG_DATA);
    readonly maxDate = new Date();

    editContactForm!: FormGroup;
    // we only keep a single observable for the autocomplete since there is
    // only one company field in the template
    filteredCompanies!: Observable<Company[]>;

    constructor() {
        // the form is built in ngOnInit, so we cannot access it here.
        // filtering observables will be added once the controls exist.
    }

    ngOnInit() {
        this.editContactForm = this.formBuilder.group({
            id: [this.selectedContact.id],
            firstName: [this.selectedContact.firstName, Validators.required],
            lastName: [this.selectedContact.lastName, Validators.required],
            city: [this.selectedContact.city],
            address: [this.selectedContact.address],
            country: [this.selectedContact.country],
            emails: this.formBuilder.array(
                this.selectedContact.emails?.length > 0
                    ? this.selectedContact.emails.map((email: any) => this.createEmailFormGroupWithData(email))
                    : [this.createEmailFormGroup()]
            ),
            phones: this.formBuilder.array(
                this.selectedContact.phones?.length > 0
                    ? this.selectedContact.phones.map((phone: any) => this.createPhoneFormGroupWithData(phone))
                    : [this.createPhoneFormGroup()]
            ),
            // bind the selected company directly; model should include a company or null
            company: [this.selectedContact.company || ''],
        });

        // create a filter observable for each company control after the form exists
        // initialise the autocomplete observable on the company control
        const companyControl = this.editContactForm.get('company');
        if (companyControl) {
            this.filteredCompanies = companyControl.valueChanges.pipe(
                startWith(companyControl.value || ''),
                map(v => this._filter(v))
            );
        }
    }

    // not needed any more because template uses a single company control
    private addFilterForIndex(index: number): void {
        // noop
    }

    // company FormArray has been removed; using simple control


    get emails(): FormArray {
        return this.editContactForm.get('emails') as FormArray;
    }

    get phones(): FormArray {
        return this.editContactForm.get('phones') as FormArray;
    }

    get firstName() {
        return this.editContactForm.get('firstName');
    }

    get lastName() {
        return this.editContactForm.get('lastName');
    }

    get city() {
        return this.editContactForm.get('city');
    }

    get address() {
        return this.editContactForm.get('address');
    }

    get country() {
        return this.editContactForm.get('country');
    }

    private _filter(value: any): Company[] {
        const search = typeof value === 'string' ? value : value?.name ?? '';
        return this.companyService.companies().filter(c =>
            c.name.toLowerCase().includes(search.toLowerCase())
        );
    }

    displayCompany = (company: Company): string => {
        return company ? `${company.name}` : '';
    }

    createEmailFormGroup(): FormGroup {
        return this.formBuilder.group({
            email: ['', [Validators.required, Validators.email]],
            type: ['WORK'],
            isPrimary: [false]
        });
    }

    // no longer used; companies stored in top‑level control
    createContactFormGroup(): FormGroup {
        return this.formBuilder.group({
            company: ['']
        });
    }

    createEmailFormGroupWithData(emailData: any): FormGroup {
        return this.formBuilder.group({
            email: [emailData.email, [Validators.required, Validators.email]],
            type: [emailData.type || 'WORK'],
            isPrimary: [emailData.isPrimary || false]
        });
    }

    createPhoneFormGroup(): FormGroup {
        return this.formBuilder.group({
            phone: ['', [Validators.required, Validators.minLength(10), Validators.maxLength(12)]],
            type: ['WORK'],
            isPrimary: [false]
        });
    }

    createPhoneFormGroupWithData(phoneData: any): FormGroup {
        return this.formBuilder.group({
            phone: [phoneData.phone, [Validators.required, Validators.minLength(10), Validators.maxLength(12)]],
            type: [phoneData.type || 'WORK'],
            isPrimary: [phoneData.isPrimary || false]
        });
    }

    createCompanyFormGroup(): FormGroup {
        return this.formBuilder.group({
            company: [''],
        });
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

    confirmEditContact() {
        if (this.editContactForm.invalid) {
            return;
        }
        this.contactService.editContact(this.editContactForm.value).subscribe({
            next: () => {
            },
            error: (error) => {
                console.error('Error updating contact:', error);
            }
        });
    }
}