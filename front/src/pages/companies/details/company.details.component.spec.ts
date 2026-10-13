import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideRouter } from '@angular/router';
import { Company } from '../../../interfaces/Company';
import { Contact } from '../../../interfaces/Contact';
import { CompanyDetailsComponent } from './company.details.component';

function contact(id: number, firstName: string, lastName: string, city: string, email: string): Contact {
  return { id, firstName, lastName, city, address: '', country: '', emails: [{ email, type: 'WORK', primary: true }], phones: [] };
}

const company: Company = {
  id: 1, name: 'Acme', city: 'Genève', address: '', country: 'Suisse', industry: 'Tech',
  contacts: [
    contact(1, 'Élodie', 'Martin', 'Paris', 'elodie@acme.example'),
    contact(2, 'Bob', 'Durand', 'Genève', 'bob@acme.example'),
  ],
};

describe('CompanyDetailsComponent', () => {
  let component: CompanyDetailsComponent;
  let fixture: ComponentFixture<CompanyDetailsComponent>;

  beforeEach(async () => {
    history.replaceState({ company }, '');
    await TestBed.configureTestingModule({
      imports: [CompanyDetailsComponent],
      providers: [provideHttpClient(), provideRouter([])]
    })
      .compileComponents();

    fixture = TestBed.createComponent(CompanyDetailsComponent);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('id', '1');
    fixture.detectChanges();
  });

  afterEach(() => history.replaceState(null, ''));

  it('lists the company contacts by name', () => {
    expect(component.shownContacts().map(c => c.lastName)).toEqual(['Durand', 'Martin']);
  });

  it('narrows the contacts to those matching every word of the search, ignoring accents', () => {
    component.contactSearch.set('elodie paris');
    expect(component.shownContacts().map(c => c.id)).toEqual([1]);

    component.contactSearch.set('bob@acme');
    expect(component.shownContacts().map(c => c.id)).toEqual([2]);

    component.contactSearch.set('nobody');
    expect(component.shownContacts()).toEqual([]);
  });
});
