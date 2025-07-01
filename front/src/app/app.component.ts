import {Component, computed, inject, Signal, signal, WritableSignal} from '@angular/core';
import {HeaderComponent} from '../components/header/header.component';
import {MatSidenav, MatSidenavContainer, MatSidenavModule} from '@angular/material/sidenav';
import {MatButton} from '@angular/material/button';
import {RouterLink, RouterOutlet} from '@angular/router';
import {MatList, MatListItem} from '@angular/material/list';
import { AuthServiceService } from './services/auth-service.service';

@Component({
  selector: 'app-root',
  imports: [HeaderComponent, MatSidenavContainer, MatSidenav, MatSidenavModule, RouterOutlet, MatButton, MatList, MatListItem, RouterLink],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'front';
  protected readonly authService = inject(AuthServiceService);

  constructor() {
  }
}
