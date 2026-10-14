import { Component, inject, ChangeDetectionStrategy } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatToolbar } from '@angular/material/toolbar';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth/auth-service';
import { ThemeService } from '../../services/theme/theme.service';
import { environment } from '../../environments/environment';

@Component({
  selector: 'app-header',
  imports: [
    MatToolbar,
    RouterLink,
    MatButtonModule,
    MatIconModule,
  ],
  templateUrl: './header.component.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrl: './header.component.scss'
})
export class HeaderComponent {
  protected readonly authService = inject(AuthService);
  protected readonly themeService = inject(ThemeService);
  protected readonly registrationOpen = environment.registrationOpen;
  private readonly router = inject(Router);

  logout() {
    this.authService.logout().subscribe({
      next: () => {
        this.router.navigate(['/app-login']);
      },
    });
  }
}
