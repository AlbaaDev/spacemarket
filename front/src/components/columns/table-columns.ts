import { computed, Signal, signal } from '@angular/core';
import { CustomField, CustomValues } from '../../interfaces/CustomField';
import { fromIsoDate } from '../../utils/dates';
import { APP_LOCALE } from '../../utils/date-adapter';

/** A table column: a built-in attribute, or a Custom field (id `cf:<field id>`). */
export interface TableColumn {
  id: string;
  label: string;
  field?: CustomField;
}

interface ColumnPrefs {
  order: string[];
  hidden: string[];
}

const CUSTOM_PREFIX = 'cf:';
const number = new Intl.NumberFormat(APP_LOCALE, { maximumFractionDigits: 2 });
const day = new Intl.DateTimeFormat(APP_LOCALE, { dateStyle: 'medium' });

export function customColumnId(field: CustomField): string {
  return CUSTOM_PREFIX + field.id;
}

export function customValue(record: { customValues?: CustomValues | null }, field: CustomField) {
  return record.customValues?.[String(field.id)];
}

export function formatCustomValue(field: CustomField, value: unknown): string {
  if (value === undefined || value === null || value === '') return '–';
  switch (field.type) {
    case 'NUMBER': return number.format(Number(value));
    case 'DATE': return day.format(fromIsoDate(String(value)));
    case 'CHECKBOX': return value ? 'Yes' : 'No';
    default: return String(value);
  }
}

/** Sort key of a Custom field value; empty values sort last in ascending order. */
export function customSortValue(field: CustomField, value: unknown): string | number {
  if (value === undefined || value === null || value === '') return field.type === 'NUMBER' ? Number.MAX_VALUE : '￿';
  switch (field.type) {
    case 'NUMBER': return Number(value);
    case 'CHECKBOX': return value ? 0 : 1;
    default: return String(value).toLowerCase();
  }
}

/**
 * Which columns a table shows, and in which order. Showing, hiding or reordering a column
 * never changes the data; the choice is remembered per table in this browser.
 */
export class TableColumns {
  private readonly prefs;

  readonly all: Signal<TableColumn[]>;
  readonly visible: Signal<TableColumn[]>;
  readonly visibleIds: Signal<string[]>;

  constructor(
    private readonly storageKey: string,
    builtIns: TableColumn[],
    customFields: Signal<CustomField[]>,
  ) {
    this.prefs = signal<ColumnPrefs>(this.load());
    this.all = computed(() => {
      const columns = [
        ...builtIns,
        ...customFields().map(field => ({ id: customColumnId(field), label: field.name, field })),
      ];
      const order = this.prefs().order;
      // Columns never placed by the user (e.g. a new Custom field) keep their natural place at the end.
      const rank = (column: TableColumn) => {
        const index = order.indexOf(column.id);
        return index === -1 ? order.length + columns.indexOf(column) : index;
      };
      return [...columns].sort((a, b) => rank(a) - rank(b));
    });
    this.visible = computed(() => this.all().filter(column => !this.prefs().hidden.includes(column.id)));
    this.visibleIds = computed(() => this.visible().map(column => column.id));
  }

  isVisible(id: string): boolean {
    return !this.prefs().hidden.includes(id);
  }

  toggle(id: string) {
    this.update(prefs => ({
      ...prefs,
      hidden: prefs.hidden.includes(id) ? prefs.hidden.filter(h => h !== id) : [...prefs.hidden, id],
    }));
  }

  move(previousIndex: number, currentIndex: number) {
    const order = this.all().map(column => column.id);
    const [moved] = order.splice(previousIndex, 1);
    order.splice(currentIndex, 0, moved);
    this.update(prefs => ({ ...prefs, order }));
  }

  reset() {
    this.update(() => ({ order: [], hidden: [] }));
  }

  private update(change: (prefs: ColumnPrefs) => ColumnPrefs) {
    this.prefs.update(change);
    try {
      localStorage.setItem(this.storageKey, JSON.stringify(this.prefs()));
    } catch {
      // Storage unavailable: the layout lasts for this visit only.
    }
  }

  private load(): ColumnPrefs {
    try {
      const stored = JSON.parse(localStorage.getItem(this.storageKey) ?? 'null');
      if (Array.isArray(stored?.order) && Array.isArray(stored?.hidden)) {
        return { order: stored.order, hidden: stored.hidden };
      }
    } catch {
      // Ignore unreadable preferences.
    }
    return { order: [], hidden: [] };
  }
}
