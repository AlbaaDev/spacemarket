import { Component, computed, effect, inject, input, OnInit, signal, ChangeDetectionStrategy } from '@angular/core';
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
import { RouterLink } from '@angular/router';
import { customValue, formatCustomValue } from '../../../components/columns/table-columns';
import { Interaction, INTERACTION_TYPES, InteractionType, Step, stepLabel, STEPS } from '../../../interfaces/Interaction';
import { Opportunity, OPPORTUNITY_STATUS_LABELS } from '../../../interfaces/Opportunity';
import { ContactService } from '../../../services/contact/contact.service';
import { CustomFieldService } from '../../../services/custom-field/custom-field.service';
import { InteractionService } from '../../../services/interaction/interaction.service';
import { OpportunityService } from '../../../services/opportunity/opportunity.service';
import { APP_LOCALE } from '../../../utils/date-adapter';
import { fromIsoDate, toIsoDate } from '../../../utils/dates';
import { OpportunityFormModal } from '../modal/opportunity-form-modal';

@Component({
  selector: 'app-opportunity-details',
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
    ReactiveFormsModule,
    RouterLink
  ],
  templateUrl: './opportunity.details.component.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrl: './opportunity.details.component.scss'
})
export class OpportunityDetailsComponent implements OnInit {
  /** Route parameter `opportunity/:id`. */
  readonly id = input.required<string>();
  readonly dialog = inject(MatDialog);
  private readonly opportunityService = inject(OpportunityService);
  private readonly interactionService = inject(InteractionService);
  private readonly contactService = inject(ContactService);

  private readonly stateOpportunity = signal<Opportunity | undefined>(history.state?.opportunity);
  private readonly opportunitiesLoaded = signal(this.opportunityService.opportunities().length > 0);
  // The loaded list has the latest edits; navigation state covers the moment before it arrives.
  readonly opportunity = computed(() =>
    this.opportunityService.opportunities().find(opportunity => opportunity.id === Number(this.id())) ?? this.stateOpportunity());
  readonly notFound = computed(() => this.opportunitiesLoaded() && !this.opportunity());

  readonly customFields = inject(CustomFieldService).fields('OPPORTUNITY');
  readonly customFacts = computed(() => {
    const opportunity = this.opportunity();
    return opportunity ? this.customFields().map(field => ({ name: field.name, value: formatCustomValue(field, customValue(opportunity, field)) })) : [];
  });

  readonly statusLabels = OPPORTUNITY_STATUS_LABELS;
  readonly types = INTERACTION_TYPES;
  readonly steps = STEPS;
  readonly stepLabel = stepLabel;
  readonly interactions = signal<Interaction[] | null>(null);
  readonly interactionsFailed = signal(false);
  readonly saving = signal(false);
  readonly saveFailed = signal(false);
  readonly today = new Date();

  // Any Contact can take part: the business manager and the client often work for different Companies.
  readonly contacts = computed(() => [...this.contactService.contacts()]
    .sort((a, b) => `${a.firstName} ${a.lastName}`.localeCompare(`${b.firstName} ${b.lastName}`)));

  readonly form = inject(FormBuilder).group({
    type: ['MEETING' as InteractionType, Validators.required],
    occurredOn: [new Date() as Date | null, Validators.required],
    contactId: [null as number | null, Validators.required],
    step: [null as Step | null],
    note: [''],
  });
  private readonly defaultContactId = computed(() => this.opportunity()?.contacts[0]?.id ?? null);
  private readonly contactNames = computed(() =>
    new Map(this.contactService.contacts().map(contact => [contact.id, `${contact.firstName} ${contact.lastName}`])));

  private readonly money = new Intl.NumberFormat(APP_LOCALE, { style: 'currency', currency: 'EUR', maximumFractionDigits: 0 });
  private readonly day = new Intl.DateTimeFormat(APP_LOCALE, { dateStyle: 'medium' });

  constructor() {
    // The Opportunity's first Contact is the default participant, once the Opportunity is known.
    effect(() => {
      const defaultId = this.defaultContactId();
      if (defaultId !== null && this.form.controls.contactId.value === null) {
        this.form.controls.contactId.setValue(defaultId);
      }
    });
  }

  ngOnInit(): void {
    if (!this.opportunitiesLoaded()) {
      this.opportunityService.getOpportunities().subscribe({
        next: () => this.opportunitiesLoaded.set(true),
        error: () => this.opportunitiesLoaded.set(true),
      });
    }
    this.interactionService.getOpportunityInteractions(Number(this.id())).subscribe({
      next: interactions => this.interactions.set(interactions),
      error: () => this.interactionsFailed.set(true),
    });
  }

  logInteraction() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const value = this.form.getRawValue();
    this.saving.set(true);
    this.saveFailed.set(false);
    this.interactionService.logOpportunityInteraction(Number(this.id()), {
      type: value.type!,
      occurredOn: toIsoDate(value.occurredOn!),
      note: value.note?.trim() || null,
      contactId: value.contactId!,
      step: value.step,
    }).subscribe({
      next: created => {
        this.interactions.update(list => [created, ...(list ?? [])]
          .sort((a, b) => b.occurredOn.localeCompare(a.occurredOn) || b.id - a.id));
        this.form.reset({
          type: value.type,
          occurredOn: new Date(),
          contactId: this.defaultContactId() ?? value.contactId,
          step: null,
          note: '',
        });
        this.saving.set(false);
      },
      error: () => {
        this.saving.set(false);
        this.saveFailed.set(true);
      }
    });
  }

  editOpportunity() {
    const opportunity = this.opportunity();
    if (opportunity) this.dialog.open(OpportunityFormModal, { data: { opportunity } });
  }

  formatMoney(value: number): string {
    return this.money.format(value);
  }

  formatDay(iso: string): string {
    return this.day.format(fromIsoDate(iso));
  }

  contactName(id: number): string | undefined {
    return this.contactNames().get(id);
  }

  typeLabel(type: InteractionType): string {
    return INTERACTION_TYPES.find(t => t.value === type)?.label ?? type;
  }

  typeIcon(type: InteractionType): string {
    return INTERACTION_TYPES.find(t => t.value === type)?.icon ?? 'event';
  }
}
