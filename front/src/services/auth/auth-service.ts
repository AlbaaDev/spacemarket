import { HttpClient } from '@angular/common/http';
import { Injectable, signal } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { finalize, Observable, tap, throwError } from 'rxjs';
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

  // getCurrentUser() {
  //   return this.http.get<User>('http://localhost:8080/users/me', {withCredentials: true}).pipe(
  //     tap({
  //       next: (user) => {
  //         this._isAuthenticated.set(true); 
  //         this._currentUser.set(user)
  //       },
  //       error: () => this.clearSession()
  //     })
  //   );
  // }

  isAuth() {
    console.log('currentUser ', this.currentUser(), "isAuthenticated ", this.isAuthenticated());
    return this.http.get<User>('http://localhost:8080/users/me', {withCredentials: true}).pipe(
      tap({
        next: (responseIsAuth) => {
          console.log(responseIsAuth);
          this._isAuthenticated.set(true); 
          this._currentUser.set(responseIsAuth);
        },
        error: () => this.clearSession()
      })
    );
  };

  logout() : Observable<void> {
    return this.http.post<void>('http://localhost:8080/auth/logout', {}, {withCredentials: true})
          .pipe(
              finalize(() => {
                this.clearSession();
              })
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