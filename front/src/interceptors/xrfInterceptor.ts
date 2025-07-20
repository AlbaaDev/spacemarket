import { HttpErrorResponse, HttpEvent, HttpHandlerFn, HttpRequest, HttpXsrfTokenExtractor } from "@angular/common/http";
import { inject } from "@angular/core";
import { Observable } from "rxjs/internal/Observable";
import { throwError } from "rxjs/internal/observable/throwError";
import { catchError } from "rxjs/internal/operators/catchError";
import { AuthService } from "../services/auth/auth-service";

export const xsrfInterceptor = (req: HttpRequest<unknown>, next: HttpHandlerFn): Observable<HttpEvent<unknown>> => {
    const tokenExtractor = inject(HttpXsrfTokenExtractor);
    const authService = inject(AuthService);
    const token = tokenExtractor.getToken();
    let authReq = req;
    
    if (token !== null && req.method !== 'GET' && req.method !== 'HEAD') {
        authReq = req.clone({
            withCredentials: true,
            headers: req.headers.set('X-XSRF-TOKEN', token),
        });
    }

    return next(authReq).pipe(
        catchError((error: any) => {
            if (error instanceof HttpErrorResponse && error.status === 401) {
                authService.logout();
            }
            return throwError(() => error);
        })
    );
};