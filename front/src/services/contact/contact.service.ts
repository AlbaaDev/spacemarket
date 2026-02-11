import { HttpClient } from '@angular/common/http';
import { inject, Injectable, signal } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { Observable, tap } from 'rxjs';
import { Contact } from '../../interfaces/Contact';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ContactService {
  private readonly http = inject(HttpClient);
  private readonly _contacts = signal<Contact[]>([]);
  readonly contacts = this._contacts.asReadonly();
  canDeleteContacts = signal<boolean>(false);
  canClearSelection = signal<boolean>(false);

  constructor() {
    this.fetchContacts();
  }

  addContact(contactToAdd: FormGroup) {
    return this.http.post<Contact>(environment.baseUrl + '/contacts/', contactToAdd, { withCredentials: true }).pipe(
      tap(newContact => {
        this._contacts.update(contacts => [...contacts, newContact]);
      })
    );
  }

  editContact(contactToEdit: FormGroup) {
    return this.http.put<Contact>(environment.baseUrl + '/contacts/', contactToEdit.value, { withCredentials: true }).pipe(
      tap(() => {
        let contactIndex = this._contacts().findIndex(contact => contact.id == contactToEdit.value.id);
        let updatedContacts = this._contacts()[contactIndex] = contactToEdit.value;
        let filteredContacts = this._contacts().filter((contact) => contact.id !== contactToEdit.value.id);
        this._contacts.update(contacts => [...filteredContacts, updatedContacts]);
        this.canClearSelection.set(true);
      })
    );
  }

  getContacts(): Observable<Contact[]> {
    return this.http.get<Contact[]>(environment.baseUrl + '/contacts/', { withCredentials: true });
  }

  deleteContactById(id: number) {
    return this.http.delete<void>(environment.baseUrl + `/contacts/${id}`, { withCredentials: true }).pipe(
      tap(() => {
        this._contacts.update(contacts => contacts.filter((contact) => contact.id !== id));
        this.canDeleteContacts.set(false);
      })
    );
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
