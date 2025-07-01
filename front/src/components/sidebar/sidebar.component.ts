import { Component, inject } from '@angular/core';
import { AuthServiceService } from '../../app/services/auth-service.service';

@Component({
  selector: 'app-sidebar',
  imports: [],
  templateUrl: './sidebar.component.html',
  styleUrl: './sidebar.component.css'
})
export class SidebarComponent {
  protected readonly authService = inject(AuthServiceService);
}
