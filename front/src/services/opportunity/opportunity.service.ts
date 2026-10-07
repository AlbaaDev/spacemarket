import { HttpClient } from '@angular/common/http';
import { inject, Injectable, signal } from '@angular/core';
import { Observable, map, tap } from 'rxjs';
import { ApiResponse } from '../../interfaces/ApiResponse';
import { Opportunity, OpportunityRequest } from '../../interfaces/Opportunity';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class OpportunityService {
  private readonly http = inject(HttpClient);
  private readonly url = environment.baseUrl + '/opportunities/';
  private readonly _opportunities = signal<Opportunity[]>([]);
  readonly opportunities = this._opportunities.asReadonly();

  getOpportunities(): Observable<Opportunity[]> {
    return this.http.get<ApiResponse<Opportunity[]>>(this.url, { withCredentials: true }).pipe(
      map(response => response.data ?? []),
      tap(opportunities => this._opportunities.set(opportunities))
    );
  }

  addOpportunity(request: OpportunityRequest): Observable<Opportunity> {
    return this.http.post<ApiResponse<Opportunity>>(this.url, request, { withCredentials: true }).pipe(
      map(response => response.data),
      tap(created => this._opportunities.update(opportunities => [...opportunities, created]))
    );
  }

  updateOpportunity(id: number, request: OpportunityRequest): Observable<Opportunity> {
    return this.http.put<ApiResponse<Opportunity>>(this.url + id, request, { withCredentials: true }).pipe(
      map(response => response.data),
      tap(updated => this._opportunities.update(opportunities =>
        opportunities.map(opportunity => opportunity.id === id ? updated : opportunity)))
    );
  }

  deleteOpportunity(id: number): Observable<void> {
    return this.http.delete<void>(this.url + id, { withCredentials: true }).pipe(
      tap(() => this._opportunities.update(opportunities => opportunities.filter(opportunity => opportunity.id !== id)))
    );
  }
}
