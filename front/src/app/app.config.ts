import { ApplicationConfig, inject, provideAppInitializer, provideZoneChangeDetection } from '@angular/core';
import { provideRouter, withComponentInputBinding } from '@angular/router';

import { provideHttpClient } from '@angular/common/http';
import { firstValueFrom } from 'rxjs/internal/firstValueFrom';
import { of } from 'rxjs/internal/observable/of';
import { catchError } from 'rxjs/internal/operators/catchError';
import { AuthService } from '../services/auth/auth-service';
import { routes } from './app.routes';
import { provideAppDateAdapter } from '../utils/date-adapter';



export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes, withComponentInputBinding()),
    provideHttpClient(),
    provideAppDateAdapter(),
    provideAppInitializer(() => {
      const authService = inject(AuthService);
      if (!authService.hasJwtCookie()) {
        return Promise.resolve();
      }
      return firstValueFrom(
        authService.getCurrentUser().pipe(catchError(() => of(null)))
      );
    }),
    //TODO : XSS
  ]
};

