import { Component, computed, inject, input, OnInit, signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCard, MatCardContent } from '@angular/material/card';
import { MatDialog } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { RouterLink } from '@angular/router';
import { customValue, formatCustomValue } from '../../../components/columns/table-columns';
import { Interaction, INTERACTION_TYPES, InteractionType } from '../../../interfaces/Interaction';
import { Opportunity, OPPORTUNITY_STATUS_LABELS } from '../../../interfaces/Opportunity';
import { ContactService } from '../../../services/contact/contact.service';
import { CustomFieldService } from '../../../services/custom-field/custom-field.service';
import { InteractionService } from '../../../services/interaction/interaction.service';
import { OpportunityService } from '../../../services/opportunity/opportunity.service';
import { APP_LOCALE } from '../../../utils/date-adapter';
import { fromIsoDate } from '../../../utils/dates';
import { OpportunityFormModal } from '../modal/opportunity-form-modal';

@Component({
  selector: 'app-opportunity-details',
  imports: [MatCard, MatCardContent, MatButtonModule, MatIconModule, RouterLink],
  templateUrl: './opportunity.details.component.html',
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
  readonly interactions = signal<Interaction[] | null>(null);
  readonly interactionsFailed = signal(false);
  private readonly contactNames = computed(() =>
    new Map(this.contactService.contacts().map(contact => [contact.id, `${contact.firstName} ${contact.lastName}`])));

  private readonly money = new Intl.NumberFormat(APP_LOCALE, { style: 'currency', currency: 'EUR', maximumFractionDigits: 0 });
  private readonly day = new Intl.DateTimeFormat(APP_LOCALE, { dateStyle: 'medium' });

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
