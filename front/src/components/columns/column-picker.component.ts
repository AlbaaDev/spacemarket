import { CdkDrag, CdkDragDrop, CdkDragHandle, CdkDropList } from '@angular/cdk/drag-drop';
import { ChangeDetectionStrategy, Component, inject, input } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatDialog } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { CustomFieldTarget } from '../../interfaces/CustomField';
import { CustomFieldDialogComponent } from './custom-field-dialog.component';
import { TableColumns } from './table-columns';

/** "Columns" menu: show, hide and reorder a table's columns, or add a Custom field. */
@Component({
  selector: 'app-column-picker',
  imports: [MatButtonModule, MatCheckboxModule, MatIconModule, MatMenuModule, CdkDropList, CdkDrag, CdkDragHandle],
  template: `
    <button mat-stroked-button type="button" [matMenuTriggerFor]="menu" aria-label="Choose columns">
      <mat-icon>view_column</mat-icon> Columns
    </button>
    <mat-menu #menu="matMenu" class="column-picker-menu" xPosition="before">
      <div class="picker" (click)="$event.stopPropagation()" (keydown.tab)="$event.stopPropagation()">
        <p class="hint">Drag to reorder. Hidden columns keep their data.</p>
        <ul cdkDropList class="list" (cdkDropListDropped)="drop($event)">
          @for (column of columns().all(); track column.id) {
            <li cdkDrag class="item" cdkDragLockAxis="y">
              <mat-icon cdkDragHandle class="handle" aria-hidden="true">drag_indicator</mat-icon>
              <mat-checkbox [checked]="columns().isVisible(column.id)" (change)="columns().toggle(column.id)">
                {{ column.label }}
              </mat-checkbox>
              @if (column.field) { <span class="custom-tag">custom</span> }
            </li>
          }
        </ul>
        <div class="actions">
          <button mat-button type="button" (click)="addColumn()"><mat-icon>add</mat-icon> Add a column</button>
          <button mat-button type="button" (click)="columns().reset()">Reset</button>
        </div>
      </div>
    </mat-menu>
  `,
  styles: `
    .picker { padding: var(--space-2) var(--space-3); min-width: 16rem; }
    .hint { margin: 0 0 var(--space-2); font-size: var(--text-xs); color: var(--mat-sys-on-surface-variant); }
    .list { list-style: none; margin: 0; padding: 0; }
    .item {
      display: flex; align-items: center; gap: var(--space-1);
      background: var(--mat-sys-surface-container);
      border-radius: 4px;
    }
    .handle { cursor: grab; color: var(--mat-sys-on-surface-variant); }
    .custom-tag { margin-left: auto; font-size: var(--text-xs); color: var(--mat-sys-on-surface-variant); padding-right: var(--space-2); }
    .actions { display: flex; justify-content: space-between; border-top: 1px solid var(--mat-sys-outline-variant); margin-top: var(--space-2); padding-top: var(--space-2); }
    .cdk-drag-preview { box-shadow: 0 2px 8px rgb(0 0 0 / 0.2); }
    .cdk-drag-placeholder { opacity: 0.3; }
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ColumnPickerComponent {
  readonly columns = input.required<TableColumns>();
  readonly target = input.required<CustomFieldTarget>();
  private readonly dialog = inject(MatDialog);

  drop(event: CdkDragDrop<unknown>) {
    this.columns().move(event.previousIndex, event.currentIndex);
  }

  addColumn() {
    this.dialog.open(CustomFieldDialogComponent, { data: { target: this.target() } });
  }
}
