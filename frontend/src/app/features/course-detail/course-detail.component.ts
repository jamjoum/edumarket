import { Component, OnInit, inject, signal, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { CourseService } from '../../core/services/course.service';
import { AuthService } from '../../core/services/auth.service';
import { CourseDetail } from '../../core/models/models';

@Component({
  selector: 'app-course-detail',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <div class="detail-page">
      @if (loading()) {
        <div class="spinner">Chargement…</div>
      } @else if (course()) {
        <div class="detail-hero">
          <div class="hero-content">
            <span class="category-tag">{{ course()!.categoryName }}</span>
            <h1>{{ course()!.title }}</h1>
            <p class="instructor">par <strong>{{ course()!.instructorName }}</strong></p>
            <div class="meta-row">
              <span class="badge level-{{ course()!.level | lowercase }}">
                {{ course()!.level }}
              </span>
              <span>🕒 {{ course()!.durationHours }}h</span>
              <span>🌐 {{ course()!.language | uppercase }}</span>
              @if (course()!.premium) {
                <span class="badge-premium">⭐ Abonnement Premium</span>
              }
            </div>
            <p class="description">{{ course()!.description }}</p>
          </div>
          <div class="hero-card">
            <div class="price-display">
              @if (course()!.premium) {
                <span class="price-premium">Inclus dans votre abonnement</span>
              } @else {
                <span class="price">{{ course()!.price | currency:'EUR' }}</span>
              }
            </div>
            @if (authService.isPremium() && course()!.premium) {
              <button class="btn-enroll">
                ✅ Accéder au cours
              </button>
            } @else if (!course()!.premium) {
              <a [routerLink]="['/checkout', course()!.id]" class="btn-enroll">
                Acheter ce cours
              </a>
            } @else {
              <a routerLink="/subscription" class="btn-premium">
                🚀 Passer Premium
              </a>
            }
            <a routerLink="/catalog" class="btn-back">← Retour au catalogue</a>
          </div>
        </div>
      }
    </div>
  `,
  styles: [`
    .detail-page { max-width: 1100px; margin: 0 auto; padding: 2rem; }
    .detail-hero {
      display: grid; grid-template-columns: 1fr 350px; gap: 3rem;
      align-items: start;
    }
    @media (max-width: 768px) { .detail-hero { grid-template-columns: 1fr; } }
    .category-tag { color: #6366f1; font-weight: 600; text-transform: uppercase; font-size: .8rem; }
    h1 { font-size: 2rem; font-weight: 800; margin: .5rem 0; line-height: 1.3; }
    .instructor { color: #6b7280; margin-bottom: 1rem; }
    .meta-row { display: flex; gap: .75rem; flex-wrap: wrap; margin-bottom: 1.5rem; }
    .badge { padding: .25rem .6rem; border-radius: 4px; font-size: .8rem; font-weight: 600; }
    .level-beginner     { background: #d1fae5; color: #065f46; }
    .level-intermediate { background: #fef3c7; color: #92400e; }
    .level-advanced     { background: #fee2e2; color: #991b1b; }
    .badge-premium { background: #fef3c7; color: #92400e; padding: .25rem .6rem; border-radius: 4px; font-size: .8rem; font-weight: 600; }
    .description { color: #374151; line-height: 1.7; font-size: 1rem; }
    .hero-card {
      background: #fff; border: 1px solid #e5e7eb; border-radius: 12px;
      padding: 1.5rem; position: sticky; top: 1rem;
      display: flex; flex-direction: column; gap: 1rem;
    }
    .price { font-size: 2rem; font-weight: 800; }
    .price-premium { color: #6366f1; font-weight: 700; }
    .btn-enroll, .btn-premium {
      display: block; padding: .85rem; border-radius: 8px; font-weight: 700;
      text-align: center; text-decoration: none; cursor: pointer; border: none; font-size: 1rem;
    }
    .btn-enroll { background: #6366f1; color: #fff; }
    .btn-premium { background: #f59e0b; color: #fff; }
    .btn-back { color: #6b7280; text-decoration: none; font-size: .9rem; text-align: center; }
    .spinner { text-align: center; padding: 3rem; color: #6b7280; }
  `]
})
export class CourseDetailComponent implements OnInit {

  @Input() id!: string;   // Liaison route param → @Input (Angular 17)

  private readonly courseService = inject(CourseService);
  readonly authService = inject(AuthService);

  readonly course  = signal<CourseDetail | null>(null);
  readonly loading = signal(true);

  ngOnInit(): void {
    this.courseService.getById(this.id).subscribe({
      next: data => {
        this.course.set(data);
        this.loading.set(false);
      },
      error: () => this.loading.set(false)
    });
  }
}
