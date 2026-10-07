import { SelectionModel } from '@angular/cdk/collections';
import { AfterViewInit, Component, computed, effect, inject, ViewChild } from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckbox } from "@angular/material/checkbox";
import { MatDialog } from '@angular/material/dialog';
import { MatIcon, MatIconModule } from "@angular/material/icon";
import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';
import { MatSort, MatSortModule } from '@angular/material/sort';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatCell, MatCellDef, MatColumnDef, MatHeaderCell, MatHeaderCellDef, MatHeaderRow, MatHeaderRowDef, MatRow, MatRowDef, MatTable, MatTableDataSource } from '@angular/material/table';
import { Router } from '@angular/router';
import { forkJoin } from 'rxjs';
import { ColumnHeaderMenuComponent } from '../../components/columns/column-header-menu.component';
import { ColumnPickerComponent } from '../../components/columns/column-picker.component';
import { CustomFieldDialogComponent } from '../../components/columns/custom-field-dialog.component';
import { customSortValue, customValue, formatCustomValue, TableColumn, TableColumns } from '../../components/columns/table-columns';
import { Company } from '../../interfaces/Company';
import { CustomFieldService } from '../../services/custom-field/custom-field.service';
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
    MatSortModule,
    ColumnPickerComponent,
    ColumnHeaderMenuComponent,
    MatIcon,
    MatIconModule,
    MatButtonModule,
    MatCheckbox,
    ReactiveFormsModule],
  templateUrl: './companies.component.html',
  styleUrl: './companies.component.scss'
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

  readonly columns = new TableColumns('spacemarket.columns.companies', [
    { id: 'name', label: 'Name' },
    { id: 'city', label: 'City' },
    { id: 'address', label: 'Address' },
    { id: 'country', label: 'Country' },
    { id: 'industry', label: 'Industry' },
  ], inject(CustomFieldService).fields('COMPANY'));

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

  readonly displayedColumns = computed(() => ['select', ...this.columns.visibleIds(), 'add']);
  readonly dataSource = new MatTableDataSource<Company>(this.companies());
  readonly selection = new SelectionModel<Company>(true, []);

  private readonly _currentYear = new Date().getFullYear();
  private readonly _currentMonth = new Date().getMonth();
  private readonly _currentDay = new Date().getDate();
  private readonly maxDate = new Date(this._currentYear, this._currentMonth, this._currentDay);

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  ngAfterViewInit() {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sortingDataAccessor = (company, columnId) => {
      const field = this.columns.all().find(c => c.id === columnId)?.field;
      if (field) return customSortValue(field, customValue(company, field));
      return String(company[columnId as keyof Company] ?? '').toLowerCase();
    };
    this.dataSource.sort = this.sort;
  }

  addColumn() {
    this.dialog.open(CustomFieldDialogComponent, { data: { target: 'COMPANY' } });
  }

  display(company: Company, column: TableColumn): string {
    if (column.field) return formatCustomValue(column.field, customValue(company, column.field));
    const value = company[column.id as keyof Company];
    return value === null || value === undefined || value === '' ? '-' : String(value);
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
