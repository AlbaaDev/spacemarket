import { HttpClient } from '@angular/common/http';
import { Injectable, signal } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { finalize, Observable, switchMap, tap, throwError } from 'rxjs';
import { User } from '../../interfaces/User';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private readonly _isAuthenticated = signal<boolean>(false);
  private readonly _currentUser = signal<any | null>(null)

  readonly isAuthenticated = this._isAuthenticated.asReadonly();
  readonly currentUser = this._currentUser.asReadonly();

  constructor(private http: HttpClient) {
      if(localStorage.getItem('user')) {
        this._isAuthenticated.set(true);
        this._currentUser.set(JSON.parse(localStorage.getItem('user')!))
      }
  }

  login(loginForm: FormGroup): Observable<User> {
    if (loginForm.invalid) {
      return throwError(() => new Error('Invalid form'));
    }

    return this.http.get('http://localhost:8080/auth/csrf', { withCredentials: true })
      .pipe(
        switchMap(() => {
          const { email, password } = loginForm.value;
          return this.http.post<User>(
            'http://localhost:8080/auth/login',
            { email, password },
            { withCredentials: true }
          );
        }),
        tap(user => {
          this._isAuthenticated.set(true);
          this._currentUser.set(user);
          localStorage.setItem('user', JSON.stringify(user));
        })
      );
  }

  signUp(signUpForm: FormGroup) {
    if(signUpForm.invalid) {
      return throwError(() => new Error('Invalid signup Form'));
    }
    const user : User = signUpForm.value;
    return this.http.post<void>('http://localhost:8080/auth/register', user);
  }

  getCurrentUser() : Observable<User> {
    return this.http.get<User>('http://localhost:8080/users/me', {withCredentials: true}).pipe(
      tap({
        next: (user) => {
          this.setCurrentUser(user);
        },
        error: () => this.clearSession()
      })
    );
  };

  logout() : Observable<void> {
    return this.http.post<void>('http://localhost:8080/auth/logout', {}, {withCredentials: true})
      .pipe(
        tap(() => this.clearSession())
      );
  }

  setCurrentUser(user: User) : void {
    this._currentUser.set(user);
    this._isAuthenticated.set(true);
    localStorage.setItem('user', JSON.stringify(user));
  }

  private clearSession() : void {
    this._isAuthenticated.set(false);
    this._currentUser.set(null);
    localStorage.removeItem('user');
  } 
}