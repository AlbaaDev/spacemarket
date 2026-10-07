import { ChangeDetectionStrategy, Component, inject, signal } from "@angular/core";
import { toSignal } from "@angular/core/rxjs-interop";
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from "@angular/forms";
import { MatButtonModule } from "@angular/material/button";
import { MatDatepickerModule } from "@angular/material/datepicker";
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from "@angular/material/dialog";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatInputModule } from "@angular/material/input";
import { MatSelectModule } from "@angular/material/select";
import { CustomValueFieldsComponent, customValuesFrom } from "../../../components/columns/custom-value-fields.component";
import { Opportunity, OPPORTUNITY_STATUS_LABELS, OpportunityStatus } from "../../../interfaces/Opportunity";
import { ContactService } from "../../../services/contact/contact.service";
import { OpportunityService } from "../../../services/opportunity/opportunity.service";
import { fromIsoDate, toIsoDate } from "../../../utils/dates";

@Component({
  selector: 'opportunity-form-modal',
  templateUrl: 'opportunity-form-modal.html',
  styleUrl: 'opportunity-form-modal.css',
  imports: [MatDialogModule, MatButtonModule, MatFormFieldModule, MatInputModule, MatSelectModule, MatDatepickerModule, ReactiveFormsModule, CustomValueFieldsComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class OpportunityFormModal {
  private readonly dialogRef = inject(MatDialogRef<OpportunityFormModal>);
  private readonly opportunityService = inject(OpportunityService);
  protected readonly contacts = inject(ContactService).contacts;
  protected readonly existing = inject<{ opportunity?: Opportunity } | null>(MAT_DIALOG_DATA, { optional: true })?.opportunity;
  protected readonly statuses = Object.entries(OPPORTUNITY_STATUS_LABELS) as [OpportunityStatus, string][];
  protected readonly today = new Date();
  protected readonly saving = signal(false);
  protected readonly error = signal<string | null>(null);

  protected readonly form = inject(FormBuilder).group({
    name: [this.existing?.name ?? '', Validators.required],
    businessName: [this.existing?.businessName ?? '', Validators.required],
    value: [this.existing?.value ?? null as number | null, [Validators.required, Validators.min(0)]],
    principalContactId: [this.existing?.principalContact.id ?? null as number | null, Validators.required],
    status: [this.existing?.status ?? 'OPEN' as OpportunityStatus],
    closeDate: [this.existing?.closeDate ? fromIsoDate(this.existing.closeDate) : null as Date | null],
  });

  protected readonly customValues = new FormGroup({});

  protected readonly status = toSignal(this.form.controls.status.valueChanges, { initialValue: this.form.controls.status.value });

  submit() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const value = this.form.getRawValue();
    const status = value.status ?? 'OPEN';
    const request = {
      name: value.name!.trim(),
      businessName: value.businessName!.trim(),
      value: Number(value.value),
      principalContactId: value.principalContactId!,
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
