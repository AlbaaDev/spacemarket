import { ApplicationConfig, inject, provideAppInitializer, provideZoneChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';

import { provideHttpClient } from '@angular/common/http';
import { firstValueFrom } from 'rxjs/internal/firstValueFrom';
import { of } from 'rxjs/internal/observable/of';
import { catchError } from 'rxjs/internal/operators/catchError';
import { AuthService } from '../services/auth/auth-service';
import { routes } from './app.routes';



export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes),
    provideHttpClient(),
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

