import { ChangeDetectionStrategy, Component, effect, inject, input } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { CustomFieldTarget, CustomValues } from '../../interfaces/CustomField';
import { CustomFieldService } from '../../services/custom-field/custom-field.service';
import { fromIsoDate, toIsoDate } from '../../utils/dates';

/**
 * Inputs for a record's Custom fields, added as controls (one per field id) to the given group.
 * Read the group back with `customValuesFrom`.
 */
@Component({
  selector: 'app-custom-value-fields',
  imports: [ReactiveFormsModule, MatCheckboxModule, MatDatepickerModule, MatFormFieldModule, MatInputModule, MatSelectModule],
  template: `
    @if (fields().length > 0) {
      <fieldset class="custom" [formGroup]="group()">
        <legend>Custom fields</legend>
        @for (field of fields(); track field.id) {
          @switch (field.type) {
            @case ('CHECKBOX') {
              <mat-checkbox [formControlName]="'' + field.id">{{ field.name }}</mat-checkbox>
            }
            @case ('SINGLE_CHOICE') {
              <mat-form-field>
                <mat-label>{{ field.name }}</mat-label>
                <mat-select [formControlName]="'' + field.id">
                  <mat-option [value]="null">–</mat-option>
                  @for (option of field.options; track option) {
                    <mat-option [value]="option">{{ option }}</mat-option>
                  }
                </mat-select>
              </mat-form-field>
            }
            @case ('DATE') {
              <mat-form-field>
                <mat-label>{{ field.name }}</mat-label>
                <input matInput [matDatepicker]="picker" [formControlName]="'' + field.id">
                <mat-datepicker-toggle matIconSuffix [for]="picker"></mat-datepicker-toggle>
                <mat-datepicker #picker></mat-datepicker>
              </mat-form-field>
            }
            @case ('NUMBER') {
              <mat-form-field>
                <mat-label>{{ field.name }}</mat-label>
                <input matInput type="number" [formControlName]="'' + field.id">
              </mat-form-field>
            }
            @default {
              <mat-form-field>
                <mat-label>{{ field.name }}</mat-label>
                <input matInput maxlength="500" [formControlName]="'' + field.id">
              </mat-form-field>
            }
          }
        }
      </fieldset>
    }
  `,
  styles: `
    .custom { display: flex; flex-direction: column; border: 0; padding: 0; margin: var(--space-3) 0 0; min-width: 0; }
    legend { font-size: var(--text-sm); font-weight: 600; color: var(--mat-sys-on-surface-variant); margin-bottom: var(--space-2); }
    mat-checkbox { margin-bottom: var(--space-3); }
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CustomValueFieldsComponent {
  readonly target = input.required<CustomFieldTarget>();
  readonly group = input.required<FormGroup>();
  readonly values = input<CustomValues | null | undefined>(undefined);

  private readonly service = inject(CustomFieldService);
  protected readonly fields = () => this.service.fields(this.target())();

  constructor() {
    // Fields may arrive after the dialog opens: add a control for each as it appears.
    effect(() => {
      const group = this.group();
      for (const field of this.fields()) {
        const key = String(field.id);
        if (group.contains(key)) continue;
        const value = this.values()?.[key];
        const initial = value === undefined ? null : field.type === 'DATE' ? fromIsoDate(String(value)) : value;
        group.addControl(key, new FormControl(field.type === 'CHECKBOX' ? !!initial : initial));
      }
    });
  }
}

/** The group's values ready to send: dates as YYYY-MM-DD, empty inputs left out. */
export function customValuesFrom(group: FormGroup): CustomValues {
  const values: CustomValues = {};
  for (const [key, value] of Object.entries(group.getRawValue() as Record<string, unknown>)) {
    if (value === null || value === undefined || value === '' || value === false) continue;
    values[key] = value instanceof Date ? toIsoDate(value) : value as string | number | boolean;
  }
  return values;
}
