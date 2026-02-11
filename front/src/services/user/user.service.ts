import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { User } from '../../interfaces/User';
import { Observable, tap } from 'rxjs';
import { AuthService } from '../auth/auth-service';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private readonly http = inject(HttpClient);
  private readonly authService = inject(AuthService);

  constructor() {

   }

  updateProfile(profileData: User) : Observable<User> {
      return this.http.put<User>(environment.baseUrl + '/users/me/profile', profileData, {withCredentials: true}).pipe(
        tap(() => {
          this.authService.setCurrentUser(profileData);
        })
      )
   }

   updateSettings(userNameData: User) : Observable<User> {
    return this.http.put<User>(environment.baseUrl + '/users/me/settings', userNameData, {withCredentials: true}).pipe(
      tap(() => {
        this.authService.setCurrentUser(userNameData);
      })
    )
  }

  updatePassword(passwordData: User) : Observable<User> {
    return this.http.put<User>(environment.baseUrl + '/users/me/password', passwordData, {withCredentials: true});
  }
}

