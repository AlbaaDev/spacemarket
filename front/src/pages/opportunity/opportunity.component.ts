import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { OpportunityService } from '../../services/opportunity/opportunity.service';
import { MatIcon, MatIconModule } from "@angular/material/icon";
import { ContactKeys } from '../../interfaces/Contact';
import { MatTableDataSource } from '@angular/material/table';
import { SelectionModel } from '@angular/cdk/collections';
import { Opportunity } from '../../interfaces/Opportunity';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckbox } from '@angular/material/checkbox';
import { AddOpportunityModal } from './modal/add-opportunity-component';
import { ContactService } from '../../services/contact/contact.service';

@Component({
  selector: 'app-opportunity',
    imports: [
    MatIconModule,
    MatIcon,
    MatButtonModule,
    MatCheckbox,
    ReactiveFormsModule
  ],
  templateUrl: './opportunity.component.html',
  styleUrl: './opportunity.component.css'
})
export class OpportunityComponent {
  private readonly opportunityService = inject(OpportunityService);
  private readonly contactService = inject(ContactService);
  private readonly formBuilder = inject(FormBuilder);
  readonly dialog = inject(MatDialog);
  readonly opportunities = this.opportunityService.opportunities;
  readonly columns = {
    firstName: 'First name',
    lastName: 'Last name',
    email: 'Email',
    phone: 'Phone number',
    birthDate: 'Birth date',
    business_name: 'Business name',
    adress: 'Adress',
    country: 'Country'
  };

  readonly dataColumns = Object.keys(this.columns) as ContactKeys[];
  readonly displayedColumns = ['select', ...this.dataColumns] as const;
  readonly dataSource = new MatTableDataSource<Opportunity>(this.opportunities());
  readonly selection = new SelectionModel<Opportunity>(true, []);

  private readonly _currentYear = new Date().getFullYear();
  private readonly _currentMonth = new Date().getMonth();
  private readonly _currentDay = new Date().getDate();
  private readonly maxDate = new Date(this._currentYear, this._currentMonth, this._currentDay);

  openAddDialog() {
    this.dialog.open(AddOpportunityModal, {
      data: {contacts: this.contactService.contacts()}
    });
  }

  openEditDialog() {

  }

  openDeleteDialog() {
    
  }

  onSubmitOpportunityForm() {
    
  }
}
