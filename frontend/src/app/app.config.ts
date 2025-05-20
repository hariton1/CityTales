import {ApplicationConfig, importProvidersFrom, provideZoneChangeDetection, isDevMode} from '@angular/core';
import { provideRouter } from '@angular/router';

import { routes } from './app.routes';
import { provideClientHydration, withEventReplay } from '@angular/platform-browser';

import { provideAnimations, BrowserAnimationsModule } from '@angular/platform-browser/animations';
import {TuiAlertService, tuiAssetsPathProvider} from '@taiga-ui/core';
import { provideServiceWorker } from '@angular/service-worker';
import {provideHttpClient, withFetch} from '@angular/common/http';
import {provideEventPlugins} from '@taiga-ui/event-plugins';

export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes),
    provideClientHydration(withEventReplay()),
    provideHttpClient(withFetch()),
    tuiAssetsPathProvider('https://taiga-ui.dev/assets/taiga-ui/icons'),
    provideAnimations(),
    provideEventPlugins(),
    importProvidersFrom(
      BrowserAnimationsModule,
      TuiAlertService
    ), provideServiceWorker('ngsw-worker.js', {
            enabled: !isDevMode(),
            registrationStrategy: 'registerWhenStable:30000'
          }), provideServiceWorker('ngsw-worker.js', {
            enabled: !isDevMode(),
            registrationStrategy: 'registerWhenStable:30000'
          })
  ]
};
