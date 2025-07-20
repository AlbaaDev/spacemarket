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
      return this.http.put<User>("http://localhost:8080/users/update/profile", profileData, {withCredentials: true}).pipe(
        tap(() => {
          this.authService.setCurrentUser(profileData);
        })
      )
   }

   updateSettings(settingsData: User) : Observable<User> {
    return this.http.put<User>("http://localhost:8080/users/update/settings", settingsData, {withCredentials: true}).pipe(
      tap(() => {
        this.authService.setCurrentUser(settingsData);
      })
    )
  }
}

