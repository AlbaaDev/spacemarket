import { Component, inject } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatListModule } from '@angular/material/list';
import { MatSidenav, MatSidenavContainer, MatSidenavModule } from '@angular/material/sidenav';
import { RouterOutlet } from '@angular/router';
import { HeaderComponent } from '../components/header/header.component';
import { SidebarComponent } from '../components/sidebar/sidebar.component';
import { AuthService } from '../services/auth/auth-service';
import { environment } from '../environments/environment';

@Component({
  selector: 'app-root',
  imports: [SidebarComponent, HeaderComponent, MatSidenavContainer, MatButtonModule, MatSidenav, MatSidenavModule, MatListModule, RouterOutlet],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
    

  title = 'SpaceMarket';
  protected readonly authService = inject(AuthService);

  constructor() {

  }
}
