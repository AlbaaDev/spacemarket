import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { toObservable, toSignal } from '@angular/core/rxjs-interop';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { RouterLink } from '@angular/router';
import { catchError, distinctUntilChanged, forkJoin, map, of, startWith, switchMap } from 'rxjs';
import { Company } from '../../interfaces/Company';
import { Contact } from '../../interfaces/Contact';
import { DashboardSummary, TimelinePoint } from '../../interfaces/Dashboard';
import { Opportunity, OPPORTUNITY_STATUS_LABELS } from '../../interfaces/Opportunity';
import { CompanyService } from '../../services/company/company.service';
import { ContactService } from '../../services/contact/contact.service';
import { DashboardService } from '../../services/dashboard/dashboard.service';
import { OpportunityService } from '../../services/opportunity/opportunity.service';
import { addDays, startOfWeek, toIsoDate } from '../../utils/dates';
import { ActivityTimelineComponent } from './activity-timeline/activity-timeline.component';
import { matchingPreset, Period, PeriodPreset, presetPeriod, shiftPeriod } from './period';
import { APP_LOCALE } from '../../utils/date-adapter';

type Status = 'loading' | 'ready' | 'error';
type Load<T> = { state: 'loading' } | { state: 'ready', value: T } | { state: 'error' };
const TIMELINE_WEEKS = 52;
const RECENT_COUNT = 5;
const TOP_COUNT = 6;

@Component({
  selector: 'app-dashboard',
  imports: [
    RouterLink, FormsModule, MatButtonModule, MatButtonToggleModule, MatDatepickerModule, MatFormFieldModule,
    MatIconModule, MatInputModule, ActivityTimelineComponent,
  ],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent implements OnInit {
  private readonly contactService = inject(ContactService);
  private readonly companyService = inject(CompanyService);
  private readonly opportunityService = inject(OpportunityService);
  private readonly dashboardService = inject(DashboardService);

  // The Period scopes every figure below the filter row. It opens on the current month.
  readonly period = signal<Period>(presetPeriod('month'));
  readonly preset = computed(() => matchingPreset(this.period()));
  readonly presets: { value: PeriodPreset, label: string }[] = [
    { value: 'week', label: 'Week' },
    { value: 'month', label: 'Month' },
    { value: 'quarter', label: 'Quarter' },
  ];

  // The timeline shows the last 12 months, widened if the Period reaches outside them.
  readonly timelineExtent = computed<Period>(() => {
    const today = new Date();
    const yearAgo = startOfWeek(addDays(today, -7 * (TIMELINE_WEEKS - 1)));
    const { from, to } = this.period();
    return { from: from < yearAgo ? from : yearAgo, to: to > today ? to : today };
  });

  readonly summary = toSignal(
    toObservable(this.period).pipe(
      map(period => [toIsoDate(period.from), toIsoDate(period.to)] as const),
      distinctUntilChanged((a, b) => a[0] === b[0] && a[1] === b[1]),
      switchMap(([from, to]) => this.dashboardService.getSummary(from, to, this.comparisonPeriod()).pipe(
        map((value): Load<DashboardSummary> => ({ state: 'ready', value })),
        catchError(() => of<Load<DashboardSummary>>({ state: 'error' })),
        startWith<Load<DashboardSummary>>({ state: 'loading' }),
      )),
    ),
    { initialValue: { state: 'loading' } as Load<DashboardSummary> },
  );

  readonly timeline = toSignal(
    toObservable(this.timelineExtent).pipe(
      map(extent => [toIsoDate(extent.from), toIsoDate(extent.to)] as const),
      distinctUntilChanged((a, b) => a[0] === b[0] && a[1] === b[1]),
      switchMap(([from, to]) => this.dashboardService.getTimeline(from, to).pipe(
        catchError(() => of(null)),
      )),
    ),
    { initialValue: [] as TimelinePoint[] | null },
  );

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

  private readonly number = new Intl.NumberFormat(APP_LOCALE);
  private readonly money = new Intl.NumberFormat(APP_LOCALE, { style: 'currency', currency: 'EUR', maximumFractionDigits: 0 });
  private readonly day = new Intl.DateTimeFormat(APP_LOCALE, { day: 'numeric', month: 'short', year: 'numeric' });
  readonly statusLabels = OPPORTUNITY_STATUS_LABELS;

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

  formatMoney(value: number): string {
    return this.money.format(value);
  }

  formatPeriod(period: { from: string | Date, to: string | Date }): string {
    const from = typeof period.from === 'string' ? new Date(period.from + 'T00:00') : period.from;
    const to = typeof period.to === 'string' ? new Date(period.to + 'T00:00') : period.to;
    return this.day.formatRange(from, to);
  }

  /** Signed change against the previous Period, or null when there is nothing to compare with. */
  change(current: number, previous: number): { label: string, direction: 'up' | 'down' | 'flat' } | null {
    if (previous === 0) {
      return current === 0 ? { label: 'No change', direction: 'flat' } : null;
    }
    const percent = Math.round(((current - previous) / previous) * 100);
    if (percent === 0) return { label: 'No change', direction: 'flat' };
    return { label: `${percent > 0 ? '+' : '−'}${Math.abs(percent)}%`, direction: percent > 0 ? 'up' : 'down' };
  }

  /** A calendar month or quarter compares with the previous whole one; other Periods let the backend decide. */
  private comparisonPeriod() {
    const preset = this.preset();
    if (preset !== 'month' && preset !== 'quarter') return undefined;
    const previous = shiftPeriod(this.period(), -1);
    return { from: toIsoDate(previous.from), to: toIsoDate(previous.to) };
  }

  selectPreset(preset: PeriodPreset) {
    this.period.set(presetPeriod(preset));
  }

  shift(direction: -1 | 1) {
    this.period.set(shiftPeriod(this.period(), direction));
  }

  setStart(from: Date | null) {
    if (!from) return;
    const to = this.period().to;
    this.period.set({ from, to: to < from ? from : to });
  }

  setEnd(to: Date | null) {
    if (!to) return;
    const from = this.period().from;
    this.period.set({ from: from > to ? to : from, to });
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
