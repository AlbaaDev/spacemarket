import * as Sentry from '@sentry/angular';

/**
 * Development only: reports browser errors to Spotlight on this machine (npm run spotlight,
 * then http://localhost:8969). There is no DSN, so nothing is sent to Sentry's servers.
 * Every console.error is reported too: Angular's default error handler and this app's
 * failed requests both end there.
 */
export function startSpotlight() {
  Sentry.init({
    environment: 'dev',
    integrations: [
      Sentry.spotlightBrowserIntegration(),
      Sentry.captureConsoleIntegration({ levels: ['error'] }),
    ],
  });
}
