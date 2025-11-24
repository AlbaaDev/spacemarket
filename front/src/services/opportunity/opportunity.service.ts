import { HttpClient } from '@angular/common/http';
import { inject, Injectable, signal } from '@angular/core';
import { Opportunity } from '../../interfaces/Opportunity';
import { FormGroup } from '@angular/forms';
import { tap } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class OpportunityService {
  private readonly http = inject(HttpClient);
  private readonly _opportunities = signal<Opportunity[]>([]);
  readonly opportunities = this._opportunities.asReadonly();

  constructor() {
  }

  addOportuntiy(opportunityToAdd: FormGroup) {
    return this.http.post<Opportunity>("http://localhost:8080/opportunities/", opportunityToAdd, { withCredentials: true }).pipe(
      tap((newOpportunity: Opportunity) => {
        this._opportunities.update(opportunities => [...opportunities, newOpportunity]);
      })
    );
  }
}
