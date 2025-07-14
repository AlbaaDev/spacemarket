import { HttpClient } from '@angular/common/http';
import { Injectable, signal } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { finalize, Observable, tap, throwError } from 'rxjs';
import { User } from '../interfaces/User';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private readonly _isAuthenticated = signal<boolean>(false);
  private readonly _currentUser = signal<any | null>(null)

  readonly isAuthenticated = this._isAuthenticated.asReadonly();
  readonly currentUser = this._currentUser.asReadonly();

  constructor(private http: HttpClient) { }

  login(loginForm: FormGroup) : Observable<User> {
    if(loginForm.invalid) {
      return throwError(() => new Error('Invalid form'));
    }
    const {email, password} = loginForm.value;
    return this.http.post<User>('http://localhost:8080/auth/login', { email, password }, {withCredentials: true}).pipe(
      tap((user) => {
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
    const {email, password, firstName, lastName} = signUpForm.value
    return this.http.post<void>('http://localhost:8080/auth/register', {email, password, firstName, lastName});
  }

  getCurrentUser() {
    return this.http.get<User>('http://localhost:8080/users/me', {withCredentials: true}).pipe(
      tap({
        next: (user) => {
          this._isAuthenticated.set(true); 
          this._currentUser.set(user)
        },
        error: () => this.clearSession()
      })
    );
  }

  isAuth() {
    return this.http.get<User>('http://localhost:8080/users/me', {withCredentials: true}).pipe(
      tap({
        next: () => {
          this._isAuthenticated.set(true); 
        },
        error: () => this.clearSession()
      })
    );
  };

  logout() : Observable<void> {
    return this.http.post<void>('http://localhost:8080/auth/logout', {}, {withCredentials: true})
          .pipe(
              finalize(() => {
                this._isAuthenticated.set(false);
                this._currentUser.set(null);
                localStorage.removeItem('user');
                this.clearSession();
              })
          );
  }

  private clearSession(): void {
    if (typeof sessionStorage === 'undefined') return;
    this._isAuthenticated.set(false);
    this._currentUser.set(null);
    sessionStorage.removeItem('user');
  } 
}