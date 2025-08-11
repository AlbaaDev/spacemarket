import { SelectionModel } from '@angular/cdk/collections';
import { Component, ViewChild } from '@angular/core';
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

  readonly dataTable: Contact[] = [
    { firstName: 'Toto',     lastName: 'Titi', email: 'titi@live.fr', phoneNumber: '0638154689', birthDay: '24.12.1990', city: 'Paris', adress: 'ABC', country: 'France' },
    { firstName: 'gdgdfgdg', lastName: 'Titi', email: 'titi@live.fr', phoneNumber: '0638154689', birthDay: '24.12.1990', city: 'Paris', adress: 'ABC', country: 'France' },
    { firstName: 'gdgdfgdg', lastName: 'Titi', email: 'titi@live.fr', phoneNumber: '0638154689', birthDay: '24.12.1990', city: 'Paris', adress: 'ABC', country: 'France' },
    { firstName: 'gdgdfgdg', lastName: 'Titi', email: 'titi@live.fr', phoneNumber: '0638154689', birthDay: '24.12.1990', city: 'Paris', adress: 'ABC', country: 'France' },
    { firstName: 'gdgdfgdg', lastName: 'Titi', email: 'titi@live.fr', phoneNumber: '0638154689', birthDay: '24.12.1990', city: 'Paris', adress: 'ABC', country: 'France' },
    { firstName: 'gettfgdg', lastName: 'Titi', email: 'titi@live.fr', phoneNumber: '0638154689', birthDay: '24.12.1990', city: 'Paris', adress: 'ABC', country: 'France' },
    { firstName: 'Tetesert', lastName: 'Titi', email: 'titi@live.fr', phoneNumber: '0638154689', birthDay: '24.12.1990', city: 'Paris', adress: 'ABC', country: 'France' },
    { firstName: 'Tetewert', lastName: 'Titi', email: 'titi@live.fr', phoneNumber: '0638154689', birthDay: '24.12.1990', city: 'Paris', adress: 'ABC', country: 'France' },
  ];
  readonly columns = {
    firstName: 'First name',
    lastName: 'Last name',
    email: 'Email',
    phoneNumber: 'Phone number',
    birthDay: 'Birth day',
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
    this.selection.clear();
  }
}


