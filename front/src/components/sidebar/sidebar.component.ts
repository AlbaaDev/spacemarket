import { Component } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { MatNavList } from '@angular/material/list';
import { MatListModule } from '@angular/material/list';
import { RouterLink, RouterLinkActive } from '@angular/router';

@Component({
  selector: 'app-sidebar',
  imports: [MatNavList, MatListModule, MatIconModule, RouterLink, RouterLinkActive],
  templateUrl: './sidebar.component.html',
  styleUrl: './sidebar.component.css'
})
export class SidebarComponent {
  readonly items = [
    { path: '/app-dashboard', label: 'Dashboard', icon: 'dashboard' },
    { path: '/contacts', label: 'Contacts', icon: 'people' },
    { path: '/companies', label: 'Companies', icon: 'domain' },
    { path: '/opportunities', label: 'Opportunities', icon: 'trending_up' },
    { path: '/calendar', label: 'Calendar', icon: 'event' },
    { path: '/workflow', label: 'Workflow', icon: 'account_tree' },
    { path: '/reporting', label: 'Reporting', icon: 'bar_chart' },
    { path: '/documents', label: 'Documents', icon: 'description' },
  ] as const;
}
