import { SelectionModel } from '@angular/cdk/collections';
import { Component, inject, ViewChild } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckbox } from '@angular/material/checkbox';
import { MatIcon, MatIconModule } from '@angular/material/icon';
import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';
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
import { Contact, ContactKeys } from '../../interfaces/Contact';
import { ContactService } from '../../services/contact/contact.service';
import { forkJoin } from 'rxjs/internal/observable/forkJoin';

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
    MatCheckbox
  ],
  templateUrl: './contact.component.html',
  styleUrl: './contact.component.css'
})
export class ContactComponent {
  private readonly contactService = inject(ContactService);
  dataTable: Contact[] = [];
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
  readonly dataSource = new MatTableDataSource<Contact>(this.dataTable);
  readonly selection = new SelectionModel<Contact>(true, []);
  @ViewChild(MatPaginator) paginator!: MatPaginator;

  ngAfterViewInit() {
    this.dataSource.paginator = this.paginator;
    this.getContacts();

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
  deleteSelected() {
    const contactObs = this.selection.selected.map(contact => this.contactService.deleteContactById(contact.id));
    forkJoin (
      contactObs
    ).subscribe({
      error: (error) => {
        console.error('Error deleting contacts:', error);
      },
      complete: () => { 
        console.log()
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
        console.error('Error fetching contacts:', error);
      }
    });
  }
}


