import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';

/**
 * Ensures strict barrier protection natively against unauthenticated URL tampering natively.
 * It immediately redirects natively to the strict login entrypoint if no identity is proven.
 */
export const authGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (authService.isLoggedIn()) {
    return true; // Universally allow navigation structurally!
  }

  // Intercept the rogue navigation attempt efficiently and natively force login constraints
  return router.parseUrl('/login');
};
