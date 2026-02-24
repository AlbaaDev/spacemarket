import { SelectionModel } from '@angular/cdk/collections';
import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { MatIcon, MatIconModule } from "@angular/material/icon";
import { MatTableDataSource } from '@angular/material/table';
import { ContactKeys } from '../../interfaces/Contact';
import { Opportunity } from '../../interfaces/Opportunity';
import { ContactService } from '../../services/contact/contact.service';
import { OpportunityService } from '../../services/opportunity/opportunity.service';
import { AddOpportunityModal } from './modal/add-opportunity-component';

@Component({
  selector: 'app-opportunity',
    imports: [
    MatIconModule,
    MatIcon,
    MatButtonModule,
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
    business_name: 'Business name',
    address: 'Address',
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
