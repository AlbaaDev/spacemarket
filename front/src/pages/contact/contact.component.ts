import { SelectionModel } from '@angular/cdk/collections';
import { Component, computed, effect, inject, ViewChild } from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckbox } from '@angular/material/checkbox';
import { MatDialog } from '@angular/material/dialog';
import { MatIcon, MatIconModule } from '@angular/material/icon';
import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';
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
import { forkJoin } from 'rxjs/internal/observable/forkJoin';
import { Contact, ContactKeys } from '../../interfaces/Contact';
import { ContactService } from '../../services/contact/contact.service';
import { AddContactModal } from './modal/add/add-modal-component';

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
    MatIcon,
    MatIconModule,
    MatButtonModule,
    MatCheckbox,
    ReactiveFormsModule
  ],
  templateUrl: './contact.component.html',
  styleUrl: './contact.component.css'
})
export class ContactComponent {
  private readonly formBuilder = inject(FormBuilder)
  private readonly contactService = inject(ContactService);
  private readonly _snackBar = inject(MatSnackBar);
  readonly dialog = inject(MatDialog);

  contacts = this.contactService.contacts;
  readonly columns = {
    firstName: 'First name',
    lastName: 'Last name',
    email: 'Email',
    phone: 'Phone number',
    birthDate: 'Birth date',
    city: 'City',
    adress: 'Adress',
    country: 'Country'
  };

  readonly dataColumns = Object.keys(this.columns) as ContactKeys[];
  readonly displayedColumns = ['select', ...this.dataColumns] as const;
  readonly dataSource = new MatTableDataSource<Contact>(this.contacts());
  readonly dataSourceComput = computed(() => this.dataSource);
  readonly selection = new SelectionModel<Contact>(true, []);

  private readonly _currentYear = new Date().getFullYear();
  private readonly _currentMonth = new Date().getMonth();
  private readonly _currentDay = new Date().getDay();
  private readonly maxDate = new Date(this._currentYear, this._currentMonth, this._currentDay);

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  ngAfterViewInit() {
    this.dataSource.paginator = this.paginator;
    this.getContacts();
  }

  constructor() {
    effect(() => {
      this.dataSource.data = this.contacts();
    });
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
  openDialog() {
    this.dialog.open(AddContactModal);
  }
  deleteSelected() {
    const contactObs = this.selection.selected.map(contact => this.contactService.deleteContactById(contact.id));
    forkJoin(
      contactObs
    ).subscribe({
      error: (error) => {
        console.error('Error deleting contactss: ', error);
      },
      complete: () => {
        this.selection.clear();
        this.getContacts();
      }
    })
  }
  getContacts() {
    this.contactService.getContacts().subscribe({
      next: (contacts) => {
        this.dataSource.data = contacts;
      },
      error: (error) => {
        console.error('Error fetching contacts: ', error);
      }
    });
  }
}