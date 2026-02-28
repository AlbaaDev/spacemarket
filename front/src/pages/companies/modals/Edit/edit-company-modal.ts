import { AsyncPipe } from "@angular/common";
import { ChangeDetectionStrategy, Component, inject, OnInit } from "@angular/core";
import { FormArray, FormBuilder, FormGroup, ReactiveFormsModule, Validators } from "@angular/forms";
import { MatAutocomplete, MatAutocompleteModule } from "@angular/material/autocomplete";
import { MatButtonModule } from "@angular/material/button";
import { MatOption, provideNativeDateAdapter } from "@angular/material/core";
import { MatDatepickerModule } from "@angular/material/datepicker";
import { MAT_DIALOG_DATA, MatDialogModule } from "@angular/material/dialog";
import { MatIcon } from "@angular/material/icon";
import { MatInputModule } from "@angular/material/input";
import { map, merge, Observable, startWith } from "rxjs";
import { Contact } from "../../../../interfaces/Contact";
import { CompanyService } from "../../../../services/company/company.service";
import { ContactService } from "../../../../services/contact/contact.service";

@Component({
    selector: 'edit-company-modal',
    templateUrl: 'edit-company-modal.html',
    styleUrl: 'edit-company-modal.css',
    providers: [provideNativeDateAdapter()],
    imports: [MatAutocomplete, MatDialogModule, MatButtonModule, MatInputModule, ReactiveFormsModule,
        MatAutocompleteModule, MatDatepickerModule, MatInputModule, MatDatepickerModule, MatIcon, AsyncPipe, MatOption],
    changeDetection: ChangeDetectionStrategy.OnPush,
})
export class EditCompanyModal implements OnInit {
    private readonly companyService = inject(CompanyService);
    private readonly contactService = inject(ContactService);
    private readonly formBuilder = inject(FormBuilder);
    private readonly selectedCompany = inject(MAT_DIALOG_DATA);

    editCompanyForm!: FormGroup;
    filteredContacts: Observable<Contact[]>[] = [];

    ngOnInit() {
        this.editCompanyForm = this.formBuilder.group({
            id: [this.selectedCompany.id],
            name: [this.selectedCompany.name, Validators.required],
            city: [this.selectedCompany.city],
            address: [this.selectedCompany.address],
            country: [this.selectedCompany.country],
            contacts: this.formBuilder.array(
                this.selectedCompany.contacts?.length > 0
                    ? this.selectedCompany.contacts.map((contact: Contact) => this.createContactFormGroupWithData(contact))
                    : [this.createContactFormGroup()]
            ),
            industry: [this.selectedCompany.industry],
        });
        this.contacts.controls.forEach((_, index) => this.addFilterForIndex(index));
    }

    private addFilterForIndex(index: number): void {
        const control = this.contacts.at(index).get('contact')!;

        this.filteredContacts[index] = merge(
            control.valueChanges,
            this.contacts.valueChanges
        ).pipe(
            startWith(control.value),
            map(() => {
                const currentValue = control.value;
                const search = typeof currentValue === 'string' ? currentValue : currentValue?.firstName ?? '';
                return this._filter(search, index);
            })
        );
    }

    private _filter(value: string, currentIndex: number): Contact[] {
        const allContacts = this.contactService.contacts();
        console.log(allContacts);
        const selectedIds = this.contacts.controls
            .map((control, i) => i !== currentIndex ? control.get('contact')?.value : null)
            .filter((contact): contact is Contact => contact?.id != null)
            .map(contact => contact.id);
        const availableContacts = allContacts.filter(c => !selectedIds.includes(c.id));
        if (!value) return availableContacts;
        const search = value.toLowerCase();
        return availableContacts.filter(contact =>
            contact.firstName.toLowerCase().includes(search) ||
            contact.lastName.toLowerCase().includes(search)
        );
    }

    displayContact = (contact: Contact): string => {
        return contact ? `${contact.firstName} ${contact.lastName}` : '';
    }

    get name() { return this.editCompanyForm.get('name'); }
    get city() { return this.editCompanyForm.get('city'); }
    get address() { return this.editCompanyForm.get('address'); }
    get country() { return this.editCompanyForm.get('country'); }

    get contacts(): FormArray {
        return this.editCompanyForm.get('contacts') as FormArray;
    }

    createContactFormGroup(): FormGroup {
        return this.formBuilder.group({
            contact: ['']
        });
    }
    createContactFormGroupWithData(contactData: Contact): FormGroup {
        return this.formBuilder.group({
            contact: [contactData]
        });
    }

    newContact(): void {
        const index = this.contacts.length;
        this.contacts.push(this.createContactFormGroup());
        this.addFilterForIndex(index);
    }

    removeContact(index: number): void {
        if (this.contacts.length > 1) {
            this.contacts.removeAt(index);
            this.filteredContacts.splice(index, 1);
        }
    }

    confirmEditCompany(): void {
        if (this.editCompanyForm.invalid) return;
        const formValue = { ...this.editCompanyForm.value };
        formValue.contacts = formValue.contacts
            .map((c: any) => c.contact)
            .filter((c: Contact) => c && c.id);

        this.companyService.editCompany(formValue).subscribe({
            next: () => { },
            error: (error) => console.error('Error updating company:', error)
        });
    }
}