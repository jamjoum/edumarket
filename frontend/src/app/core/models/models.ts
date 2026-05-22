// ─────────────────────────────────────────────────────────────────────────────
// Models – interfaces TypeScript reflétant les DTOs du backend
// ─────────────────────────────────────────────────────────────────────────────

export type CourseLevel = 'BEGINNER' | 'INTERMEDIATE' | 'ADVANCED';

export interface CourseSummary {
  id: string;
  title: string;
  slug: string;
  instructorName: string;
  categoryName: string;
  price: number;
  level: CourseLevel;
  premium: boolean;
  thumbnailUrl?: string;
}

export interface CourseDetail {
  id: string;
  title: string;
  slug: string;
  description: string;
  instructorName: string;
  categoryName: string;
  price: number;
  durationHours: number;
  level: CourseLevel;
  language: string;
  thumbnailUrl?: string;
  premium: boolean;
  createdAt: string;
}

export interface Category {
  id: number;
  name: string;
  slug: string;
  description?: string;
  icon?: string;
}

export type PaymentMethod = 'CREDIT_CARD' | 'PAYPAL' | 'CRYPTO';
export type SubscriptionPlan = 'MONTHLY' | 'YEARLY' | 'TRIAL';

export interface PaymentRequest {
  userId: string;
  courseId?: string;
  paymentMethod: PaymentMethod;
  planType?: SubscriptionPlan;
}

export interface PaymentResponse {
  id: string;
  amount: number;
  currency: string;
  paymentMethod: PaymentMethod;
  status: 'PENDING' | 'SUCCESS' | 'FAILED' | 'REFUNDED';
  providerRef?: string;
  createdAt: string;
}

export interface User {
  id: string;
  email: string;
  fullName: string;
  premium: boolean;
  role: 'STUDENT' | 'INSTRUCTOR' | 'ADMIN';
}

export interface SubscriptionPricing {
  plan: SubscriptionPlan;
  price: number;
  label: string;
  description: string;
  highlight?: boolean;
}
