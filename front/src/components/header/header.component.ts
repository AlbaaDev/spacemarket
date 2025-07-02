import { Component, inject } from '@angular/core';
import {MatToolbar} from '@angular/material/toolbar';
import {Router, RouterLink, RouterLinkActive} from '@angular/router';
import { AuthServiceService } from '../../app/services/auth-service.service';

@Component({
  selector: 'app-header',
  imports: [
    MatToolbar,
    RouterLink,
    RouterLinkActive
  ],
  templateUrl: './header.component.html',
  styleUrl: './header.component.css'
})
export class HeaderComponent {

  protected readonly authService = inject(AuthServiceService);
  private readonly router = inject(Router);

  logout() {
    localStorage.removeItem("authToken");
    localStorage.removeItem("tokenExpiration");
    this.router.navigate(['/app-login']);
  }
}
