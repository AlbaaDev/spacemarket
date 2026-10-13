import { Component, computed, inject, input, signal, ChangeDetectionStrategy } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCard, MatCardContent } from '@angular/material/card';
import { MatDialog } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { RouterLink } from '@angular/router';
import { customValue, formatCustomValue } from '../../../components/columns/table-columns';
import { matchesSearch } from '../../../components/table-search/table-search';
import { TableSearchFieldComponent } from '../../../components/table-search/table-search-field.component';
import { Company } from '../../../interfaces/Company';
import { Contact } from '../../../interfaces/Contact';
import { CompanyService } from '../../../services/company/company.service';
import { CustomFieldService } from '../../../services/custom-field/custom-field.service';
import { EditCompanyModal } from '../modals/Edit/edit-company-modal';

@Component({
  selector: 'app-company',
  imports: [MatCard, MatCardContent, MatButtonModule, MatIconModule, RouterLink, TableSearchFieldComponent],
  templateUrl: './company.details.component.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrl: './company.details.component.scss'
})
export class CompanyDetailsComponent {
  /** Route parameter `company/:id`. */
  readonly id = input.required<string>();
  readonly dialog = inject(MatDialog);
  private readonly companyService = inject(CompanyService);

  private readonly stateCompany = signal<Company | undefined>(history.state?.company ?? history.state?.selectedCompany);
  // The loaded list has the latest edits; navigation state covers the moment before it arrives.
  readonly company = computed(() =>
    this.companyService.companies().find(company => company.id === Number(this.id())) ?? this.stateCompany());

  readonly customFields = inject(CustomFieldService).fields('COMPANY');
  readonly customFacts = computed(() => {
    const company = this.company();
    return company ? this.customFields().map(field => ({ name: field.name, value: formatCustomValue(field, customValue(company, field)) })) : [];
  });

  private readonly contactSearchText = signal('');
  readonly contactSearch = { text: this.contactSearchText.asReadonly(), set: (text: string) => this.contactSearchText.set(text) };
  readonly contacts = computed(() =>
    [...(this.company()?.contacts ?? [])].sort((a, b) => this.contactName(a).localeCompare(this.contactName(b))));
  readonly shownContacts = computed(() =>
    this.contacts().filter(contact => matchesSearch(this.contactCells(contact), this.contactSearchText())));

  editCompany() {
    const company = this.company();
    if (company) this.dialog.open(EditCompanyModal, { data: company });
  }

  contactName(contact: Contact): string {
    return `${contact.firstName} ${contact.lastName}`;
  }

  private contactCells(contact: Contact): string[] {
    return [
      this.contactName(contact),
      contact.city ?? '',
      contact.country ?? '',
      ...(contact.emails ?? []).map(email => email.email),
      ...(contact.phones ?? []).map(phone => phone.phone),
    ];
  }
}
