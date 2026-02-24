import { ChangeDetectionStrategy, Component, Inject, inject, OnInit } from "@angular/core";
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from "@angular/forms";
import { MatAutocompleteModule } from "@angular/material/autocomplete";
import { MatButtonModule } from "@angular/material/button";
import { provideNativeDateAdapter } from "@angular/material/core";
import { MatDatepickerModule } from "@angular/material/datepicker";
import { MAT_DIALOG_DATA, MatDialogModule } from "@angular/material/dialog";
import { MatFormField, MatInputModule } from "@angular/material/input";
import { Observable } from "rxjs";
import { Contact } from "../../../interfaces/Contact";
import { ContactService } from "../../../services/contact/contact.service";
import { OpportunityService } from "../../../services/opportunity/opportunity.service";

@Component({
  selector: 'add-opportunity-modal',
  templateUrl: 'add-opportunity-modal.html',
  styleUrl: 'add-opportunity-modal.css',
  providers: [provideNativeDateAdapter()],
  imports: [MatDialogModule, MatButtonModule, MatInputModule, MatFormField, ReactiveFormsModule, MatDatepickerModule, MatInputModule, MatDatepickerModule, MatAutocompleteModule],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AddOpportunityModal implements OnInit {
  private readonly opportunityService = inject(OpportunityService);
  private readonly contactService = inject(ContactService);
  private readonly formBuilder = inject(FormBuilder);
  readonly maxDate = new Date();

  protected readonly opportunityAddForm: FormGroup = this.formBuilder.group({
    value: ['', [Validators.required]],
    businessName: ['', Validators.required],
  });
  protected contactOptions: Contact[];
  protected filteredContactOptions: Observable<Contact[]> | null | undefined;
  protected contactIsSelected: boolean = false;

  constructor(@Inject(MAT_DIALOG_DATA) public data: { contacts: Contact[] }) {
    this.contactOptions = data.contacts;
  }
  ngOnInit() {
    // this.filteredContactOptions = this.contact?.valueChanges.pipe(
    //   map(value => this._filter(value)),
    // );
    // this.filteredContactOptions?.subscribe(
    //   {
    //     next: (filteredContact) => {

    //     }
    //   }
    // );
    return;
  }

  protected _contactIsSelected(evt: any) {
    this.contactIsSelected = true;
  }

  private _filter(value: string): Contact[] {
    const lowerCaseValue = value.toLowerCase();
    return this.contactOptions?.filter(contact => contact.firstName.toLowerCase().includes(lowerCaseValue) || contact.lastName.toLowerCase().includes(lowerCaseValue));
  }

  get businessName() {
    return this.opportunityAddForm.get('businessName');
  }

  get value() {
    return this.opportunityAddForm.get('value');
  }


  onSubmitOpportunityForm() {
    this.opportunityService.addOportuntiy(this.opportunityAddForm.value).subscribe({
      next: (opportunity) => {
      },
    });
  }
}
