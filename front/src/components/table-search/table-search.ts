import { SelectionModel } from '@angular/cdk/collections';
import { effect, inject, signal, Signal, untracked } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatTableDataSource } from '@angular/material/table';
import { ActivatedRoute, Router } from '@angular/router';
import { TableColumn, TableColumns } from '../columns/table-columns';

const QUERY_PARAM = 'q';

/** Lowercases and strips accents, so "Éric" and "eric" compare equal. */
export function normalizeForSearch(text: string): string {
  return text.normalize('NFD').replace(/\p{M}/gu, '').toLowerCase();
}

/** True when every word of the Search appears somewhere in the row's cells. */
export function matchesSearch(cells: string[], search: string): boolean {
  const words = normalizeForSearch(search).split(/\s+/).filter(Boolean);
  if (words.length === 0) return true;
  const row = normalizeForSearch(cells.join('\n'));
  return words.every(word => row.includes(word));
}

/**
 * A table's Search: narrows the rows to those whose visible Columns, as displayed, contain every
 * word typed. The Search lives in the page URL (?q=) so it survives a visit to a detail page.
 * Changing it clears the selection, so no hidden row stays selected, and returns to the first page.
 */
export class TableSearch<T> {
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);
  private readonly _text = signal(this.route.snapshot.queryParamMap.get(QUERY_PARAM) ?? '');

  readonly text: Signal<string> = this._text.asReadonly();

  constructor(
    private readonly dataSource: MatTableDataSource<T>,
    columns: TableColumns,
    display: (row: T, column: TableColumn) => string,
    private readonly selection: SelectionModel<T>,
    private readonly paginator: () => MatPaginator | undefined,
  ) {
    dataSource.filterPredicate = (row, search) =>
      matchesSearch(columns.visible().map(column => display(row, column)), search);
    // Re-filter when the Search or the visible Columns change.
    effect(() => {
      columns.visible();
      const text = this._text();
      untracked(() => this.dataSource.filter = text);
    });
  }

  /** Rows currently shown by the Search, across all pages. */
  get rows(): T[] {
    return this.dataSource.filteredData;
  }

  set(text: string) {
    if (text === this._text()) return;
    this._text.set(text);
    this.selection.clear();
    this.paginator()?.firstPage();
    void this.router.navigate([], {
      relativeTo: this.route,
      queryParams: { [QUERY_PARAM]: text || null },
      queryParamsHandling: 'merge',
      replaceUrl: true,
    });
  }
}
