import { HttpClient } from '@angular/common/http';
import { Injectable, signal } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { catchError, firstValueFrom, Observable, of, tap, throwError } from 'rxjs';
import { environment } from '../../environments/environment';
import { ApiResponse } from '../../interfaces/ApiResponse';
import { User } from '../../interfaces/User';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private readonly _isAuthenticated = signal<boolean>(false);
  private readonly _currentUser = signal<User | null>(null)

  readonly isAuthenticated = this._isAuthenticated.asReadonly();
  readonly currentUser = this._currentUser.asReadonly();

  constructor(private readonly http: HttpClient) {
    if (localStorage.getItem('user')) {
      this._isAuthenticated.set(true);
      this._currentUser.set(JSON.parse(localStorage.getItem('user')!))
    }
  }

  login(loginForm: FormGroup): Observable<ApiResponse<User>> {
    if (loginForm.invalid) {
      return throwError(() => new Error('Invalid form'));
    }

    const { email, password } = loginForm.value;
    return this.http.post<ApiResponse<User>>(environment.baseUrl + '/auth/login', { email, password }, { withCredentials: true })
      .pipe(
        tap(user => {
          this._isAuthenticated.set(true);
          this._currentUser.set(user.data);
          localStorage.setItem('user', JSON.stringify(user.data));
        })
      );
  }

  signUp(signUpForm: FormGroup) {
    if (signUpForm.invalid) {
      return throwError(() => new Error('Invalid signup Form'));
    }
    const user: User = signUpForm.value;
    return this.http.post<void>(environment.baseUrl + '/auth/register', user);
  }

  getCurrentUser(): Observable<ApiResponse<User>> {
    console.log("getCurrentUser  ");
    return this.http.get<ApiResponse<User>>(environment.baseUrl + '/users/me', { withCredentials: true }).pipe(
      tap({
        next: (response) => {
          this.setCurrentUser(response.data);
        },
        error: () => this.endSession()
      })
    );
  };

  logout(): Observable<void> {
    return this.http.post<void>(environment.baseUrl + '/auth/logout', {}, { withCredentials: true })
      .pipe(
        tap(() => this.endSession())
      );
  }

  setCurrentUser(newUser: User): void {
    const user = { ...this._currentUser(), ...newUser };
    this._currentUser.set(user);
    this._isAuthenticated.set(true);
    localStorage.setItem('user', JSON.stringify(user));
    console.log(" this._currentUser : ", this._currentUser());
  }

  /**
   * At startup, checks that the stored user's session is still alive. The jwt cookie is httpOnly,
   * so asking the API is the only way to know; an expired session ends here instead of showing
   * empty pages.
   */
  restoreSession(): Promise<unknown> {
    if (!this._isAuthenticated()) {
      return Promise.resolve();
    }
    return firstValueFrom(this.getCurrentUser().pipe(catchError(() => of(null))));
  }

  /** Forgets the signed-in user locally, e.g. after logout or when the API says the session has expired. */
  endSession(): void {
    this._isAuthenticated.set(false);
    this._currentUser.set(null);
    localStorage.removeItem('user');
  }
}