import { inject, Injectable } from "@angular/core";
import { ActivatedRouteSnapshot, CanActivate, CanActivateFn, GuardResult, MaybeAsync, Router, RouterStateSnapshot, UrlTree } from "@angular/router";
import { Observable } from "rxjs/internal/Observable";
import { catchError, map, of } from "rxjs";
import { AuthService } from "../services/auth/auth-service";

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {
  constructor(
    private readonly authService: AuthService,
    private readonly router: Router
  ) {}


  canActivate(): Observable<boolean | UrlTree> {
    return this.authService.isAuth().pipe(
      map(() => true),
      catchError(() => of(this.router.parseUrl('/app-login')))
    );
  }
} 