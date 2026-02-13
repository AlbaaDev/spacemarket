import { ApplicationConfig, inject, provideAppInitializer, provideZoneChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';

import { routes } from './app.routes';
import { HttpClient, provideHttpClient, withInterceptors } from '@angular/common/http';
import { xsrfInterceptor } from '../interceptors/xrfInterceptor';
import { firstValueFrom } from 'rxjs/internal/firstValueFrom';
import { tap } from 'rxjs/internal/operators/tap';
import { AuthService } from '../services/auth/auth-service';
import { catchError } from 'rxjs/internal/operators/catchError';
import { of } from 'rxjs/internal/observable/of';

export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({ eventCoalescing: true }), 
    provideRouter(routes),
    provideHttpClient(),
    provideAppInitializer(() => {
      const authService = inject(AuthService);
      return firstValueFrom(
        authService.getCurrentUser().pipe(catchError(() => of(null)))
      );
    }),
  ]
};
