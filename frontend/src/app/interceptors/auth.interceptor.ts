import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';

/**
 * Functional HttpInterceptor introduced in Angular 15+.
 * This literally catches every single outbound asynchronous HTTP Request universally.
 * If we possess a token, it structurally binds it onto an 'Authorization' Header implicitly.
 */
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const token = authService.getToken();

  // If a JWT token exists in localStorage, clone the request to forcefully add the strict header
  if (token) {
    const authReq = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
    // Send the securely wrapped request out towards Java backend
    return next(authReq);
  }

  // If no token exists (e.g. login/register request itself), natively pass through as is
  return next(req);
};
