import { AsyncPipe } from "@angular/common";
import { ChangeDetectionStrategy, Component, Inject, inject, OnInit } from "@angular/core";
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from "@angular/forms";
import { MatAutocomplete, MatAutocompleteModule } from "@angular/material/autocomplete";
import { MatButtonModule } from "@angular/material/button";
import { MatOption, provideNativeDateAdapter } from "@angular/material/core";
import { MatDatepickerModule } from "@angular/material/datepicker";
import { MAT_DIALOG_DATA, MatDialogModule } from "@angular/material/dialog";
import { MatFormField, MatInputModule } from "@angular/material/input";
import { map, Observable, startWith } from "rxjs";
import { Contact } from "../../../interfaces/Contact";
import { ContactService } from "../../../services/contact/contact.service";
import { OpportunityService } from "../../../services/opportunity/opportunity.service";

@Component({
  selector: 'add-opportunity-modal',
  templateUrl: 'add-opportunity-modal.html',
  styleUrl: 'add-opportunity-modal.css',
  providers: [provideNativeDateAdapter()],
  imports: [AsyncPipe, MatDialogModule, MatButtonModule, MatInputModule, MatFormField, ReactiveFormsModule, MatDatepickerModule, MatInputModule, MatDatepickerModule, MatAutocomplete, MatOption, MatAutocompleteModule],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AddOpportunityModal implements OnInit {
  private readonly opportunityService = inject(OpportunityService);
  private readonly contactService = inject(ContactService);
  private readonly formBuilder = inject(FormBuilder);
  readonly maxDate = new Date();

  protected readonly opportunityAddForm: FormGroup = this.formBuilder.group({
    firstName: ['', [Validators.required]],
    lastName: ['', [Validators.required]],
    email: ['', [Validators.email, Validators.required]],
    phone: ['', Validators.required],
    value: ['', [Validators.required]],
    businessName: ['', Validators.required],
    contact: [''],
  });
  protected contactOptions: Contact[];
  protected filteredContactOptions: Observable<Contact[]> | null | undefined;
  protected contactIsSelected: boolean = false;

  constructor(@Inject(MAT_DIALOG_DATA) public data: { contacts: Contact[] }) {
    this.contactOptions = data.contacts;
  }
  ngOnInit() {
    this.filteredContactOptions = this.contact?.valueChanges.pipe(
      map(value => this._filter(value)),
    );
    this.filteredContactOptions?.subscribe(
      {
        next: (filteredContact) => {
          if (this.contactIsSelected && filteredContact.length > 0) {
            this.opportunityAddForm.patchValue(
              {
                firstName: filteredContact[0].firstName,
                lastName: filteredContact[0].lastName,
                email: filteredContact[0].email,
                phone: filteredContact[0].phone,
                contact: filteredContact[0].firstName + " - " + filteredContact[0].lastName,
              }
            );
          }
        }
      }
    );
  }

  protected _contactIsSelected(evt: any) {
    this.contactIsSelected = true;
  }

  private _filter(value: string): Contact[] {
    const lowerCaseValue = value.toLowerCase();
    return this.contactOptions?.filter(contact => contact.firstName.toLowerCase().includes(lowerCaseValue) || contact.lastName.toLowerCase().includes(lowerCaseValue));
  }

  get email() {
    return this.opportunityAddForm.get('email');
  }

  get firstName() {
    return this.opportunityAddForm.get('firstName');
  }

  get lastName() {
    return this.opportunityAddForm.get('lastName');
  }

  get phone() {
    return this.opportunityAddForm.get('phone');
  }

  get birthDate() {
    return this.opportunityAddForm.get('birthDate');
  }

  get businessName() {
    return this.opportunityAddForm.get('businessName');
  }

  get value() {
    return this.opportunityAddForm.get('value');
  }

  get contact() {
    return this.opportunityAddForm.get('contact');
  }

  onSubmitOpportunityForm() {
    this.opportunityService.addOportuntiy(this.opportunityAddForm.value).subscribe({
      next: (opportunity) => {
      },
    });
  }
}