import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideRouter } from '@angular/router';
import { Opportunity } from '../../../interfaces/Opportunity';
import { OpportunityDetailsComponent } from './opportunity.details.component';

const opportunity: Opportunity = {
  id: 5, name: 'Cloud migration', businessName: 'Microsoft', value: 45000, status: 'OPEN', closeDate: null,
  principalContact: { id: 3, firstName: 'Sarah', lastName: 'Connor' }, customValues: {},
};

describe('OpportunityDetailsComponent', () => {
  let component: OpportunityDetailsComponent;
  let fixture: ComponentFixture<OpportunityDetailsComponent>;

  beforeEach(async () => {
    history.replaceState({ opportunity }, '');
    await TestBed.configureTestingModule({
      imports: [OpportunityDetailsComponent],
      providers: [provideHttpClient(), provideRouter([])]
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
});
