import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  {
    path: '',
    redirectTo: 'catalog',
    pathMatch: 'full'
  },
  {
    path: 'catalog',
    loadComponent: () =>
      import('./features/catalog/catalog.component').then(m => m.CatalogComponent),
    title: 'EduMarket – Catalogue'
  },
  {
    path: 'courses/:id',
    loadComponent: () =>
      import('./features/course-detail/course-detail.component').then(m => m.CourseDetailComponent),
    title: 'Détail du cours'
  },
  {
    path: 'subscription',
    loadComponent: () =>
      import('./features/subscription/subscription.component').then(m => m.SubscriptionComponent),
    title: 'Abonnement Premium',
    canActivate: [authGuard]
  },
  {
    path: 'checkout/:courseId',
    loadComponent: () =>
      import('./features/checkout/checkout.component').then(m => m.CheckoutComponent),
    title: 'Paiement',
    canActivate: [authGuard]
  },
  {
    path: '**',
    redirectTo: 'catalog'
  }
];
