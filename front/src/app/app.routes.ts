import { Routes } from '@angular/router';
import { AuthGuard } from '../guards/auth.guards';
import { NoAuthGaurd } from '../guards/no-auth.guards';
import { CalendrierComponent } from '../pages/calendrier/calendrier.component';
import { CompaniesComponent } from '../pages/companies/companies.component';
import { ContactComponent } from '../pages/contact/contact.component';
import { DashboardComponent } from '../pages/dashboard/dashboard.component';
import { DocumentsComponent } from '../pages/documents/documents.component';
import { HomeComponent } from '../pages/home/home.component';
import { LoginComponent } from '../pages/login/login.component';
import { OpportunityComponent } from '../pages/opportunity/opportunity.component';
import { PageNotFoundComponent } from '../pages/page-not-found/page-not-found.component';
import { PasswordForgottenComponent } from '../pages/password-forgotten/password-forgotten.component';
import { ProfileComponent } from '../pages/profile/profile.component';
import { ReportingComponent } from '../pages/reporting/reporting.component';
import { SettingsComponent } from '../pages/settings/settings.component';
import { SignUpComponent } from '../pages/sign-up/sign-up.component';
import { WorkflowComponent } from '../pages/workflow/workflow.component';

export const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'app-home', component: HomeComponent },
  { path: 'app-login', component: LoginComponent, canActivate: [NoAuthGaurd] },
  { path: 'app-sign-up', component: SignUpComponent, canActivate: [NoAuthGaurd] },
  { path: 'app-password-forgotten', component: PasswordForgottenComponent },
  { path: 'app-logout', redirectTo: '/app-login' },

  { path: 'app-dashboard', component: DashboardComponent, canActivate: [AuthGuard] },
  { path: 'app-companies', component: CompaniesComponent, canActivate: [AuthGuard] },
  { path: 'app-settings', component: SettingsComponent, canActivate: [AuthGuard] },
  { path: 'app-profile', component: ProfileComponent, canActivate: [AuthGuard] },
  { path: 'app-contact', component: ContactComponent, canActivate: [AuthGuard] },
  { path: 'app-opportunity', component: OpportunityComponent, canActivate: [AuthGuard] },
  { path: 'app-calendrier', component: CalendrierComponent, canActivate: [AuthGuard] },
  { path: 'app-workflow', component: WorkflowComponent, canActivate: [AuthGuard] },
  { path: 'app-reporting', component: ReportingComponent, canActivate: [AuthGuard] },
  { path: 'app-document', component: DocumentsComponent, canActivate: [AuthGuard] },

  { path: '**', component: PageNotFoundComponent },

];
