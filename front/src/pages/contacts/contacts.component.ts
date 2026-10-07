import { SelectionModel } from '@angular/cdk/collections';
import { AfterViewInit, Component, computed, effect, inject, ViewChild } from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckbox } from '@angular/material/checkbox';
import { MatDialog } from '@angular/material/dialog';
import { MatIcon, MatIconModule } from '@angular/material/icon';
import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';
import { MatSort, MatSortModule } from '@angular/material/sort';
import { MatSnackBar } from '@angular/material/snack-bar';
import {
  MatCell, MatCellDef,
  MatColumnDef,
  MatHeaderCell,
  MatHeaderCellDef,
  MatHeaderRow, MatHeaderRowDef,
  MatRow, MatRowDef,
  MatTable,
  MatTableDataSource
} from '@angular/material/table';
import { Router } from '@angular/router';
import { forkJoin } from 'rxjs/internal/observable/forkJoin';
import { ColumnHeaderMenuComponent } from '../../components/columns/column-header-menu.component';
import { ColumnPickerComponent } from '../../components/columns/column-picker.component';
import { CustomFieldDialogComponent } from '../../components/columns/custom-field-dialog.component';
import { customSortValue, customValue, formatCustomValue, TableColumn, TableColumns } from '../../components/columns/table-columns';
import { Contact } from '../../interfaces/Contact';
import { CustomFieldService } from '../../services/custom-field/custom-field.service';
import { ContactService } from '../../services/contact/contact.service';
import { DeleteCompanyModal } from '../companies/modals/Delete/delete-company-modal';
import { AddContactModal } from './modals/Add/add-contact-modal-component';
import { EditContactModal } from './modals/Edit/edit-contact-modal';

@Component({
  selector: 'app-contact',
  imports: [
    MatTable,
    MatColumnDef,
    MatHeaderCell,
    MatCell,
    MatHeaderRow,
    MatRow,
    MatHeaderCellDef,
    MatHeaderRowDef,
    MatRowDef,
    MatCellDef,
    MatPaginatorModule,
    MatSortModule,
    ColumnPickerComponent,
    ColumnHeaderMenuComponent,
    MatIcon,
    MatIconModule,
    MatButtonModule,
    MatCheckbox,
    ReactiveFormsModule
  ],
  templateUrl: './contacts.component.html',
  styleUrl: './contacts.component.scss'
})
export class ContactsComponent implements AfterViewInit {
  private readonly formBuilder = inject(FormBuilder)
  private readonly contactService = inject(ContactService);
  private readonly _snackBar = inject(MatSnackBar);
  private readonly router = inject(Router);

  readonly dialog = inject(MatDialog);

  contacts = this.contactService.contacts;
  canDeleteContacts = this.contactService.canDeleteContacts;
  canClearSelection = this.contactService.canClearSelection;

  readonly columns = new TableColumns('spacemarket.columns.contacts', [
    { id: 'firstName', label: 'First name' },
    { id: 'lastName', label: 'Last name' },
    { id: 'company', label: 'Company' },
    { id: 'emails', label: 'Emails' },
    { id: 'phones', label: 'Phones' },
    { id: 'city', label: 'City' },
    { id: 'address', label: 'Address' },
    { id: 'country', label: 'Country' },
  ], inject(CustomFieldService).fields('CONTACT'));
  readonly displayedColumns = computed(() => ['select', ...this.columns.visibleIds(), 'add']);
  readonly dataSource = new MatTableDataSource<Contact>(this.contacts());
  readonly selection = new SelectionModel<Contact>(true, []);

  private readonly _currentYear = new Date().getFullYear();
  private readonly _currentMonth = new Date().getMonth();
  private readonly _currentDay = new Date().getDate();
  private readonly maxDate = new Date(this._currentYear, this._currentMonth, this._currentDay);

  constructor() {
    effect(() => {
      this.dataSource.data = this.contacts();
      if (this.canDeleteContacts()) {
        this.confirmDeleteContact();
      }
      if (this.canClearSelection()) {
        this.selection.clear();
      }
    });
  }

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  ngAfterViewInit() {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sortingDataAccessor = (contact, columnId) => {
      const field = this.columns.all().find(c => c.id === columnId)?.field;
      if (field) return customSortValue(field, customValue(contact, field));
      return this.displayColumn(contact, columnId).toLowerCase();
    };
    this.dataSource.sort = this.sort;
  }

  addColumn() {
    this.dialog.open(CustomFieldDialogComponent, { data: { target: 'CONTACT' } });
  }

  display(contact: Contact, column: TableColumn): string {
    return column.field ? formatCustomValue(column.field, customValue(contact, column.field)) : this.displayColumn(contact, column.id);
  }

  isAllSelected() {
    const numSelected = this.selection.selected.length;
    const numRows = this.dataSource.data.length;
    return numSelected === numRows;
  }
  toggleAllRows() {
    if (this.isAllSelected()) {
      this.selection.clear();
      return;
    }
    this.selection.select(...this.dataSource.data);
  }
  openAddDialog() {
    this.dialog.open(AddContactModal);
  }
  openDeleteDialog() {
    if (this.selection.selected) {
      this.dialog.open(DeleteCompanyModal);
    }
  }
  openEditDialog() {
    if (this.selection.selected) {
      this.dialog.open(EditContactModal, { data: this.selection.selected[0] });
    }
  }
  confirmDeleteContact() {
    const contactObs = this.selection.selected.map(contact => this.contactService.deleteContactById(contact.id));
    forkJoin(
      contactObs
    ).subscribe({
      error: (error) => {
        console.error('Error deleting contacts: ', error);
      },
      complete: () => {
        this.selection.clear();
        this.dataSource.data = this.contactService.contacts();
      }
    })
  }
  getContacts() {
    this.contactService.getContacts().subscribe({
      next: (reponse) => {
        this.dataSource.data = reponse.data;
      },
      error: (error) => {
        console.error('Error fetching contacts: ', error);
      }
    });
  }

  goToDetailsPage(contact?: Contact) {
    const selectedContact: Contact | undefined = contact ?? this.selection.selected[0];
    if (!selectedContact) {
      return;
    }
    this.router.navigate(['/contact', selectedContact.id], {
      state: { contact: selectedContact }
    });
  }

  isArray(value: any): boolean {
    return Array.isArray(value);
  }

  isObject(value: any): boolean {
    return typeof value === 'object' && value !== null
  }

  getArrayItems(arr: any[]): string[] {
    if (!arr || arr.length === 0) return [];

    return arr.map(item => {
      if (typeof item === 'object') {
        return item.email || item.phone || item.name || '';
      }
      return String(item);
    }).filter(Boolean);
  }

  displayColumn(element: any, column: string): string {
    const value = element[column];
    if (value === null || value === undefined) {
      return '-';
    }
    if (Array.isArray(value)) {
      return this.getArrayItems(value).join(', ');
    }
    if (typeof value === 'object') {
      return value.name ?? (value.toString ? value.toString() : '-');
    }
    return String(value);
  }

  displayContact(contact: Contact): string {
    return contact ? `${contact.firstName} ${contact.lastName}` : '';
  }

}
