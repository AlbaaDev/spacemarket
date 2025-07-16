import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { User } from '../../interfaces/User';
import { Observable, tap } from 'rxjs';
import { AuthService } from '../auth/auth-service';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private readonly http = inject(HttpClient);
  private readonly authService = inject(AuthService);

  constructor() {

   }

   updateProfile(profileData: User) : Observable<User> {
      return this.http.post<User>("http://localhost:8080/users/update", profileData, {withCredentials: true}).pipe(
        tap((updatedUser) => {
          this.authService.setCurrentUser(updatedUser);
        })
      )
   }

   updatePassword(password: any) {

   }
  }

