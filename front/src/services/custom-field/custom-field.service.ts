import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable, Signal, signal, WritableSignal } from '@angular/core';
import { Observable, map, tap } from 'rxjs';
import { ApiResponse } from '../../interfaces/ApiResponse';
import { CustomField, CustomFieldRequest, CustomFieldTarget } from '../../interfaces/CustomField';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class CustomFieldService {
  private readonly http = inject(HttpClient);
  private readonly url = environment.baseUrl + '/custom-fields/';
  private readonly cache: Record<CustomFieldTarget, WritableSignal<CustomField[]>> = {
    CONTACT: signal([]),
    COMPANY: signal([]),
    OPPORTUNITY: signal([]),
  };
  private readonly loaded = new Set<CustomFieldTarget>();

  /** The target's fields, loaded on first use. */
  fields(target: CustomFieldTarget): Signal<CustomField[]> {
    if (!this.loaded.has(target)) {
      this.loaded.add(target);
      this.http.get<ApiResponse<CustomField[]>>(this.url, { params: new HttpParams().set('target', target), withCredentials: true })
        .subscribe({
          next: response => this.cache[target].set(response.data ?? []),
          error: () => this.loaded.delete(target),
        });
    }
    return this.cache[target].asReadonly();
  }

  createField(request: CustomFieldRequest): Observable<CustomField> {
    return this.http.post<ApiResponse<CustomField>>(this.url, request, { withCredentials: true }).pipe(
      map(response => response.data),
      tap(field => this.cache[field.target].update(fields => [...fields, field]))
    );
  }

  updateField(id: number, request: CustomFieldRequest): Observable<CustomField> {
    return this.http.put<ApiResponse<CustomField>>(this.url + id, request, { withCredentials: true }).pipe(
      map(response => response.data),
      tap(field => this.cache[field.target].update(fields => fields.map(f => f.id === id ? field : f)))
    );
  }

  deleteField(field: CustomField): Observable<void> {
    return this.http.delete<void>(this.url + field.id, { withCredentials: true }).pipe(
      tap(() => this.cache[field.target].update(fields => fields.filter(f => f.id !== field.id)))
    );
  }
}
