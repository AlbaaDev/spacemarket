import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { throwError } from 'rxjs';

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
      return this.http.post('http://localhost:8080/auth/login', { email, password });   
    } else {
      console.log('Invalid form');
      return throwError(() => new Error('Invalid form'));
    }
  }
}
