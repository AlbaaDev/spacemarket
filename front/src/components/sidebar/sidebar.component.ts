import { Component, inject } from '@angular/core';
import { AuthService } from '../../services/auth-service';
import { MatSidenav } from '@angular/material/sidenav';
import { MatList, MatListItem } from '@angular/material/list';
import { MatButtonModule } from '@angular/material/button';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-sidebar',
  imports: [MatList, MatListItem, MatButtonModule, RouterLink],
  templateUrl: './sidebar.component.html',
  styleUrl: './sidebar.component.css'
})
export class SidebarComponent {
  protected readonly authService = inject(AuthService);
}
