import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable, map, tap } from 'rxjs';
import { ApiResponse } from '../../interfaces/ApiResponse';
import { Interaction, InteractionRequest, OpportunityInteractionRequest } from '../../interfaces/Interaction';
import { environment } from '../../environments/environment';
import { OpportunityService } from '../opportunity/opportunity.service';

@Injectable({
  providedIn: 'root'
})
export class InteractionService {
  private readonly http = inject(HttpClient);
  private readonly opportunityService = inject(OpportunityService);

  getContactInteractions(contactId: number): Observable<Interaction[]> {
    return this.http.get<ApiResponse<Interaction[]>>(`${environment.baseUrl}/contacts/${contactId}/interactions`, { withCredentials: true })
      .pipe(map(response => response.data ?? []));
  }

  getOpportunityInteractions(opportunityId: number): Observable<Interaction[]> {
    return this.http.get<ApiResponse<Interaction[]>>(`${environment.baseUrl}/opportunities/${opportunityId}/interactions`, { withCredentials: true })
      .pipe(map(response => response.data ?? []));
  }

  logInteraction(contactId: number, request: InteractionRequest): Observable<Interaction> {
    return this.http.post<ApiResponse<Interaction>>(`${environment.baseUrl}/contacts/${contactId}/interactions`, request, { withCredentials: true })
      .pipe(map(response => response.data), tap(created => this.refreshStep(created)));
  }

  logOpportunityInteraction(opportunityId: number, request: OpportunityInteractionRequest): Observable<Interaction> {
    return this.http.post<ApiResponse<Interaction>>(`${environment.baseUrl}/opportunities/${opportunityId}/interactions`, request, { withCredentials: true })
      .pipe(map(response => response.data), tap(created => this.refreshStep(created)));
  }

  deleteInteraction(interaction: Interaction): Observable<void> {
    return this.http.delete<void>(`${environment.baseUrl}/interactions/${interaction.id}`, { withCredentials: true })
      .pipe(tap(() => this.refreshStep(interaction)));
  }

  // An Opportunity's Step is computed from its Interactions, so it can change with any of them.
  private refreshStep(interaction: Interaction) {
    if (interaction.step !== null) {
      this.opportunityService.getOpportunities().subscribe({ error: () => undefined });
    }
  }
}
