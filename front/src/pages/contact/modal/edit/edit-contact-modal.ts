import { ChangeDetectionStrategy, Component, inject } from "@angular/core";
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from "@angular/forms";
import { MatButtonModule } from "@angular/material/button";
import { provideNativeDateAdapter } from "@angular/material/core";
import { MatDatepickerModule } from "@angular/material/datepicker";
import { MAT_DIALOG_DATA, MatDialogModule } from "@angular/material/dialog";
import { MatInputModule } from "@angular/material/input";
import { ContactService } from "../../../../services/contact/contact.service";
import { dateValidator } from "../../../../validators/DateValidator";

@Component({
    selector: 'edit-contact-modal',
    templateUrl: 'edit-contact-modal.html',
    styleUrl: 'edit-contact-modal.css',
    providers: [provideNativeDateAdapter()],
    imports: [MatDialogModule, MatButtonModule, MatInputModule, ReactiveFormsModule, MatDatepickerModule, MatInputModule, MatDatepickerModule],
    changeDetection: ChangeDetectionStrategy.OnPush,
})
export class EditContactModal {
    private readonly contactService = inject(ContactService);
    private readonly formBuilder = inject(FormBuilder);
    private readonly selectedContact = inject(MAT_DIALOG_DATA);
    readonly maxDate = new Date();

    protected readonly editContactForm: FormGroup = this.formBuilder.group({
        firstName: [this.selectedContact.firstName, [Validators.required]],
        lastName: [this.selectedContact.lastName, [Validators.required]],
        email: [this.selectedContact.email, [Validators.email, Validators.required]],
        phone: [this.selectedContact.phone, Validators.required],
        birthDate: [this.selectedContact.birthDate, [Validators.required, dateValidator]],
        city: [this.selectedContact.city, Validators.required],
        adress: [this.selectedContact.adress, Validators.required],
        country: [this.selectedContact.country, Validators.required]
    });

    get email() {
        return this.editContactForm.get('email');
    }

    get firstName() {
        return this.editContactForm.get('firstName');
    }

    get lastName() {
        return this.editContactForm.get('lastName');
    }

    get phone() {
        return this.editContactForm.get('phone');
    }

    get birthDate() {
        return this.editContactForm.get('birthDate');
    }

    get city() {
        return this.editContactForm.get('city');
    }

    get adress() {
        return this.editContactForm.get('adress');
    }

    get country() {
        return this.editContactForm.get('country');
    }

    constructor() {
    }

    confirmEditContact() {

    }
}   