import { HttpClient, HttpHeaders } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Contact } from '../../interfaces/Contact';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ContactService {
  private readonly http = inject(HttpClient);
  constructor() {  }

  getContacts() : Observable<Contact[]> {
    return this.http.get<Contact[]>("http://localhost:8080/contacts/", { withCredentials: true });
  }

  deleteContactById(id: number)  {
    return this.http.delete<void>(`http://localhost:8080/contacts/${id}`, {withCredentials: true});
  }
}
