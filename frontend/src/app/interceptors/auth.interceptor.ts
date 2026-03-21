import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';
import { Router } from '@angular/router';
import { catchError } from 'rxjs/operators';
import { throwError } from 'rxjs';
import { MatSnackBar } from '@angular/material/snack-bar';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const router = inject(Router);
  const snackBar = inject(MatSnackBar);
  const token = authService.getToken();

  let authReq = req;
  // If a JWT token exists in localStorage, clone the request to forcefully add the strict header
  if (token) {
    authReq = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
  }

  // Send the securely wrapped request out towards Java backend
  // And intercept 401/403 errors globally to forcefully redirect to login.
  return next(authReq).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status === 401 || error.status === 403) {
        if (token && req.url.includes('/api/')) {
          authService.logout();
          snackBar.open("Your session expired. Please log in again.", "Dismiss", {
            duration: 5000,
            panelClass: ['error-snackbar'],
            verticalPosition: 'bottom',
            horizontalPosition: 'center'
          });
          router.navigate(['/login']);
        }
      } else if (error.status === 429) {
        snackBar.open("Too many simulations. Please wait 1 minute.", "Close", { duration: 5000 });
      } else if (error.status === 503) {
        snackBar.open("Simulation engine is temporarily unavailable. Please try again in 30 seconds.", "Close", { duration: 5000 });
      } else if (error.status === 408) {
        snackBar.open("Simulation timed out. Try fewer SNR points.", "Close", { duration: 5000 });
      } else if (error.status === 400) {
        let errorMsg = "Invalid input.";
        if (error.error && typeof error.error === 'object') {
          // Format dictionary of errors returned by backend @Valid into a single string
          errorMsg = Object.entries(error.error)
            .map(([field, msg]) => `${field}: ${msg}`)
            .join(' | ');
        }
        snackBar.open(errorMsg, "Close", { duration: 7000 });
      }
      return throwError(() => error);
    })
  );
};
