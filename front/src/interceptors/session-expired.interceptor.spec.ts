import { HttpClient, provideHttpClient, withInterceptors } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth/auth-service';
import { sessionExpiredInterceptor } from './session-expired.interceptor';

describe('sessionExpiredInterceptor', () => {
  let http: HttpClient;
  let httpTesting: HttpTestingController;
  let router: jasmine.SpyObj<Router>;
  let authService: AuthService;

  beforeEach(() => {
    localStorage.setItem('user', JSON.stringify({ id: 1, email: 'john@work.com' }));
    router = jasmine.createSpyObj<Router>('Router', ['navigate']);
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(withInterceptors([sessionExpiredInterceptor])),
        provideHttpClientTesting(),
        { provide: Router, useValue: router },
      ],
    });
    http = TestBed.inject(HttpClient);
    httpTesting = TestBed.inject(HttpTestingController);
    authService = TestBed.inject(AuthService);
  });

  afterEach(() => {
    httpTesting.verify();
    localStorage.removeItem('user');
  });

  it('signs the user out and sends them to the login page when the session has expired', () => {
    http.get('/contacts/').subscribe({ error: () => {} });
    httpTesting.expectOne('/contacts/').flush(null, { status: 401, statusText: 'Unauthorized' });

    expect(authService.isAuthenticated()).toBeFalse();
    expect(localStorage.getItem('user')).toBeNull();
    expect(router.navigate).toHaveBeenCalledWith(['/app-login']);
  });

  it('leaves a wrong password to the login form', () => {
    http.post('/auth/login', {}).subscribe({ error: () => {} });
    httpTesting.expectOne('/auth/login').flush(null, { status: 401, statusText: 'Unauthorized' });

    expect(authService.isAuthenticated()).toBeTrue();
    expect(router.navigate).not.toHaveBeenCalled();
  });

  it('keeps the session on other errors', () => {
    http.get('/contacts/').subscribe({ error: () => {} });
    httpTesting.expectOne('/contacts/').flush(null, { status: 500, statusText: 'Server Error' });

    expect(authService.isAuthenticated()).toBeTrue();
    expect(router.navigate).not.toHaveBeenCalled();
  });
});
