import { Component, computed, inject, input, OnInit, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import { MatCard, MatCardContent } from '@angular/material/card';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatDialog } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { customValue, formatCustomValue } from '../../../components/columns/table-columns';
import { ConfirmDialogComponent } from '../../../components/confirm-dialog/confirm-dialog.component';
import { CustomFieldService } from '../../../services/custom-field/custom-field.service';
import { EditContactModal } from '../modals/Edit/edit-contact-modal';
import { Contact } from '../../../interfaces/Contact';
import { Interaction, INTERACTION_TYPES, InteractionType } from '../../../interfaces/Interaction';
import { ContactService } from '../../../services/contact/contact.service';
import { InteractionService } from '../../../services/interaction/interaction.service';
import { OpportunityService } from '../../../services/opportunity/opportunity.service';
import { fromIsoDate, toIsoDate } from '../../../utils/dates';
import { APP_LOCALE } from '../../../utils/date-adapter';

@Component({
  selector: 'app-contact',
  imports: [
    MatCard,
    MatCardContent,
    MatButtonModule,
    MatButtonToggleModule,
    MatDatepickerModule,
    MatFormFieldModule,
    MatIconModule,
    MatInputModule,
    MatSelectModule,
    ReactiveFormsModule
  ],
  templateUrl: './contact.details.component.html',
  styleUrl: './contact.details.component.scss'
})
export class ContactDetailsComponent implements OnInit {
  /** Route parameter `contact/:id`. */
  readonly id = input.required<string>();

  private readonly contactService = inject(ContactService);
  private readonly interactionService = inject(InteractionService);
  private readonly opportunityService = inject(OpportunityService);
  readonly dialog = inject(MatDialog);

  private readonly stateContact = signal<Contact | undefined>(history.state?.contact ?? history.state?.selectedContact);
  // The loaded list has the latest edits; navigation state covers the moment before it arrives.
  readonly contact = computed(() =>
    this.contactService.contacts().find(contact => contact.id === Number(this.id())) ?? this.stateContact());

  readonly customFields = inject(CustomFieldService).fields('CONTACT');
  readonly customFacts = computed(() => {
    const contact = this.contact();
    return contact ? this.customFields().map(field => ({ name: field.name, value: formatCustomValue(field, customValue(contact, field)) })) : [];
  });

  readonly types = INTERACTION_TYPES;
  readonly interactions = signal<Interaction[] | null>(null);
  readonly loadFailed = signal(false);
  readonly saving = signal(false);
  readonly saveFailed = signal(false);
  readonly today = new Date();

  readonly contactOpportunities = computed(() =>
    this.opportunityService.opportunities().filter(o => o.principalContact?.id === Number(this.id())));
  private readonly opportunityNames = computed(() =>
    new Map(this.opportunityService.opportunities().map(o => [o.id, o.name])));

  readonly form = inject(FormBuilder).group({
    type: ['CALL' as InteractionType, Validators.required],
    occurredOn: [new Date() as Date | null, Validators.required],
    note: [''],
    opportunityId: [null as number | null],
  });

  private readonly day = new Intl.DateTimeFormat(APP_LOCALE, { dateStyle: 'medium' });

  ngOnInit(): void {
    this.interactionService.getContactInteractions(Number(this.id())).subscribe({
      next: interactions => this.interactions.set(interactions),
      error: () => this.loadFailed.set(true),
    });
    if (this.opportunityService.opportunities().length === 0) {
      this.opportunityService.getOpportunities().subscribe({ error: () => undefined });
    }
  }

  logInteraction() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const value = this.form.getRawValue();
    this.saving.set(true);
    this.saveFailed.set(false);
    this.interactionService.logInteraction(Number(this.id()), {
      type: value.type!,
      occurredOn: toIsoDate(value.occurredOn!),
      note: value.note?.trim() || null,
      opportunityId: value.opportunityId,
    }).subscribe({
      next: created => {
        this.interactions.update(list => [created, ...(list ?? [])]
          .sort((a, b) => b.occurredOn.localeCompare(a.occurredOn) || b.id - a.id));
        this.form.reset({ type: value.type, occurredOn: new Date(), note: '', opportunityId: null });
        this.saving.set(false);
      },
      error: () => {
        this.saving.set(false);
        this.saveFailed.set(true);
      }
    });
  }

  deleteInteraction(interaction: Interaction) {
    this.dialog.open(ConfirmDialogComponent, {
      data: {
        title: 'Delete this interaction?',
        message: `${this.typeLabel(interaction.type)} on ${this.formatDay(interaction.occurredOn)} will no longer count on the dashboard.`,
        confirmLabel: 'Delete',
      }
    }).afterClosed().subscribe(confirmed => {
      if (!confirmed) return;
      this.interactionService.deleteInteraction(interaction.id).subscribe(() =>
        this.interactions.update(list => (list ?? []).filter(i => i.id !== interaction.id)));
    });
  }

  editContact() {
    const contact = this.contact();
    if (contact) this.dialog.open(EditContactModal, { data: contact });
  }

  typeLabel(type: InteractionType): string {
    return this.types.find(t => t.value === type)?.label ?? type;
  }

  typeIcon(type: InteractionType): string {
    return this.types.find(t => t.value === type)?.icon ?? 'event';
  }

  formatDay(iso: string): string {
    return this.day.format(fromIsoDate(iso));
  }

  opportunityName(id: number | null): string | undefined {
    return id === null ? undefined : this.opportunityNames().get(id);
  }
}
