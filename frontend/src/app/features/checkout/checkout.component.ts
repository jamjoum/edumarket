import { Component, OnInit, Input, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { PaymentService } from '../../core/services/payment.service';
import { AuthService } from '../../core/services/auth.service';
import { CourseService } from '../../core/services/course.service';
import { CourseDetail, PaymentMethod } from '../../core/models/models';

@Component({
  selector: 'app-checkout',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <div class="checkout-page">
      <h1>💳 Finaliser l'achat</h1>

      @if (course()) {
        <div class="checkout-layout">
          <div class="order-summary">
            <h2>Récapitulatif</h2>
            <div class="order-item">
              <span>{{ course()!.title }}</span>
              <strong>{{ course()!.price | currency:'EUR' }}</strong>
            </div>
            <div class="order-total">
              <span>Total</span>
              <strong>{{ course()!.price | currency:'EUR' }}</strong>
            </div>
          </div>

          <div class="payment-form">
            <h2>Mode de paiement</h2>
            <div class="methods">
              @for (m of methods; track m.id) {
                <label class="method-option" [class.active]="selectedMethod() === m.id">
                  <input type="radio" [value]="m.id" name="payment"
                         (change)="selectedMethod.set(m.id)"/>
                  {{ m.icon }} {{ m.label }}
                </label>
              }
            </div>

            <button class="btn-pay"
                    [disabled]="paymentService.processing()"
                    (click)="pay()">
              @if (paymentService.processing()) { ⏳ Traitement en cours… }
              @else { Payer {{ course()!.price | currency:'EUR' }} }
            </button>

            @if (paymentService.lastPayment()?.status === 'SUCCESS') {
              <div class="success">
                ✅ Paiement réussi ! Accédez à votre cours.
                <br/><small>Réf: {{ paymentService.lastPayment()?.providerRef }}</small>
              </div>
            }

            <a routerLink="/catalog" class="back-link">← Retour au catalogue</a>
          </div>
        </div>
      }
    </div>
  `,
  styles: [`
    .checkout-page { max-width: 800px; margin: 0 auto; padding: 2rem; }
    h1 { font-size: 2rem; font-weight: 800; margin-bottom: 2rem; }
    .checkout-layout { display: grid; grid-template-columns: 1fr 1fr; gap: 2rem; }
    @media (max-width: 640px) { .checkout-layout { grid-template-columns: 1fr; } }
    .order-summary, .payment-form {
      background: #f9fafb; border-radius: 12px; padding: 1.5rem;
    }
    h2 { font-size: 1.2rem; font-weight: 700; margin-bottom: 1rem; }
    .order-item, .order-total {
      display: flex; justify-content: space-between; padding: .75rem 0; border-bottom: 1px solid #e5e7eb;
    }
    .order-total { font-weight: 700; font-size: 1.1rem; border-bottom: none; padding-top: 1rem; }
    .methods { display: flex; flex-direction: column; gap: .75rem; margin-bottom: 1.5rem; }
    .method-option {
      display: flex; align-items: center; gap: .75rem; padding: .75rem;
      border: 2px solid #e5e7eb; border-radius: 8px; cursor: pointer;
    }
    .method-option.active { border-color: #6366f1; background: #eef2ff; }
    .method-option input { accent-color: #6366f1; }
    .btn-pay {
      width: 100%; padding: 1rem; background: #6366f1; color: #fff;
      border: none; border-radius: 10px; font-weight: 700; font-size: 1rem; cursor: pointer;
    }
    .btn-pay:disabled { opacity: .6; cursor: not-allowed; }
    .success {
      margin-top: 1rem; background: #d1fae5; color: #065f46;
      padding: 1rem; border-radius: 8px; text-align: center;
    }
    .back-link { display: block; text-align: center; color: #6b7280; margin-top: 1rem; }
  `]
})
export class CheckoutComponent implements OnInit {
  @Input() courseId!: string;

  readonly paymentService = inject(PaymentService);
  readonly authService    = inject(AuthService);
  readonly courseService  = inject(CourseService);

  readonly course         = signal<CourseDetail | null>(null);
  readonly selectedMethod = signal<PaymentMethod>('CREDIT_CARD');

  readonly methods = [
    { id: 'CREDIT_CARD' as PaymentMethod, label: 'Carte bancaire', icon: '💳' },
    { id: 'PAYPAL'      as PaymentMethod, label: 'PayPal',         icon: '🅿️' },
    { id: 'CRYPTO'      as PaymentMethod, label: 'Crypto (USDC)',   icon: '🔗' }
  ];

  ngOnInit(): void {
    this.courseService.getById(this.courseId).subscribe(c => this.course.set(c));
  }

  pay(): void {
    const user   = this.authService.currentUser();
    const course = this.course();
    if (!user || !course) return;

    this.paymentService.payForCourse({
      userId:        user.id,
      courseId:      course.id,
      paymentMethod: this.selectedMethod()
    }).subscribe();
  }
}
