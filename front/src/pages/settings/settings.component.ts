import { Component, inject } from '@angular/core';
import { AuthService } from '../../services/auth-service';

@Component({
  selector: 'app-settings',
  imports: [],
  templateUrl: './settings.component.html',
  styleUrl: './settings.component.css'
})
export class SettingsComponent {

  private readonly userService = inject(AuthService);

  constructor() {
      
  }

  getEmail () {
    // this.userService.currentUser.email;
  }

  ngOnInit() {
  }
}
