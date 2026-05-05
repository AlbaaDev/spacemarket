import { Component, inject, OnInit } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { Contact } from '../../../interfaces/Contact';
import { MatCard, MatCardContent } from '@angular/material/card';


@Component({
  selector: 'app-contact',
  imports: [
    MatCard,
    MatCardContent,
    ReactiveFormsModule
  ],
  templateUrl: './contact.details.component.html',
  styleUrl: './contact.details.component.scss'
})
export class ContactDetailsComponent implements OnInit {
  contact!: Contact;
  readonly dialog = inject(MatDialog);

  constructor() { }
  ngOnInit(): void {
    this.contact = history.state?.contact ? history.state?.contact :  history.state?.selectedContact ;
  }
}
