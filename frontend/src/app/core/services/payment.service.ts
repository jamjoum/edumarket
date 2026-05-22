import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { PaymentRequest, PaymentResponse } from '../models/models';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class PaymentService {

  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/payments`;

  /** Dernier paiement effectué */
  readonly lastPayment = signal<PaymentResponse | null>(null);
  readonly processing  = signal<boolean>(false);

  payForCourse(request: PaymentRequest): Observable<PaymentResponse> {
    this.processing.set(true);
    return this.http.post<PaymentResponse>(`${this.baseUrl}/course`, request).pipe(
      tap(res => {
        this.lastPayment.set(res);
        this.processing.set(false);
      })
    );
  }

  payForSubscription(request: PaymentRequest): Observable<PaymentResponse> {
    this.processing.set(true);
    return this.http.post<PaymentResponse>(`${this.baseUrl}/subscription`, request).pipe(
      tap(res => {
        this.lastPayment.set(res);
        this.processing.set(false);
      })
    );
  }
}
