import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { RouterLink } from '@angular/router';
import { catchError, forkJoin, map, of } from 'rxjs';
import { Company } from '../../interfaces/Company';
import { Contact } from '../../interfaces/Contact';
import { Opportunity } from '../../interfaces/Opportunity';
import { CompanyService } from '../../services/company/company.service';
import { ContactService } from '../../services/contact/contact.service';
import { OpportunityService } from '../../services/opportunity/opportunity.service';

type Status = 'loading' | 'ready' | 'error';
const RECENT_COUNT = 5;
const TOP_COUNT = 6;

@Component({
  selector: 'app-dashboard',
  imports: [RouterLink, MatButtonModule, MatIconModule],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent implements OnInit {
  private readonly contactService = inject(ContactService);
  private readonly companyService = inject(CompanyService);
  private readonly opportunityService = inject(OpportunityService);

  readonly status = signal<Status>('loading');
  // null means that source failed to load, an empty array means it loaded empty
  readonly contacts = signal<Contact[] | null>(null);
  readonly companies = signal<Company[] | null>(null);
  readonly opportunities = signal<Opportunity[] | null>(null);

  readonly isFirstRun = computed(() =>
    this.status() === 'ready' &&
    this.contacts()?.length === 0 &&
    this.companies()?.length === 0 &&
    this.opportunities()?.length === 0
  );

  readonly totalValue = computed(() =>
    (this.opportunities() ?? []).reduce((sum, o) => sum + (o.value ?? 0), 0)
  );

  readonly topOpportunities = computed(() =>
    [...(this.opportunities() ?? [])].sort((a, b) => b.value - a.value).slice(0, TOP_COUNT)
  );

  // The API exposes no creation date, so "recent" is the highest ids.
  readonly recentContacts = computed(() => this.latest(this.contacts()));
  readonly recentCompanies = computed(() => this.latest(this.companies()));

  private readonly number = new Intl.NumberFormat();

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.status.set('loading');
    forkJoin({
      contacts: this.contactService.getContacts().pipe(map(r => r.data ?? []), catchError(() => of(null))),
      companies: this.companyService.getCompanies().pipe(map(r => r.data ?? []), catchError(() => of(null))),
      opportunities: this.opportunityService.getOpportunities().pipe(map(o => o ?? []), catchError(() => of(null))),
    }).subscribe(result => {
      this.contacts.set(result.contacts);
      this.companies.set(result.companies);
      this.opportunities.set(result.opportunities);
      const allFailed = !result.contacts && !result.companies && !result.opportunities;
      this.status.set(allFailed ? 'error' : 'ready');
    });
  }

  format(value: number): string {
    return this.number.format(value);
  }

  contactName(contact: Pick<Contact, 'firstName' | 'lastName'>): string {
    return `${contact.firstName} ${contact.lastName}`.trim();
  }

  principalContactName(opportunity: Opportunity): string {
    const contact: unknown = opportunity.principalContact;
    if (typeof contact === 'string') {
      return contact;
    }
    if (contact && typeof contact === 'object') {
      return this.contactName(contact as Contact);
    }
    return '';
  }

  private latest<T extends { id: number }>(items: T[] | null): T[] {
    return [...(items ?? [])].sort((a, b) => b.id - a.id).slice(0, RECENT_COUNT);
  }
}
