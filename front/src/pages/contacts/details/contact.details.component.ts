import { Component, inject, OnInit } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute, Router } from '@angular/router';
import { Contact } from '../../../interfaces/Contact';


@Component({
  selector: 'app-contact',
  imports: [
    ReactiveFormsModule
  ],
  templateUrl: './contact.details.component.html',
  styleUrl: './contact.details.component.scss'
})
export class ContactDetailsComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  contact!: Contact;
  readonly dialog = inject(MatDialog);

  constructor() { }
  ngOnInit(): void {
    this.contact = history.state?.contact ? history.state?.contact :  history.state?.selectedContact ;
  }
}
