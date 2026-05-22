import { Injectable, signal, computed } from '@angular/core';
import { User } from '../models/models';

/**
 * Service d'authentification – état géré par Signals.
 *
 * ► SINGLETON PATTERN :
 * providedIn: 'root' garantit une instance unique (Singleton)
 * partagée dans toute l'application Angular.
 * C'est l'équivalent Angular du Singleton Spring (@Service).
 */
@Injectable({ providedIn: 'root' })
export class AuthService {

  // Signal privé – mutable uniquement dans ce service
  private readonly _currentUser = signal<User | null>(null);

  // Computed publics – lecture seule pour les composants
  readonly currentUser  = this._currentUser.asReadonly();
  readonly isLoggedIn   = computed(() => this._currentUser() !== null);
  readonly isPremium    = computed(() => this._currentUser()?.premium ?? false);
  readonly isInstructor = computed(() => this._currentUser()?.role === 'INSTRUCTOR');

  /**
   * Simule une connexion (à remplacer par un vrai appel JWT en prod).
   */
  loginDemo(): void {
    this._currentUser.set({
      id: 'b0000000-0000-0000-0000-000000000001',
      email: 'etudiant1@example.com',
      fullName: 'Jean-Paul Tremblay',
      premium: true,
      role: 'STUDENT'
    });
  }

  logout(): void {
    this._currentUser.set(null);
  }
}
