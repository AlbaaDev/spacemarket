import {Component, computed, inject, Signal, signal, WritableSignal} from '@angular/core';
import {HeaderComponent} from '../components/header/header.component';
import {MatSidenav, MatSidenavContainer, MatSidenavModule} from '@angular/material/sidenav';
import {MatButton, MatButtonModule} from '@angular/material/button';
import {RouterLink, RouterOutlet} from '@angular/router';
import { AuthService } from '../services/auth-service';
import { SidebarComponent } from '../components/sidebar/sidebar.component';
import { MatList, MatListItem, MatListModule } from '@angular/material/list';

@Component({
  selector: 'app-root',
  imports: [SidebarComponent, HeaderComponent, MatSidenavContainer, MatButtonModule, MatSidenav, MatSidenavModule, MatListModule, RouterOutlet],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'front';
  protected readonly authService = inject(AuthService);

  constructor() {
  }
}
