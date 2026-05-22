import { HttpInterceptorFn } from '@angular/common/http';

/**
 * Intercepteur HTTP fonctionnel (Angular 17).
 * Ajoute le token JWT si disponible.
 */
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const token = localStorage.getItem('edumarket_token');

  if (token) {
    const authReq = req.clone({
      setHeaders: { Authorization: `Bearer ${token}` }
    });
    return next(authReq);
  }

  return next(req);
};
