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
import {PageNotFoundComponent} from '../pages/page-not-found/page-not-found.component';

export const routes: Routes = [
  { path: 'app-home', component: HomeComponent },
  { path: 'app-contact', component: ContactComponent },
  { path: 'app-calendrier', component: CalendrierComponent },
  { path: 'app-opportunite', component: OpportuniteComponent },
  { path: 'app-automation', component: AutomationComponent },
  { path: 'app-workflow', component: WorkflowComponent },
  { path: 'app-reporting', component: ReportingComponent },
  { path: 'app-media', component: MediaComponent },
  { path: 'app-login', component: LoginComponent },
  { path: 'app-sign-up', component: SignUpComponent },
  { path: 'app-settings', component: SettingsComponent },
  { path: 'app-profile', component: ProfileComponent },
  { path: '**', component: PageNotFoundComponent },
];
