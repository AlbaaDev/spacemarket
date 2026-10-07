import { ChangeDetectionStrategy, Component, inject, input } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { ConfirmDialogComponent } from '../confirm-dialog/confirm-dialog.component';
import { CustomField } from '../../interfaces/CustomField';
import { CustomFieldService } from '../../services/custom-field/custom-field.service';
import { CustomFieldDialogComponent } from './custom-field-dialog.component';
import { customColumnId, TableColumns } from './table-columns';

/** The ⋮ menu in a Custom field's column header: edit, hide or delete the field. */
@Component({
  selector: 'app-column-header-menu',
  imports: [MatButtonModule, MatIconModule, MatMenuModule],
  template: `
    <button mat-icon-button type="button" class="trigger" [matMenuTriggerFor]="menu"
      (click)="$event.stopPropagation()" [attr.aria-label]="'Options for column ' + field().name">
      <mat-icon>more_vert</mat-icon>
    </button>
    <mat-menu #menu="matMenu">
      <button mat-menu-item type="button" (click)="edit()"><mat-icon>edit</mat-icon>Edit column</button>
      <button mat-menu-item type="button" (click)="columns().toggle(columnId())"><mat-icon>visibility_off</mat-icon>Hide column</button>
      <button mat-menu-item type="button" class="danger" (click)="delete()"><mat-icon>delete</mat-icon>Delete column…</button>
    </mat-menu>
  `,
  styles: `
    :host { display: inline-flex; vertical-align: middle; }
    .trigger { --mat-icon-button-state-layer-size: 28px; padding: 2px; width: 28px; height: 28px; }
    .trigger mat-icon { font-size: 18px; width: 18px; height: 18px; }
    .danger, .danger mat-icon { color: var(--mat-sys-error); }
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ColumnHeaderMenuComponent {
  readonly field = input.required<CustomField>();
  readonly columns = input.required<TableColumns>();
  private readonly dialog = inject(MatDialog);
  private readonly service = inject(CustomFieldService);

  columnId() {
    return customColumnId(this.field());
  }

  edit() {
    this.dialog.open(CustomFieldDialogComponent, { data: { target: this.field().target, field: this.field() } });
  }

  delete() {
    const field = this.field();
    this.dialog.open(ConfirmDialogComponent, {
      data: {
        title: `Delete the column "${field.name}"?`,
        message: 'Every value entered in this column is deleted too. This cannot be undone.',
        confirmLabel: 'Delete column',
      },
    }).afterClosed().subscribe(confirmed => {
      if (confirmed) this.service.deleteField(field).subscribe();
    });
  }
}
