import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideRouter } from '@angular/router';
import { ContactDetailsComponent } from './contact.details.component';
import { provideAppDateAdapter } from '../../../utils/date-adapter';


describe('ContactComponent', () => {
  let component: ContactDetailsComponent;
  let fixture: ComponentFixture<ContactDetailsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ContactDetailsComponent],
      providers: [provideHttpClient(), provideRouter([]), provideAppDateAdapter()]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ContactDetailsComponent);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('id', '1');
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
