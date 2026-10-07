import { HttpClient } from '@angular/common/http';
import { inject, Injectable, signal } from '@angular/core';
import { Opportunity } from '../../interfaces/Opportunity';
import { FormGroup } from '@angular/forms';
import { Observable, tap } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class OpportunityService {
  private readonly http = inject(HttpClient);
  private readonly _opportunities = signal<Opportunity[]>([]);
  readonly opportunities = this._opportunities.asReadonly();

  constructor() {
  }

  getOpportunities(): Observable<Opportunity[]> {
    return this.http.get<Opportunity[]>(environment.baseUrl + '/opportunities/', { withCredentials: true }).pipe(
      tap(opportunities => this._opportunities.set(opportunities))
    );
  }

  addOportuntiy(opportunityToAdd: FormGroup) {
    return this.http.post<Opportunity>(environment.baseUrl + '/opportunities/', opportunityToAdd, { withCredentials: true }).pipe(
      tap((newOpportunity: Opportunity) => {
        this._opportunities.update(opportunities => [...opportunities, newOpportunity]);
      })
    );
  }
}
