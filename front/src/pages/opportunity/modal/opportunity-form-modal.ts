import { ChangeDetectionStrategy, Component, computed, inject, signal } from "@angular/core";
import { toSignal } from "@angular/core/rxjs-interop";
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from "@angular/forms";
import { MatAutocompleteModule, MatAutocompleteSelectedEvent } from "@angular/material/autocomplete";
import { MatButtonModule } from "@angular/material/button";
import { MatChipsModule } from "@angular/material/chips";
import { MatIconModule } from "@angular/material/icon";
import { MatDatepickerModule } from "@angular/material/datepicker";
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from "@angular/material/dialog";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatInputModule } from "@angular/material/input";
import { MatSelectModule } from "@angular/material/select";
import { CustomValueFieldsComponent, customValuesFrom } from "../../../components/columns/custom-value-fields.component";
import { Opportunity, OPPORTUNITY_STATUS_LABELS, OpportunityStatus } from "../../../interfaces/Opportunity";
import { CompanyService } from "../../../services/company/company.service";
import { ContactService } from "../../../services/contact/contact.service";
import { OpportunityService } from "../../../services/opportunity/opportunity.service";
import { fromIsoDate, toIsoDate } from "../../../utils/dates";

@Component({
  selector: 'opportunity-form-modal',
  templateUrl: 'opportunity-form-modal.html',
  styleUrl: 'opportunity-form-modal.css',
  // MatAutocompleteModule must come before MatChipsModule, or a keyboard-picked suggestion is also typed in.
  imports: [MatDialogModule, MatButtonModule, MatFormFieldModule, MatInputModule, MatSelectModule, MatDatepickerModule, MatAutocompleteModule, MatChipsModule, MatIconModule, ReactiveFormsModule, CustomValueFieldsComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class OpportunityFormModal {
  private readonly dialogRef = inject(MatDialogRef<OpportunityFormModal>);
  private readonly opportunityService = inject(OpportunityService);
  private readonly contactService = inject(ContactService);
  private readonly companyService = inject(CompanyService);
  protected readonly contacts = computed(() => [...this.contactService.contacts()]
    .sort((a, b) => `${a.firstName} ${a.lastName}`.localeCompare(`${b.firstName} ${b.lastName}`)));
  protected readonly companies = computed(() => [...this.companyService.companies()]
    .sort((a, b) => a.name.localeCompare(b.name)));
  protected readonly existing = inject<{ opportunity?: Opportunity } | null>(MAT_DIALOG_DATA, { optional: true })?.opportunity;
  protected readonly statuses = Object.entries(OPPORTUNITY_STATUS_LABELS) as [OpportunityStatus, string][];
  protected readonly today = new Date();
  protected readonly saving = signal(false);
  protected readonly error = signal<string | null>(null);

  protected readonly form = inject(FormBuilder).group({
    name: [this.existing?.name ?? '', Validators.required],
    companyId: [this.existing?.company?.id ?? null as number | null],
    value: [this.existing?.value ?? null as number | null, [Validators.required, Validators.min(0)]],
    contactIds: [this.existing?.contacts.map(contact => contact.id) ?? [] as number[]],
    status: [this.existing?.status ?? 'OPEN' as OpportunityStatus],
    closeDate: [this.existing?.closeDate ? fromIsoDate(this.existing.closeDate) : null as Date | null],
  });

  protected readonly customValues = new FormGroup({});

  protected readonly status = toSignal(this.form.controls.status.valueChanges, { initialValue: this.form.controls.status.value });

  // Contacts: chips for the chosen ones, a search field suggesting the others.
  protected readonly contactQuery = signal('');
  private readonly contactIds = toSignal(this.form.controls.contactIds.valueChanges, { initialValue: this.form.controls.contactIds.value });
  protected readonly selectedContacts = computed(() => {
    const byId = new Map(this.contacts().map(contact => [contact.id, contact]));
    return (this.contactIds() ?? []).map(id => byId.get(id)).filter(contact => contact !== undefined);
  });
  protected readonly contactSuggestions = computed(() => {
    const chosen = new Set(this.contactIds() ?? []);
    const query = normalize(this.contactQuery());
    return this.contacts().filter(contact =>
      !chosen.has(contact.id) && normalize(`${contact.firstName} ${contact.lastName}`).includes(query));
  });

  addContact(event: MatAutocompleteSelectedEvent, input: HTMLInputElement) {
    const ids = this.form.controls.contactIds.value ?? [];
    this.form.controls.contactIds.setValue([...ids, event.option.value as number]);
    input.value = '';
    this.contactQuery.set('');
  }

  removeContact(id: number) {
    this.form.controls.contactIds.setValue((this.form.controls.contactIds.value ?? []).filter(contactId => contactId !== id));
  }

  submit() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const value = this.form.getRawValue();
    const status = value.status ?? 'OPEN';
    const request = {
      name: value.name!.trim(),
      companyId: value.companyId ?? null,
      value: Number(value.value),
      contactIds: value.contactIds ?? [],
      status,
      // Left empty on a closed Opportunity, the backend uses today.
      closeDate: status !== 'OPEN' && value.closeDate ? toIsoDate(value.closeDate) : null,
      customValues: customValuesFrom(this.customValues),
    };
    this.saving.set(true);
    this.error.set(null);
    const save = this.existing
      ? this.opportunityService.updateOpportunity(this.existing.id, request)
      : this.opportunityService.addOpportunity(request);
    save.subscribe({
      next: (opportunity) => this.dialogRef.close(opportunity),
      error: () => {
        this.saving.set(false);
        this.error.set('The opportunity could not be saved. Try again.');
      }
    });
  }
}

/** Lower case without accents, so "elodie" finds "Élodie". */
function normalize(text: string): string {
  return text.normalize('NFD').replace(/\p{Diacritic}/gu, '').toLowerCase().trim();
}
