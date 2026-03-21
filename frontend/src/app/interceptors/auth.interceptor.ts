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
      }
      return throwError(() => error);
    })
  );
};
