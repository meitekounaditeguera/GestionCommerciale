import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';

import { AuthService } from '../services/auth.service';

// URL de base de l'API : seules ces requêtes reçoivent le token JWT.
const API_BASE_URL = 'http://localhost:8080';

// Attache automatiquement "Authorization: Bearer <token>" à toutes les
// requêtes envoyées vers l'API backend, sans avoir à le faire dans chaque service.
export const jwtInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const token = authService.getToken();

  if (!token || !req.url.startsWith(API_BASE_URL)) {
    return next(req);
  }

  const requeteAuthentifiee = req.clone({
    setHeaders: { Authorization: `Bearer ${token}` }
  });

  return next(requeteAuthentifiee);
};
