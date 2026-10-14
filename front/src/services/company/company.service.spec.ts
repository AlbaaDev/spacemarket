import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';

import { environment } from '../../environments/environment';
import { Contact } from '../../interfaces/Contact';
import { ContactService } from '../contact/contact.service';
import { CompanyService } from './company.service';

describe('CompanyService', () => {
  let service: CompanyService;
  let contactService: ContactService;
  let http: HttpTestingController;

  const contact = (id: number, company: Contact['company']): Contact => ({
    id, firstName: 'First', lastName: `Last${id}`, emails: [], phones: [], city: '', address: '', country: '', company,
  });

  beforeEach(() => {
    TestBed.configureTestingModule({ providers: [provideHttpClient(), provideHttpClientTesting()] });
    service = TestBed.inject(CompanyService);
    contactService = TestBed.inject(ContactService);
    http = TestBed.inject(HttpTestingController);
    http.expectOne(environment.baseUrl + '/companies/').flush({ data: [] });
    http.expectOne(environment.baseUrl + '/contacts/').flush({
      data: [contact(1, { id: 7, name: 'Acme' }), contact(2, { id: 8, name: 'Globex' }), contact(3, null)],
    });
  });

  afterEach(() => http.verify());

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('leaves the Contacts of a deleted Company without a Company', () => {
    service.deleteCompanyById(7).subscribe();
    http.expectOne(environment.baseUrl + '/companies/7').flush(null);

    expect(contactService.contacts().map(c => c.company?.name ?? null)).toEqual([null, 'Globex', null]);
  });
});
