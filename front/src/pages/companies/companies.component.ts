import { SelectionModel } from '@angular/cdk/collections';
import { AfterViewInit, Component, effect, inject, ViewChild } from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckbox } from "@angular/material/checkbox";
import { MatDialog } from '@angular/material/dialog';
import { MatIcon, MatIconModule } from "@angular/material/icon";
import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatCell, MatCellDef, MatColumnDef, MatHeaderCell, MatHeaderCellDef, MatHeaderRow, MatHeaderRowDef, MatRow, MatRowDef, MatTable, MatTableDataSource } from '@angular/material/table';
import { Router } from '@angular/router';
import { forkJoin } from 'rxjs';
import { Company, CompanyKeys } from '../../interfaces/Company';
import { CompanyService } from '../../services/company/company.service';
import { AddCompanyModal } from './modals/Add/add-company-modal';
import { DeleteCompanyModal } from './modals/Delete/delete-company-modal';
import { EditCompanyModal } from './modals/Edit/edit-company-modal';

@Component({
  selector: 'companies',
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
    ReactiveFormsModule],
  templateUrl: './companies.component.html',
  styleUrl: './companies.component.css'
})
export class CompaniesComponent implements AfterViewInit {
  private readonly formBuilder = inject(FormBuilder)
  private readonly companyService = inject(CompanyService);
  private readonly _snackBar = inject(MatSnackBar);
  private readonly router = inject(Router);

  readonly dialog = inject(MatDialog);

  companies = this.companyService.companies;
  canDeleteCompanies = this.companyService.canDeleteCompanies;
  canClearSelection = this.companyService.canClearSelection;

  readonly columns = {
    name: 'Name',
    city: 'City',
    address: 'Address',
    country: 'Country',
    industry: 'Industry',
  };

  isArray(value: any): boolean {
    return Array.isArray(value);
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

  readonly dataColumns = Object.keys(this.columns) as CompanyKeys[];
  readonly displayedColumns = ['select', ...this.dataColumns] as const;
  readonly dataSource = new MatTableDataSource<Company>(this.companies());
  readonly selection = new SelectionModel<Company>(true, []);

  private readonly _currentYear = new Date().getFullYear();
  private readonly _currentMonth = new Date().getMonth();
  private readonly _currentDay = new Date().getDate();
  private readonly maxDate = new Date(this._currentYear, this._currentMonth, this._currentDay);

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  ngAfterViewInit() {
    this.dataSource.paginator = this.paginator;
  }
  constructor() {
    effect(() => {
      this.dataSource.data = this.companies();
      if (this.canDeleteCompanies()) {
        this.confirmDeleteCompany();
      }
      if (this.canClearSelection()) {
        this.selection.clear();
      }
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
  openAddDialog() {
    this.dialog.open(AddCompanyModal);
  }
  openDeleteDialog() {
    if (this.selection.selected) {
      this.dialog.open(DeleteCompanyModal);
    }
  }
  openEditDialog() {
    if (this.selection.selected) {
      this.dialog.open(EditCompanyModal, { data: this.selection.selected[0] });
    }
  }
  confirmDeleteCompany() {
    const companyObs = this.selection.selected.map(company => this.companyService.deleteCompanyById(company.id));
    forkJoin(
      companyObs
    ).subscribe({
      error: (error) => {
        console.error('Error deleting companies: ', error);
      },
      complete: () => {
        this.selection.clear();
        this.dataSource.data = this.companyService.companies();
      }
    })
  }
  // getCompanies() {
  //   this.companyService.getCompanies().subscribe({
  //     next: (response) => {
  //       this.dataSource.data = response.data;
  //     },
  //     error: (error) => {
  //       console.error('Error fetching companies: ', error);
  //     }
  //   });
  // }
  
  goToDetailsPage(company?: Company) {
    const selectedCompany: Company | undefined = company ?? this.selection.selected[0];
    if (!selectedCompany) {
      return;
    }
    this.router.navigate(['/company', selectedCompany.id], {
      state: { company: selectedCompany }
    });
  }
}
