import {
  Component, OnInit, inject, ChangeDetectionStrategy, effect
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CourseService } from '../../core/services/course.service';
import { AuthService } from '../../core/services/auth.service';
import { CourseLevel } from '../../core/models/models';

/**
 * ┌─────────────────────────────────────────────────────────────┐
 * │  Catalogue de cours – Composant Standalone Angular 17       │
 * ├─────────────────────────────────────────────────────────────┤
 * │  • ChangeDetectionStrategy.OnPush : mise à jour uniquement  │
 * │    quand les Signals changent (performance optimale).       │
 * │  • Signals : pas de subscribe() manuel, réactivité fine.    │
 * │  • effect() : log de débogage sur changement de filtre.     │
 * └─────────────────────────────────────────────────────────────┘
 */
@Component({
  selector: 'app-catalog',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div class="catalog-page">
      <!-- ── En-tête ── -->
      <header class="catalog-header">
        <h1>Catalogue de cours</h1>
        <p>{{ courseService.coursesCount() }} cours disponibles</p>

        @if (!authService.isLoggedIn()) {
          <button class="btn-demo" (click)="authService.loginDemo()">
            🎓 Connexion démo
          </button>
        } @else {
          <span class="user-badge">
            {{ authService.currentUser()?.fullName }}
            @if (authService.isPremium()) { <span class="premium-tag">⭐ Premium</span> }
          </span>
        }
      </header>

      <!-- ── Filtres ── -->
      <section class="filters" aria-label="Filtres">
        <div class="filter-group">
          <label>Niveau</label>
          <select [value]="courseService.levelFilter()"
                  (change)="onLevelChange($event)">
            <option value="">Tous</option>
            <option value="BEGINNER">Débutant</option>
            <option value="INTERMEDIATE">Intermédiaire</option>
            <option value="ADVANCED">Avancé</option>
          </select>
        </div>

        <div class="filter-group">
          <label>Accès</label>
          <select [value]="courseService.premiumFilter()"
                  (change)="onPremiumChange($event)">
            <option value="">Tous</option>
            <option value="false">Gratuit / À l'unité</option>
            <option value="true">Premium uniquement</option>
          </select>
        </div>

        <button class="btn-clear" (click)="courseService.clearFilters()">
          Réinitialiser
        </button>
      </section>

      <!-- ── Chargement ── -->
      @if (courseService.loading()) {
        <div class="loading-grid">
          @for (i of [1,2,3,4,5,6]; track i) {
            <div class="card-skeleton"></div>
          }
        </div>
      }

      <!-- ── Erreur ── -->
      @if (courseService.error()) {
        <div class="error-banner" role="alert">
          ⚠️ {{ courseService.error() }}
          <button (click)="courseService.loadAll()">Réessayer</button>
        </div>
      }

      <!-- ── Grille de cours ── -->
      @if (!courseService.loading() && !courseService.error()) {
        <div class="courses-grid">
          @for (course of courseService.courses(); track course.id) {
            <article class="course-card" [class.is-premium]="course.premium">
              <div class="card-thumbnail">
                @if (course.thumbnailUrl) {
                  <img [src]="course.thumbnailUrl" [alt]="course.title" loading="lazy"/>
                } @else {
                  <div class="thumbnail-placeholder">📚</div>
                }
                @if (course.premium) {
                  <span class="badge-premium">⭐ Premium</span>
                }
              </div>
              <div class="card-body">
                <span class="category-tag">{{ course.categoryName }}</span>
                <h2 class="card-title">{{ course.title }}</h2>
                <p class="card-instructor">par {{ course.instructorName }}</p>
                <div class="card-meta">
                  <span class="level-badge level-{{ course.level | lowercase }}">
                    {{ levelLabel(course.level) }}
                  </span>
                  <span class="price">
                    {{ course.premium ? 'Inclus Premium' : (course.price | currency:'EUR') }}
                  </span>
                </div>
              </div>
              <div class="card-footer">
                <a [routerLink]="['/courses', course.id]" class="btn-detail">
                  Voir le cours
                </a>
                @if (!course.premium) {
                  <a [routerLink]="['/checkout', course.id]" class="btn-buy">
                    Acheter
                  </a>
                }
              </div>
            </article>
          } @empty {
            <div class="empty-state">
              <p>Aucun cours ne correspond à vos filtres.</p>
              <button (click)="courseService.clearFilters()">Voir tous les cours</button>
            </div>
          }
        </div>
      }

      <!-- ── Bannière Premium ── -->
      @if (!authService.isPremium()) {
        <div class="premium-banner">
          <h2>🚀 Accédez à tout le contenu Premium</h2>
          <p>Machine Learning, Docker, Ethical Hacking et plus encore.</p>
          <a routerLink="/subscription" class="btn-premium">
            Voir les abonnements
          </a>
        </div>
      }
    </div>
  `,
  styles: [`
    .catalog-page { max-width: 1200px; margin: 0 auto; padding: 2rem; }

    .catalog-header {
      display: flex; align-items: center; gap: 1rem; flex-wrap: wrap;
      margin-bottom: 2rem;
    }
    .catalog-header h1 { font-size: 2rem; font-weight: 700; flex: 1; }

    .filters {
      display: flex; gap: 1rem; align-items: flex-end;
      background: #f8f9fa; padding: 1rem; border-radius: 8px;
      margin-bottom: 2rem; flex-wrap: wrap;
    }
    .filter-group { display: flex; flex-direction: column; gap: .25rem; }
    .filter-group label { font-size: .8rem; font-weight: 600; color: #555; }
    .filter-group select {
      padding: .5rem .75rem; border: 1px solid #ddd;
      border-radius: 6px; font-size: .95rem;
    }

    .courses-grid {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
      gap: 1.5rem;
    }

    .course-card {
      border: 1px solid #e5e7eb; border-radius: 12px; overflow: hidden;
      display: flex; flex-direction: column; transition: transform .2s, box-shadow .2s;
      background: #fff;
    }
    .course-card:hover { transform: translateY(-4px); box-shadow: 0 8px 24px rgba(0,0,0,.1); }
    .course-card.is-premium { border-color: #f59e0b; }

    .card-thumbnail {
      position: relative; height: 180px; background: linear-gradient(135deg, #667eea, #764ba2);
      display: flex; align-items: center; justify-content: center;
    }
    .card-thumbnail img { width: 100%; height: 100%; object-fit: cover; }
    .thumbnail-placeholder { font-size: 3rem; }
    .badge-premium {
      position: absolute; top: .75rem; right: .75rem;
      background: #f59e0b; color: #fff; padding: .2rem .6rem;
      border-radius: 20px; font-size: .75rem; font-weight: 600;
    }

    .card-body { padding: 1rem; flex: 1; }
    .category-tag { font-size: .75rem; color: #6366f1; font-weight: 600; text-transform: uppercase; }
    .card-title { font-size: 1rem; font-weight: 700; margin: .5rem 0; line-height: 1.4; }
    .card-instructor { font-size: .85rem; color: #6b7280; }
    .card-meta { display: flex; justify-content: space-between; align-items: center; margin-top: .75rem; }
    .price { font-weight: 700; color: #111; font-size: 1.1rem; }

    .level-badge {
      font-size: .7rem; padding: .2rem .5rem; border-radius: 4px; font-weight: 600;
    }
    .level-beginner     { background: #d1fae5; color: #065f46; }
    .level-intermediate { background: #fef3c7; color: #92400e; }
    .level-advanced     { background: #fee2e2; color: #991b1b; }

    .card-footer {
      padding: 1rem; border-top: 1px solid #f3f4f6;
      display: flex; gap: .5rem;
    }
    .btn-detail, .btn-buy {
      flex: 1; padding: .6rem; border-radius: 8px; text-align: center;
      font-size: .9rem; font-weight: 600; cursor: pointer; text-decoration: none;
    }
    .btn-detail { background: #f3f4f6; color: #111; }
    .btn-detail:hover { background: #e5e7eb; }
    .btn-buy { background: #6366f1; color: #fff; }
    .btn-buy:hover { background: #4f46e5; }

    .card-skeleton {
      height: 360px; background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
      background-size: 200% 100%; animation: shimmer 1.5s infinite;
      border-radius: 12px;
    }
    @keyframes shimmer { 0% { background-position: 200% 0 } 100% { background-position: -200% 0 } }

    .loading-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(300px,1fr)); gap: 1.5rem; }

    .error-banner {
      background: #fee2e2; color: #991b1b; padding: 1rem; border-radius: 8px;
      display: flex; gap: 1rem; align-items: center; margin-bottom: 1rem;
    }

    .premium-banner {
      margin-top: 3rem; background: linear-gradient(135deg, #667eea, #764ba2);
      color: #fff; padding: 3rem; border-radius: 16px; text-align: center;
    }
    .premium-banner h2 { font-size: 1.8rem; margin-bottom: .5rem; }
    .btn-premium {
      display: inline-block; margin-top: 1rem; background: #fff; color: #667eea;
      padding: .75rem 2rem; border-radius: 8px; font-weight: 700; text-decoration: none;
    }

    .btn-demo { background: #6366f1; color: #fff; border: none; padding: .6rem 1.2rem; border-radius: 8px; cursor: pointer; }
    .btn-clear { background: transparent; border: 1px solid #ddd; padding: .5rem .75rem; border-radius: 6px; cursor: pointer; }
    .user-badge { font-size: .9rem; color: #374151; }
    .premium-tag { background: #f59e0b; color: #fff; padding: .1rem .4rem; border-radius: 4px; font-size: .75rem; margin-left: .4rem; }
    .empty-state { grid-column: 1/-1; text-align: center; padding: 3rem; color: #6b7280; }
  `]
})
export class CatalogComponent implements OnInit {
  readonly courseService = inject(CourseService);
  readonly authService   = inject(AuthService);

  constructor() {
    // effect() : s'exécute quand les Signals changent
    effect(() => {
      console.debug('[Catalog] Filtre niveau:', this.courseService.levelFilter());
      console.debug('[Catalog] Filtre premium:', this.courseService.premiumFilter());
      console.debug('[Catalog] Cours affichés:', this.courseService.coursesCount());
    });
  }

  ngOnInit(): void {
    this.courseService.loadAll();
  }

  onLevelChange(event: Event): void {
    const value = (event.target as HTMLSelectElement).value as CourseLevel | '';
    this.courseService.setLevelFilter(value || null);
  }

  onPremiumChange(event: Event): void {
    const value = (event.target as HTMLSelectElement).value;
    this.courseService.setPremiumFilter(value === '' ? null : value === 'true');
  }

  levelLabel(level: string): string {
    return { BEGINNER: 'Débutant', INTERMEDIATE: 'Intermédiaire', ADVANCED: 'Avancé' }[level] ?? level;
  }
}
