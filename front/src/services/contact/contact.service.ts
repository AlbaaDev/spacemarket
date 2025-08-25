import { HttpClient } from '@angular/common/http';
import { inject, Injectable, signal } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { Observable, tap } from 'rxjs';
import { Contact } from '../../interfaces/Contact';

@Injectable({
  providedIn: 'root'
})
export class ContactService {
  private readonly http = inject(HttpClient);
  private readonly _contacts = signal<Contact[]>([]);
  readonly contacts = this._contacts.asReadonly();

  constructor() {
    this.fetchContacts();
  }

  addContact(contactToAdd: FormGroup) {
    return this.http.post<Contact>("http://localhost:8080/contacts/add", contactToAdd, { withCredentials: true }).pipe(
      tap(newContact => {
        this._contacts.update(contacts => [...contacts, newContact]);
      })
    );
  }

  getContacts(): Observable<Contact[]> {
    return this.http.get<Contact[]>("http://localhost:8080/contacts/", { withCredentials: true });
  }

  deleteContactById(id: number) {
    return this.http.delete<void>(`http://localhost:8080/contacts/${id}`, { withCredentials: true });
  }

  private fetchContacts(): void {
    this.getContacts().subscribe({
      next: (contacts) => {
        this._contacts.set(contacts);
      },
      error: (error) => {
        console.error('Error loading contacts:', error);
      }
    });
  }


}
