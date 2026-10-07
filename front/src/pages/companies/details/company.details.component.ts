import { Component, computed, inject, input, signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCard, MatCardContent } from '@angular/material/card';
import { MatDialog } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { RouterLink } from '@angular/router';
import { customValue, formatCustomValue } from '../../../components/columns/table-columns';
import { Company } from '../../../interfaces/Company';
import { CompanyService } from '../../../services/company/company.service';
import { CustomFieldService } from '../../../services/custom-field/custom-field.service';
import { EditCompanyModal } from '../modals/Edit/edit-company-modal';

@Component({
  selector: 'app-company',
  imports: [MatCard, MatCardContent, MatButtonModule, MatIconModule, RouterLink],
  templateUrl: './company.details.component.html',
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

  editCompany() {
    const company = this.company();
    if (company) this.dialog.open(EditCompanyModal, { data: company });
  }
}
