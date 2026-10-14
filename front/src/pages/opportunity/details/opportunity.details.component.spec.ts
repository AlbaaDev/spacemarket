import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideRouter } from '@angular/router';
import { Opportunity } from '../../../interfaces/Opportunity';
import { provideAppDateAdapter } from '../../../utils/date-adapter';
import { OpportunityDetailsComponent } from './opportunity.details.component';

const opportunity: Opportunity = {
  id: 5, name: 'Cloud migration', company: { id: 2, name: 'Microsoft' }, value: 45000, status: 'OPEN', closeDate: null,
  contacts: [{ id: 3, firstName: 'Sarah', lastName: 'Connor' }], customValues: {}, step: 'CLIENT',
};

describe('OpportunityDetailsComponent', () => {
  let component: OpportunityDetailsComponent;
  let fixture: ComponentFixture<OpportunityDetailsComponent>;

  beforeEach(async () => {
    history.replaceState({ opportunity }, '');
    await TestBed.configureTestingModule({
      imports: [OpportunityDetailsComponent],
      providers: [provideHttpClient(), provideRouter([]), provideAppDateAdapter()]
    })
      .compileComponents();

    fixture = TestBed.createComponent(OpportunityDetailsComponent);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('id', '5');
    fixture.detectChanges();
  });

  afterEach(() => history.replaceState(null, ''));

  it('shows the opportunity passed by the table before the list loads', () => {
    expect(component.opportunity()?.name).toBe('Cloud migration');
    expect(fixture.nativeElement.querySelector('h1').textContent).toContain('Cloud migration');
  });

  it('shows the opportunity Step among its facts', () => {
    expect(fixture.nativeElement.querySelector('.facts').textContent).toContain('Client interview');
  });

  it("logs an interaction with the opportunity's first contact by default", () => {
    expect(component.form.controls.contactId.value).toBe(3);
  });
});
