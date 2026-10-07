import { SelectionModel } from '@angular/cdk/collections';
import { AfterViewInit, Component, computed, effect, inject, OnInit, signal, ViewChild } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckbox } from '@angular/material/checkbox';
import { MatDialog } from '@angular/material/dialog';
import { MatIconModule } from "@angular/material/icon";
import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';
import { MatSort, MatSortModule } from '@angular/material/sort';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { forkJoin } from 'rxjs';
import { ColumnHeaderMenuComponent } from '../../components/columns/column-header-menu.component';
import { ColumnPickerComponent } from '../../components/columns/column-picker.component';
import { CustomFieldDialogComponent } from '../../components/columns/custom-field-dialog.component';
import { customSortValue, customValue, formatCustomValue, TableColumn, TableColumns } from '../../components/columns/table-columns';
import { ConfirmDialogComponent } from '../../components/confirm-dialog/confirm-dialog.component';
import { CustomFieldService } from '../../services/custom-field/custom-field.service';
import { Opportunity, OPPORTUNITY_STATUS_LABELS } from '../../interfaces/Opportunity';
import { OpportunityService } from '../../services/opportunity/opportunity.service';
import { fromIsoDate } from '../../utils/dates';
import { OpportunityFormModal } from './modal/opportunity-form-modal';
import { APP_LOCALE } from '../../utils/date-adapter';

type OpportunityColumn = 'name' | 'businessName' | 'principalContact' | 'value' | 'status' | 'closeDate';

@Component({
  selector: 'opportunity',
  imports: [MatTableModule, MatSortModule, MatPaginatorModule, MatCheckbox, MatIconModule, MatButtonModule, ColumnPickerComponent, ColumnHeaderMenuComponent],
  templateUrl: './opportunity.component.html',
  styleUrl: './opportunity.component.css'
})
export class OpportunityComponent implements OnInit, AfterViewInit {
  private readonly opportunityService = inject(OpportunityService);
  readonly dialog = inject(MatDialog);
  readonly opportunities = this.opportunityService.opportunities;
  readonly loadFailed = signal(false);

  readonly columns = new TableColumns('spacemarket.columns.opportunities', [
    { id: 'name', label: 'Name' },
    { id: 'businessName', label: 'Business name' },
    { id: 'principalContact', label: 'Principal contact' },
    { id: 'value', label: 'Value' },
    { id: 'status', label: 'Status' },
    { id: 'closeDate', label: 'Close date' },
  ], inject(CustomFieldService).fields('OPPORTUNITY'));
  readonly displayedColumns = computed(() => ['select', ...this.columns.visibleIds(), 'add']);
  readonly dataSource = new MatTableDataSource<Opportunity>([]);
  readonly selection = new SelectionModel<Opportunity>(true, [], true, (a, b) => a.id === b.id);

  private readonly money = new Intl.NumberFormat(APP_LOCALE, { style: 'currency', currency: 'EUR', maximumFractionDigits: 0 });
  private readonly day = new Intl.DateTimeFormat(APP_LOCALE, { dateStyle: 'medium' });

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  constructor() {
    effect(() => {
      this.dataSource.data = this.opportunities();
    });
    this.dataSource.sortingDataAccessor = (opportunity, columnId) => {
      const field = this.columns.all().find(c => c.id === columnId)?.field;
      if (field) return customSortValue(field, customValue(opportunity, field));
      const column = columnId as OpportunityColumn;
      switch (column) {
        case 'principalContact': return this.contactName(opportunity).toLowerCase();
        case 'closeDate': return opportunity.closeDate ?? '';
        case 'value': return opportunity.value;
        default: return String(opportunity[column] ?? '').toLowerCase();
      }
    };
  }

  ngOnInit() {
    this.opportunityService.getOpportunities().subscribe({ error: () => this.loadFailed.set(true) });
  }

  ngAfterViewInit() {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }

  isAllSelected() {
    return this.selection.selected.length === this.dataSource.data.length;
  }

  toggleAllRows() {
    if (this.isAllSelected()) {
      this.selection.clear();
      return;
    }
    this.selection.select(...this.dataSource.data);
  }

  openAddDialog() {
    this.dialog.open(OpportunityFormModal);
  }

  openEditDialog(opportunity: Opportunity | undefined = this.selection.selected[0]) {
    if (!opportunity) return;
    this.dialog.open(OpportunityFormModal, { data: { opportunity } }).afterClosed().subscribe(saved => {
      if (saved) this.selection.clear();
    });
  }

  openDeleteDialog() {
    const count = this.selection.selected.length;
    this.dialog.open(ConfirmDialogComponent, {
      data: {
        title: count === 1 ? 'Delete this opportunity?' : `Delete ${count} opportunities?`,
        message: 'Interactions linked to them are kept but no longer point to an opportunity.',
        confirmLabel: 'Delete',
      }
    }).afterClosed().subscribe(confirmed => {
      if (!confirmed) return;
      forkJoin(this.selection.selected.map(o => this.opportunityService.deleteOpportunity(o.id)))
        .subscribe({ complete: () => this.selection.clear() });
    });
  }

  addColumn() {
    this.dialog.open(CustomFieldDialogComponent, { data: { target: 'OPPORTUNITY' } });
  }

  display(opportunity: Opportunity, tableColumn: TableColumn): string {
    if (tableColumn.field) return formatCustomValue(tableColumn.field, customValue(opportunity, tableColumn.field));
    const column = tableColumn.id as OpportunityColumn;
    switch (column) {
      case 'principalContact': return this.contactName(opportunity);
      case 'value': return this.money.format(opportunity.value);
      case 'status': return OPPORTUNITY_STATUS_LABELS[opportunity.status];
      case 'closeDate': return opportunity.closeDate ? this.day.format(fromIsoDate(opportunity.closeDate)) : '–';
      default: return opportunity[column] ?? '–';
    }
  }

  private contactName(opportunity: Opportunity): string {
    const contact = opportunity.principalContact;
    return contact ? `${contact.firstName} ${contact.lastName}` : '';
  }
}
