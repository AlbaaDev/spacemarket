import { HttpClient } from '@angular/common/http';
import { Injectable, signal } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { Observable, switchMap, tap, throwError } from 'rxjs';
import { User } from '../../interfaces/User';
import { environment } from '../../environments/environment';

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

  login(loginForm: FormGroup): Observable<User> {
    if (loginForm.invalid) {
      return throwError(() => new Error('Invalid form'));
    }

    const { email, password } = loginForm.value;
    return this.http.post<User>(
      environment.baseUrl + '/auth/login',
      { email, password },
      { withCredentials: true }
    ).pipe(
      tap(user => {
        this._isAuthenticated.set(true);
        this._currentUser.set(user);
        localStorage.setItem('user', JSON.stringify(user));
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

  getCurrentUser(): Observable<User> {
    return this.http.get<User>(environment.baseUrl + '/users/me', { withCredentials: true }).pipe(
      tap({
        next: (user) => {
          this.setCurrentUser(user);
        },
        error: () => this.clearSession()
      })
    );
  };

  logout(): Observable<void> {
    return this.http.post<void>(environment.baseUrl + '/auth/logout', {}, { withCredentials: true })
      .pipe(
        tap(() => this.clearSession())
      );
  }

  setCurrentUser(user: User): void {
    this._currentUser.set(user);
    this._isAuthenticated.set(true);
    localStorage.setItem('user', JSON.stringify(user));
  }

  private clearSession(): void {
    this._isAuthenticated.set(false);
    this._currentUser.set(null);
    localStorage.removeItem('user');
  }
}