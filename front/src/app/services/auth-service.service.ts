import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { first, tap, throwError } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthServiceService {

  constructor(private http: HttpClient) { 
  }

  login(loginForm: FormGroup) {
    const email = loginForm.get('email')?.value;
    const password = loginForm.get('password')?.value;

    if(email && password) {
      return this.http.post<{token: string, expiresIn: number}>('http://localhost:8080/auth/login', { email, password }).pipe(
        tap(response => {
          const now = Date.now();
          const expirationTime = now + response.expiresIn;
          
          localStorage.setItem('authToken', response.token);
          localStorage.setItem('tokenExpiration', expirationTime.toString());
        })
      );   
    } else {
      console.log('Invalid form');
      return throwError(() => new Error('Invalid form'));
    }
  }

  signUp(signUpForm: FormGroup) {
    const email = signUpForm.get('email')?.value;
    const password = signUpForm.get('password')?.value;
    const firstName = signUpForm.get('firstName')?.value;
    const lastName = signUpForm.get('lastName')?.value;

    if(email && password && firstName && lastName) {
        return this.http.post('http://localhost:8080/auth/signup', {email, password, firstName, lastName});
    } else {
      console.log('Invalid signUp Form');
      return throwError(() => new Error('Invalid SignUp Form'));
    }
  }

  getCurrentUser() {
    const token = localStorage.getItem('authToken');
    const tokenExpirationen = localStorage.getItem('tokenExpiration');

    if(token && tokenExpirationen && !this.isExpired(tokenExpirationen)) {
      return this.http.post('http://localhost:8080/users/me', {token});
    } else {
      return throwError(() => new Error('token has expired'));
    }
  }
  
  isExpired(tokenExpirationen: string) : boolean {
    if (!tokenExpirationen) return true;
    const currentTime = Math.floor(Date.now() / 1000);
    return parseInt(tokenExpirationen) < currentTime;
  }

  isAuth() : boolean {
    return localStorage.getItem('authToken') != null;
  }
}