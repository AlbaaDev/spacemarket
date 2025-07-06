import { Component, inject } from '@angular/core';
import {MatToolbar} from '@angular/material/toolbar';
import {Router, RouterLink, RouterLinkActive} from '@angular/router';
import { AuthService } from '../../services/auth-service';
import { MatButton } from '@angular/material/button';

@Component({
  selector: 'app-header',
  imports: [
    MatToolbar,
    RouterLink,
    MatButton,
    RouterLinkActive
  ],
  templateUrl: './header.component.html',
  styleUrl: './header.component.css'
})
export class HeaderComponent {
  protected readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  logout() {
    this.authService.logout().subscribe({
      next: () => {
        this.router.navigate(['/app-home']);
      },
    });
  }
}
