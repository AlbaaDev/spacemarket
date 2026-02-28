import { Component, inject, OnInit } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { Company } from '../../../interfaces/Company';


@Component({
  selector: 'app-contact',
  imports: [
    ReactiveFormsModule
  ],
  templateUrl: './company.details.component.html',
  styleUrl: './company.details.component.scss'
})
export class CompanyDetailsComponent implements OnInit {
  company!: Company;
  readonly dialog = inject(MatDialog);

  constructor() { }
  ngOnInit(): void {
    this.company = history.state?.company ? history.state?.company : history.state?.selectedCompany;
  }
}
