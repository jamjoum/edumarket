import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PaymentService } from '../../core/services/payment.service';
import { AuthService } from '../../core/services/auth.service';
import { PaymentMethod, SubscriptionPlan, SubscriptionPricing } from '../../core/models/models';

@Component({
  selector: 'app-subscription',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="sub-page">
      <header class="sub-header">
        <h1>🚀 Passez Premium</h1>
        <p>Accédez à tout le catalogue EduMarket en illimité</p>
      </header>

      <!-- Plans -->
      <div class="plans-grid">
        @for (plan of plans; track plan.plan) {
          <div class="plan-card" [class.highlighted]="plan.highlight"
               [class.selected]="selectedPlan() === plan.plan"
               (click)="selectPlan(plan.plan)">
            @if (plan.highlight) { <div class="popular-badge">⭐ Populaire</div> }
            <h2>{{ plan.label }}</h2>
            <div class="plan-price">
              <span class="amount">{{ plan.price | currency:'EUR' }}</span>
              <span class="per">/{{ plan.plan === 'MONTHLY' ? 'mois' : 'an' }}</span>
            </div>
            <p class="plan-desc">{{ plan.description }}</p>
            <div class="check-indicator">
              @if (selectedPlan() === plan.plan) { ✅ Sélectionné }
            </div>
          </div>
        }
      </div>

      <!-- Méthode de paiement -->
      <div class="payment-section">
        <h3>Mode de paiement</h3>
        <div class="payment-methods">
          @for (method of methods; track method.id) {
            <button class="method-btn"
                    [class.active]="selectedMethod() === method.id"
                    (click)="selectedMethod.set(method.id)">
              {{ method.icon }} {{ method.label }}
            </button>
          }
        </div>

        <button class="btn-subscribe"
                [disabled]="paymentService.processing()"
                (click)="subscribe()">
          @if (paymentService.processing()) { ⏳ Traitement… }
          @else { Souscrire maintenant }
        </button>

        @if (paymentService.lastPayment()?.status === 'SUCCESS') {
          <div class="success-msg">
            ✅ Abonnement activé ! Profitez de vos cours premium.
          </div>
        }
      </div>
    </div>
  `,
  styles: [`
    .sub-page { max-width: 900px; margin: 0 auto; padding: 2rem; }
    .sub-header { text-align: center; margin-bottom: 3rem; }
    .sub-header h1 { font-size: 2.5rem; font-weight: 800; }
    .sub-header p { color: #6b7280; font-size: 1.1rem; }

    .plans-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(240px,1fr)); gap: 1.5rem; margin-bottom: 3rem; }

    .plan-card {
      border: 2px solid #e5e7eb; border-radius: 16px; padding: 2rem;
      cursor: pointer; position: relative; transition: all .2s;
    }
    .plan-card:hover { border-color: #6366f1; }
    .plan-card.selected { border-color: #6366f1; background: #eef2ff; }
    .plan-card.highlighted { border-color: #f59e0b; }
    .popular-badge {
      position: absolute; top: -12px; left: 50%; transform: translateX(-50%);
      background: #f59e0b; color: #fff; padding: .2rem .8rem; border-radius: 20px; font-size: .8rem; font-weight: 700;
    }
    .plan-card h2 { font-size: 1.3rem; font-weight: 700; margin-bottom: .5rem; }
    .plan-price { margin: 1rem 0; }
    .amount { font-size: 2.5rem; font-weight: 800; color: #111; }
    .per { color: #6b7280; font-size: 1rem; }
    .plan-desc { color: #6b7280; font-size: .9rem; }
    .check-indicator { margin-top: 1rem; color: #6366f1; font-weight: 600; min-height: 1.5rem; }

    .payment-section { background: #f9fafb; border-radius: 12px; padding: 2rem; }
    .payment-section h3 { font-size: 1.1rem; font-weight: 700; margin-bottom: 1rem; }
    .payment-methods { display: flex; gap: 1rem; flex-wrap: wrap; margin-bottom: 1.5rem; }
    .method-btn {
      padding: .6rem 1.2rem; border: 2px solid #ddd; border-radius: 8px;
      background: #fff; cursor: pointer; font-size: .95rem; transition: all .2s;
    }
    .method-btn.active { border-color: #6366f1; background: #eef2ff; font-weight: 600; }
    .btn-subscribe {
      width: 100%; padding: 1rem; background: #6366f1; color: #fff;
      border: none; border-radius: 10px; font-size: 1.1rem; font-weight: 700; cursor: pointer;
    }
    .btn-subscribe:disabled { opacity: .6; cursor: not-allowed; }
    .success-msg { margin-top: 1rem; background: #d1fae5; color: #065f46; padding: 1rem; border-radius: 8px; text-align: center; font-weight: 600; }
  `]
})
export class SubscriptionComponent {

  readonly paymentService = inject(PaymentService);
  readonly authService    = inject(AuthService);

  readonly selectedPlan   = signal<SubscriptionPlan>('YEARLY');
  readonly selectedMethod = signal<PaymentMethod>('CREDIT_CARD');

  readonly plans: SubscriptionPricing[] = [
    { plan: 'MONTHLY', price: 19.99, label: 'Mensuel', description: 'Idéal pour tester', highlight: false },
    { plan: 'YEARLY',  price: 149.99, label: 'Annuel', description: 'Économisez 40% · 12.49€/mois', highlight: true },
    { plan: 'TRIAL',   price: 0, label: 'Essai 14j', description: 'Gratuit, sans CB', highlight: false }
  ];

  readonly methods = [
    { id: 'CREDIT_CARD' as PaymentMethod, label: 'Carte bancaire', icon: '💳' },
    { id: 'PAYPAL'      as PaymentMethod, label: 'PayPal',         icon: '🅿️' },
    { id: 'CRYPTO'      as PaymentMethod, label: 'Crypto',         icon: '🔗' }
  ];

  selectPlan(plan: SubscriptionPlan): void {
    this.selectedPlan.set(plan);
  }

  subscribe(): void {
    const user = this.authService.currentUser();
    if (!user) return;

    this.paymentService.payForSubscription({
      userId:        user.id,
      paymentMethod: this.selectedMethod(),
      planType:      this.selectedPlan()
    }).subscribe();
  }
}
