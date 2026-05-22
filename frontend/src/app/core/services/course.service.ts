import { Injectable, signal, computed, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { toSignal } from '@angular/core/rxjs-interop';
import { Observable, tap, catchError, EMPTY } from 'rxjs';
import { CourseSummary, CourseDetail, CourseLevel } from '../models/models';
import { environment } from '../../../environments/environment';

/**
 * ┌─────────────────────────────────────────────────────────────┐
 * │  Angular 17 – Signals Architecture                          │
 * ├─────────────────────────────────────────────────────────────┤
 * │  Les Signals remplacent les BehaviorSubject RxJS pour       │
 * │  la gestion d'état local :                                  │
 * │    • signal()   → état mutable réactif                      │
 * │    • computed() → valeur dérivée (mémoïsée)                 │
 * │    • effect()   → effets de bord (dans les composants)      │
 * │                                                             │
 * │  Avantages : pas de subscribe(), plus simple, meilleure     │
 * │  détection des changements (zone-less ready).               │
 * └─────────────────────────────────────────────────────────────┘
 */
@Injectable({ providedIn: 'root' })
export class CourseService {

  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/courses`;

  // ── État global via Signals ───────────────────────────────────────────

  /** Liste brute du catalogue */
  private readonly _courses = signal<CourseSummary[]>([]);

  /** Filtre actif niveau */
  readonly levelFilter = signal<CourseLevel | null>(null);

  /** Filtre premium */
  readonly premiumFilter = signal<boolean | null>(null);

  /** État de chargement */
  readonly loading = signal<boolean>(false);

  /** Erreur courante */
  readonly error = signal<string | null>(null);

  // ── Computed Signals (valeurs dérivées) ───────────────────────────────

  /** Cours filtrés côté client selon les filtres actifs */
  readonly courses = computed(() => {
    let list = this._courses();

    const level   = this.levelFilter();
    const premium = this.premiumFilter();

    if (level)   list = list.filter(c => c.level === level);
    if (premium !== null) list = list.filter(c => c.premium === premium);

    return list;
  });

  /** Nombre de cours visibles */
  readonly coursesCount = computed(() => this.courses().length);

  /** Cours gratuits uniquement */
  readonly freeCourses = computed(() =>
    this._courses().filter(c => !c.premium)
  );

  /** Cours premium uniquement */
  readonly premiumCourses = computed(() =>
    this._courses().filter(c => c.premium)
  );

  // ── Actions ───────────────────────────────────────────────────────────

  loadAll(): void {
    this.loading.set(true);
    this.error.set(null);

    this.http.get<CourseSummary[]>(this.baseUrl).pipe(
      tap(data => {
        this._courses.set(data);
        this.loading.set(false);
      }),
      catchError(err => {
        this.error.set('Impossible de charger les cours. Réessayez plus tard.');
        this.loading.set(false);
        return EMPTY;
      })
    ).subscribe();
  }

  loadByCategory(categoryId: number): void {
    this.loading.set(true);
    this.http.get<CourseSummary[]>(`${this.baseUrl}/category/${categoryId}`).pipe(
      tap(data => {
        this._courses.set(data);
        this.loading.set(false);
      }),
      catchError(() => {
        this.loading.set(false);
        return EMPTY;
      })
    ).subscribe();
  }

  getById(id: string): Observable<CourseDetail> {
    return this.http.get<CourseDetail>(`${this.baseUrl}/${id}`);
  }

  setLevelFilter(level: CourseLevel | null): void {
    this.levelFilter.set(level);
  }

  setPremiumFilter(premium: boolean | null): void {
    this.premiumFilter.set(premium);
  }

  clearFilters(): void {
    this.levelFilter.set(null);
    this.premiumFilter.set(null);
  }
}
