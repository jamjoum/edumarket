import { ApplicationConfig, provideZoneChangeDetection } from '@angular/core';
import { provideRouter, withComponentInputBinding, withViewTransitions } from '@angular/router';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { provideAnimations } from '@angular/platform-browser/animations';
import { routes } from './app.routes';
import { authInterceptor } from './core/interceptors/auth.interceptor';

/**
 * ┌─────────────────────────────────────────────────────────────┐
 * │  Configuration Angular 17 – Standalone API                  │
 * │  Plus de NgModule : tout est fourni via providers.          │
 * └─────────────────────────────────────────────────────────────┘
 */
export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(
      routes,
      withComponentInputBinding(),   // Liaison route params → @Input
      withViewTransitions()          // Transitions de page fluides
    ),
    provideHttpClient(
      withInterceptors([authInterceptor])
    ),
    provideAnimations(),
  ]
};
