import { Routes } from '@angular/router';
import { AuthGuard } from '../guards/auth.guards';
import { NoAuthGaurd } from '../guards/no-auth.guards';
import { HomeComponent } from '../pages/home/home.component';
import { LoginComponent } from '../pages/login/login.component';
import { PageNotFoundComponent } from '../pages/page-not-found/page-not-found.component';
import { PasswordForgottenComponent } from '../pages/password-forgotten/password-forgotten.component';
import { SignUpComponent } from '../pages/sign-up/sign-up.component';

// Signed-in pages load on demand so the first screen (login) stays small.
export const routes: Routes = [
  { path: '', component: LoginComponent },
  { path: 'app-home', component: HomeComponent },
  { path: 'app-login', component: LoginComponent, canActivate: [NoAuthGaurd] },
  { path: 'app-sign-up', component: SignUpComponent, canActivate: [NoAuthGaurd] },
  { path: 'app-password-forgotten', component: PasswordForgottenComponent },
  { path: 'app-logout', redirectTo: '/app-login' },
  { path: 'app-dashboard', loadComponent: () => import('../pages/dashboard/dashboard.component').then(m => m.DashboardComponent), canActivate: [AuthGuard] },
  { path: 'companies', loadComponent: () => import('../pages/companies/companies.component').then(m => m.CompaniesComponent), canActivate: [AuthGuard] },
  { path: 'company/:id', loadComponent: () => import('../pages/companies/details/company.details.component').then(m => m.CompanyDetailsComponent), canActivate: [AuthGuard] },
  { path: 'app-settings', loadComponent: () => import('../pages/settings/settings.component').then(m => m.SettingsComponent), canActivate: [AuthGuard] },
  { path: 'app-profile', loadComponent: () => import('../pages/profile/profile.component').then(m => m.ProfileComponent), canActivate: [AuthGuard] },
  { path: 'contacts', loadComponent: () => import('../pages/contacts/contacts.component').then(m => m.ContactsComponent), canActivate: [AuthGuard] },
  { path: 'contact/:id', loadComponent: () => import('../pages/contacts/details/contact.details.component').then(m => m.ContactDetailsComponent), canActivate: [AuthGuard] },
  { path: 'opportunities', loadComponent: () => import('../pages/opportunity/opportunity.component').then(m => m.OpportunityComponent), canActivate: [AuthGuard] },
  { path: 'calendar', loadComponent: () => import('../pages/calendrier/calendrier.component').then(m => m.CalendrierComponent), canActivate: [AuthGuard] },
  { path: 'workflow', loadComponent: () => import('../pages/workflow/workflow.component').then(m => m.WorkflowComponent), canActivate: [AuthGuard] },
  { path: 'reporting', loadComponent: () => import('../pages/reporting/reporting.component').then(m => m.ReportingComponent), canActivate: [AuthGuard] },
  { path: 'documents', loadComponent: () => import('../pages/documents/documents.component').then(m => m.DocumentsComponent), canActivate: [AuthGuard] },

  { path: 'opportunity', redirectTo: 'opportunities' },
  { path: 'calendrier', redirectTo: 'calendar' },
  { path: 'app-document', redirectTo: 'documents' },
  { path: '**', component: PageNotFoundComponent },

];
