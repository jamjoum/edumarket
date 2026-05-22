import { Component } from '@angular/core';
import { RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive, CommonModule],
  template: `
    <nav class="navbar">
      <a routerLink="/" class="brand">
        📚 <strong>EduMarket</strong>
      </a>
      <div class="nav-links">
        <a routerLink="/catalog"      routerLinkActive="active">Catalogue</a>
        <a routerLink="/subscription" routerLinkActive="active">Premium</a>
      </div>
    </nav>
    <main>
      <router-outlet/>
    </main>
    <footer class="footer">
      <p>EduMarket © 2024 – Architecture Spring Boot · Angular 17 · Docker</p>
    </footer>
  `,
  styles: [`
    :host { display: flex; flex-direction: column; min-height: 100vh; }
    main { flex: 1; }

    .navbar {
      display: flex; align-items: center; justify-content: space-between;
      padding: 1rem 2rem; background: #fff; border-bottom: 1px solid #e5e7eb;
      position: sticky; top: 0; z-index: 100; box-shadow: 0 1px 4px rgba(0,0,0,.06);
    }
    .brand { text-decoration: none; color: #111; font-size: 1.3rem; }
    .nav-links { display: flex; gap: 1.5rem; }
    .nav-links a { text-decoration: none; color: #374151; font-weight: 500; padding: .4rem .8rem; border-radius: 6px; }
    .nav-links a.active { background: #eef2ff; color: #6366f1; }
    .nav-links a:hover { color: #6366f1; }

    .footer {
      text-align: center; padding: 1.5rem; background: #f9fafb;
      border-top: 1px solid #e5e7eb; color: #9ca3af; font-size: .85rem;
    }
  `]
})
export class AppComponent {}
