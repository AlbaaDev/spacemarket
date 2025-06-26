import { Component } from '@angular/core';
import {
  MatCell, MatCellDef,
  MatColumnDef,
  MatHeaderCell,
  MatHeaderCellDef,
  MatHeaderRow, MatHeaderRowDef,
  MatRow, MatRowDef,
  MatTable
} from '@angular/material/table';
import {PeriodicElement} from '../../interfaces/PeriodicElement';

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
    MatCellDef
  ],
  templateUrl: './contact.component.html',
  styleUrl: './contact.component.css'
})
export class ContactComponent {
  displayedColumns: string[] = ['position', 'name', 'weight', 'symbol'];
  dataSource = ELEMENT_DATA;
}

const ELEMENT_DATA: PeriodicElement[] = [
  {name: "toto", phone: "+33645456456", email: "toto@live.fr", tags: 'tag1'},
  {name: "titi", phone: "+336457896456", email: "titi@live.fr", tags: 'tag2'},
];

