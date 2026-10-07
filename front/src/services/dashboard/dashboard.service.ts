import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable, map } from 'rxjs';
import { ApiResponse } from '../../interfaces/ApiResponse';
import { DashboardSummary, TimelinePoint } from '../../interfaces/Dashboard';
import { environment } from '../../environments/environment';
import { IsoDate } from '../../utils/dates';

@Injectable({
  providedIn: 'root'
})
export class DashboardService {
  private readonly http = inject(HttpClient);

  /** Without a comparison Period, the backend compares with the same number of days just before. */
  getSummary(from: IsoDate, to: IsoDate, compare?: { from: IsoDate, to: IsoDate }): Observable<DashboardSummary> {
    let params = new HttpParams().set('from', from).set('to', to);
    if (compare) {
      params = params.set('compareFrom', compare.from).set('compareTo', compare.to);
    }
    return this.http.get<ApiResponse<DashboardSummary>>(environment.baseUrl + '/dashboard/summary', {
      params, withCredentials: true
    }).pipe(map(response => response.data));
  }

  getTimeline(from: IsoDate, to: IsoDate): Observable<TimelinePoint[]> {
    return this.http.get<ApiResponse<TimelinePoint[]>>(environment.baseUrl + '/dashboard/timeline', {
      params: new HttpParams().set('from', from).set('to', to), withCredentials: true
    }).pipe(map(response => response.data ?? []));
  }
}
