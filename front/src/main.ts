import { bootstrapApplication } from '@angular/platform-browser';
import { appConfig } from './app/app.config';
import { AppComponent } from './app/app.component';
import { environment } from './environments/environment';

// Loaded on demand so the Sentry SDK never weighs on the production bundle.
const errorReporting = environment.production
  ? Promise.resolve()
  : import('./app/spotlight').then(m => m.startSpotlight());

errorReporting
  .then(() => bootstrapApplication(AppComponent, appConfig))
  .catch((err) => console.error(err));
