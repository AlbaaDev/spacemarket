import { ChangeDetectionStrategy, Component, input } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { TableSearch } from './table-search';

/** Search box above a table: a magnifier, the typed text, and a button (or Escape) to clear it. */
@Component({
  selector: 'app-table-search-field',
  imports: [MatButtonModule, MatIconModule],
  template: `
    <label class="field">
      <mat-icon aria-hidden="true" class="magnifier">search</mat-icon>
      <input type="search" [placeholder]="placeholder()" [attr.aria-label]="placeholder()"
        [value]="search().text()" (input)="search().set(input.value)" (keydown.escape)="search().set('')" #input />
      @if (search().text()) {
        <button mat-icon-button type="button" class="clear" (click)="search().set(''); input.focus()" aria-label="Clear the search">
          <mat-icon>close</mat-icon>
        </button>
      }
    </label>
  `,
  styles: `
    :host { flex: 1 1 12rem; max-width: 22rem; min-width: 0; }
    .field {
      display: flex; align-items: center; gap: var(--space-1);
      height: 40px; padding: 0 var(--space-1) 0 var(--space-2);
      border: 1px solid var(--mat-sys-outline-variant);
      border-radius: 999px;
      background: var(--mat-sys-surface-container-lowest);
      cursor: text;
    }
    .field:focus-within { border-color: var(--mat-sys-primary); outline: 1px solid var(--mat-sys-primary); }
    .magnifier { color: var(--mat-sys-on-surface-variant); flex: none; }
    input {
      flex: 1; min-width: 0; border: 0; outline: 0; background: transparent;
      font: inherit; color: var(--mat-sys-on-surface);
    }
    input::-webkit-search-cancel-button { display: none; }
    .clear { flex: none; --mat-icon-button-state-layer-size: 32px; padding: 4px; }
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TableSearchFieldComponent {
  readonly search = input.required<Pick<TableSearch<unknown>, 'text' | 'set'>>();
  readonly placeholder = input('Search');
}
