import {Component, computed, Signal, signal, WritableSignal} from '@angular/core';
import {HeaderComponent} from '../components/header/header.component';
import {MatSidenav, MatSidenavContainer, MatSidenavModule} from '@angular/material/sidenav';
import {MatButton} from '@angular/material/button';
import {RouterLink, RouterOutlet} from '@angular/router';
import {MatList, MatListItem} from '@angular/material/list';

@Component({
  selector: 'app-root',
  imports: [HeaderComponent, MatSidenavContainer, MatSidenav, MatSidenavModule, RouterOutlet, MatButton, MatList, MatListItem, RouterLink],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'front';
  count: WritableSignal<number> = signal(0);
  doubleCount: Signal<number> = computed(() => this.count() * 2);

  constructor() {
    this.count.set(2);
  }
}
