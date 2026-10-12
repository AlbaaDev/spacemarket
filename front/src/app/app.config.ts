import { ApplicationConfig, inject, provideAppInitializer, provideZoneChangeDetection } from '@angular/core';
import { provideRouter, withComponentInputBinding } from '@angular/router';

import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { sessionExpiredInterceptor } from '../interceptors/session-expired.interceptor';
import { AuthService } from '../services/auth/auth-service';
import { routes } from './app.routes';
import { provideAppDateAdapter } from '../utils/date-adapter';



export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes, withComponentInputBinding()),
    provideHttpClient(withInterceptors([sessionExpiredInterceptor])),
    provideAppDateAdapter(),
    provideAppInitializer(() => inject(AuthService).restoreSession()),
    //TODO : XSS
  ]
};

