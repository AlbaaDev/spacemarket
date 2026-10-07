import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable, map } from 'rxjs';
import { ApiResponse } from '../../interfaces/ApiResponse';
import { Interaction, InteractionRequest } from '../../interfaces/Interaction';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class InteractionService {
  private readonly http = inject(HttpClient);

  getContactInteractions(contactId: number): Observable<Interaction[]> {
    return this.http.get<ApiResponse<Interaction[]>>(`${environment.baseUrl}/contacts/${contactId}/interactions`, { withCredentials: true })
      .pipe(map(response => response.data ?? []));
  }

  logInteraction(contactId: number, request: InteractionRequest): Observable<Interaction> {
    return this.http.post<ApiResponse<Interaction>>(`${environment.baseUrl}/contacts/${contactId}/interactions`, request, { withCredentials: true })
      .pipe(map(response => response.data));
  }

  deleteInteraction(id: number): Observable<void> {
    return this.http.delete<void>(`${environment.baseUrl}/interactions/${id}`, { withCredentials: true });
  }
}
