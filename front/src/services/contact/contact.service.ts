import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { AuthService } from '../auth/auth-service';
import { tap } from 'rxjs/internal/operators/tap';

@Injectable({
  providedIn: 'root'
})
export class ContactService {
  private readonly http = inject(HttpClient);
  private readonly authService = inject(AuthService);

  constructor() {

  }

  getContacts() {
    return this.http.get<any[]>("http://localhost:8080/contacts/", { withCredentials: true });
  }
}
