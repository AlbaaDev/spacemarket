import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { HttpErrorResponse } from '@angular/common/http';
import { CUSTOM_FIELD_TYPES, CustomField, CustomFieldTarget, CustomFieldType } from '../../interfaces/CustomField';
import { CustomFieldService } from '../../services/custom-field/custom-field.service';

export interface CustomFieldDialogData {
  target: CustomFieldTarget;
  /** Present when editing; its type cannot change. */
  field?: CustomField;
}

/** Creates or edits a Custom field. Closes with the saved field. */
@Component({
  selector: 'app-custom-field-dialog',
  imports: [MatDialogModule, MatButtonModule, MatFormFieldModule, MatIconModule, MatInputModule, MatSelectModule, ReactiveFormsModule],
  template: `
    <h2 mat-dialog-title>{{ data.field ? 'Edit column' : 'Add a column' }}</h2>
    <form [formGroup]="form" (ngSubmit)="submit()">
      <mat-dialog-content class="fields">
        <mat-form-field>
          <mat-label>Name</mat-label>
          <input matInput formControlName="name" maxlength="64" required cdkFocusInitial>
          <mat-error>Give the column a name</mat-error>
        </mat-form-field>

        <mat-form-field>
          <mat-label>Type</mat-label>
          <mat-select formControlName="type">
            @for (type of types; track type.value) {
              <mat-option [value]="type.value"><mat-icon aria-hidden="true">{{ type.icon }}</mat-icon>{{ type.label }}</mat-option>
            }
          </mat-select>
          @if (data.field) {
            <mat-hint>The type is fixed once the column exists.</mat-hint>
          }
        </mat-form-field>

        @if (type() === 'SINGLE_CHOICE') {
          <mat-form-field>
            <mat-label>Choices</mat-label>
            <textarea matInput formControlName="options" rows="4" required></textarea>
            <mat-hint>One per line. Removing a choice clears it wherever it was picked.</mat-hint>
            <mat-error>Add at least one choice</mat-error>
          </mat-form-field>
        }

        @if (error()) {
          <p class="form-error" role="alert">{{ error() }}</p>
        }
      </mat-dialog-content>
      <mat-dialog-actions align="end">
        <button mat-button type="button" mat-dialog-close>Cancel</button>
        <button mat-flat-button type="submit" [disabled]="saving()">{{ data.field ? 'Save' : 'Add column' }}</button>
      </mat-dialog-actions>
    </form>
  `,
  styles: `
    .fields { display: flex; flex-direction: column; gap: var(--space-2); min-width: min(24rem, 80vw); }
    mat-option mat-icon { vertical-align: middle; }
    .form-error { color: var(--mat-sys-error); margin: 0; }
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CustomFieldDialogComponent {
  protected readonly data = inject<CustomFieldDialogData>(MAT_DIALOG_DATA);
  private readonly dialogRef = inject(MatDialogRef<CustomFieldDialogComponent>);
  private readonly service = inject(CustomFieldService);
  protected readonly types = CUSTOM_FIELD_TYPES;
  protected readonly saving = signal(false);
  protected readonly error = signal<string | null>(null);

  protected readonly form = inject(FormBuilder).nonNullable.group({
    name: [this.data.field?.name ?? '', Validators.required],
    type: [{ value: this.data.field?.type ?? 'TEXT' as CustomFieldType, disabled: !!this.data.field }],
    options: [this.data.field?.options.join('\n') ?? ''],
  });

  protected readonly type = toSignal(this.form.controls.type.valueChanges, { initialValue: this.form.controls.type.value });

  submit() {
    const value = this.form.getRawValue();
    const options = value.options.split('\n').map(o => o.trim()).filter(Boolean);
    if (value.type === 'SINGLE_CHOICE' && options.length === 0) {
      this.form.controls.options.setErrors({ required: true });
      this.form.controls.options.markAsTouched();
    }
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const request = {
      name: value.name.trim(),
      target: this.data.target,
      type: value.type,
      options: value.type === 'SINGLE_CHOICE' ? options : [],
    };
    this.saving.set(true);
    this.error.set(null);
    const save = this.data.field
      ? this.service.updateField(this.data.field.id, request)
      : this.service.createField(request);
    save.subscribe({
      next: field => this.dialogRef.close(field),
      error: (error: HttpErrorResponse) => {
        this.saving.set(false);
        this.error.set(error.error?.errors?.[0] ?? 'The column could not be saved. Try again.');
      },
    });
  }
}
