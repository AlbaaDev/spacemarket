import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { AuthService } from '../services/auth/auth-service';

/** When the API answers 401 the session is over: forget the stored user and go back to the login page. */
export const sessionExpiredInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  return next(req).pipe(
    catchError((error: unknown) => {
      // A 401 on the login request itself is a wrong password, which the login form reports.
      if (error instanceof HttpErrorResponse && error.status === 401 && !req.url.endsWith('/auth/login')) {
        authService.endSession();
        router.navigate(['/app-login']);
      }
      return throwError(() => error);
    })
  );
};
