import { HttpClient } from '@angular/common/http';
import { inject, Injectable, signal } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { Observable, tap } from 'rxjs';
import { environment } from '../../environments/environment';
import { ApiResponse } from '../../interfaces/ApiResponse';
import { Company } from '../../interfaces/Company';

@Injectable({
  providedIn: 'root'
})
export class CompanyService {
  private readonly http = inject(HttpClient);
  private readonly _companies = signal<Company[]>([]);

  readonly companies = this._companies.asReadonly();
  canDeleteCompanies = signal<boolean>(false);
  canClearSelection = signal<boolean>(false);

  constructor() {
    this.fetchCompany();
  }

  addCompany(companyToAdd: FormGroup) {
    return this.http.post<ApiResponse<Company>>(environment.baseUrl + '/companies/', companyToAdd, { withCredentials: true }).pipe(
      tap(response => {
        this._companies.update(companies => [...companies, response.data]);
      })
    );
  }

  editCompany(companyToEdit: Company) {
    return this.http.patch<ApiResponse<Company>>(environment.baseUrl + '/companies/', companyToEdit, { withCredentials: true }).pipe(
      tap(() => {
        let companyIndex = this._companies().findIndex(company => company.id == companyToEdit.id);
        let updatedCompanies = this._companies()[companyIndex] = companyToEdit;
        let filteredCompanies = this._companies().filter((company) => company.id !== companyToEdit.id);
        this._companies.update(companies => [...filteredCompanies, updatedCompanies]);
        this.canClearSelection.set(true);
      })
    );
  }

  getCompanies(): Observable<ApiResponse<Company[]>> {
    return this.http.get<ApiResponse<Company[]>>(environment.baseUrl + '/companies/', { withCredentials: true });
  }

  deleteCompanyById(id: number) {
    return this.http.delete<void>(environment.baseUrl + `/companies/${id}`, { withCredentials: true }).pipe(
      tap(() => {
        this._companies.update(companies => companies.filter((company) => company.id !== id));
        this.canDeleteCompanies.set(false);
      })
    );
  }

  private fetchCompany(): void {
    this.getCompanies().subscribe({
      next: (response) => {
        this._companies.set(response.data);
      },
      error: (error) => {
        console.error('Error loading companies:', error);
      }
    });
  }
}
