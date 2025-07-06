import { Routes } from '@angular/router';
import {LoginComponent} from '../pages/login/login.component';
import {SignUpComponent} from '../pages/sign-up/sign-up.component';
import {SettingsComponent} from '../pages/settings/settings.component';
import {HomeComponent} from '../pages/home/home.component';
import {ProfileComponent} from '../pages/profile/profile.component';
import {ContactComponent} from '../pages/contact/contact.component';
import {CalendrierComponent} from '../pages/calendrier/calendrier.component';
import {OpportuniteComponent} from '../pages/opportunite/opportunite.component';
import {AutomationComponent} from '../pages/automation/automation.component';
import {WorkflowComponent} from '../pages/workflow/workflow.component';
import {ReportingComponent} from '../pages/reporting/reporting.component';
import {MediaComponent} from '../pages/media/media.component';
import { AuthGuard } from '../guards/auth.guards';
import { DashboardComponent } from '../pages/dashboard/dashboard.component';
import { AuthenticatedLayoutComponent } from './authenticated-layout-component/authenticated-layout-component';

export const routes: Routes = [
  { path: 'app-home', component: HomeComponent },
  { path: 'app-login', component: LoginComponent },
  { path: 'app-sign-up', component: SignUpComponent },
  { path: 'app-logout', redirectTo: '/app-login' },

  { path: 'app-settings', component: SettingsComponent, canActivate: [AuthGuard] },
  { path: 'app-profile', component: ProfileComponent, canActivate: [AuthGuard] },
  { path: 'app-contact', component: ContactComponent, canActivate: [AuthGuard] },
  { path: 'app-calendrier', component: CalendrierComponent, canActivate: [AuthGuard] },
  { path: 'app-opportunite', component: OpportuniteComponent, canActivate: [AuthGuard] },
  { path: 'app-automation', component: AutomationComponent, canActivate: [AuthGuard] },
  { path: 'app-workflow', component: WorkflowComponent, canActivate: [AuthGuard] },
  { path: 'app-reporting', component: ReportingComponent, canActivate: [AuthGuard] },
  { path: 'app-media', component: MediaComponent, canActivate: [AuthGuard] },
  { path: 'app-dashboard', component: DashboardComponent, canActivate: [AuthGuard] },

  // { path: '**', redirectTo: '/app-page-not-found' },
];
