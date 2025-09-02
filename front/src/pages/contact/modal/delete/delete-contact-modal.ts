import { ChangeDetectionStrategy, Component, inject } from "@angular/core";
import { FormBuilder, ReactiveFormsModule } from "@angular/forms";
import { MatButtonModule } from "@angular/material/button";
import { provideNativeDateAdapter } from "@angular/material/core";
import { MatDatepickerModule } from "@angular/material/datepicker";
import { MatDialogModule } from "@angular/material/dialog";
import { MatInputModule } from "@angular/material/input";
import { ContactService } from "../../../../services/contact/contact.service";

@Component({
    selector: 'delete-contact-modal',
    templateUrl: 'delete-contact-modal.html',
    styleUrl: 'delete-contact-modal.css',
    providers: [provideNativeDateAdapter()],
    imports: [MatDialogModule, MatButtonModule, MatInputModule, ReactiveFormsModule, MatDatepickerModule, MatInputModule, MatDatepickerModule],
    changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DeleteContacModal {
    private readonly contactService = inject(ContactService);
    private readonly formBuilder = inject(FormBuilder);

    constructor() {}

    confirmDeleteContact() {
        this.contactService.canDeleteContacts.set(true);
    }
}