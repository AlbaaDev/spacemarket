import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';

import { environment } from '../../environments/environment';
import { AuthService } from './auth-service';

describe('AuthService', () => {
  let httpTesting: HttpTestingController;

  function createService(): AuthService {
    TestBed.configureTestingModule({ providers: [provideHttpClient(), provideHttpClientTesting()] });
    httpTesting = TestBed.inject(HttpTestingController);
    return TestBed.inject(AuthService);
  }

  afterEach(() => {
    httpTesting.verify();
    localStorage.removeItem('user');
  });

  it('should be created', () => {
    expect(createService()).toBeTruthy();
  });

  describe('restoreSession', () => {
    it('asks nothing of the API when nobody is signed in', async () => {
      const service = createService();

      await service.restoreSession();

      httpTesting.expectNone(environment.baseUrl + '/users/me');
      expect(service.isAuthenticated()).toBeFalse();
    });

    it('keeps the stored user signed in while the session is still valid', async () => {
      localStorage.setItem('user', JSON.stringify({ id: 1, email: 'john@work.com' }));
      const service = createService();

      const restored = service.restoreSession();
      httpTesting.expectOne(environment.baseUrl + '/users/me').flush({ data: { id: 1, email: 'john@work.com' } });
      await restored;

      expect(service.isAuthenticated()).toBeTrue();
    });

    it('signs the stored user out when the session has expired', async () => {
      localStorage.setItem('user', JSON.stringify({ id: 1, email: 'john@work.com' }));
      const service = createService();

      const restored = service.restoreSession();
      httpTesting.expectOne(environment.baseUrl + '/users/me').flush(null, { status: 401, statusText: 'Unauthorized' });
      await restored;

      expect(service.isAuthenticated()).toBeFalse();
      expect(localStorage.getItem('user')).toBeNull();
    });
  });
});
