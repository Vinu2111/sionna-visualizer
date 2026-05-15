import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatIconModule } from '@angular/material/icon';
import { trigger, transition, style, animate } from '@angular/animations';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatInputModule,
    MatButtonModule,
    MatFormFieldModule,
    MatProgressSpinnerModule,
    MatIconModule
  ],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss',
  animations: [
    // Slide the page in from the right on load
    trigger('slideIn', [
      transition(':enter', [
        style({ opacity: 0, transform: 'translateX(20px)' }),
        animate('400ms ease-out', style({ opacity: 1, transform: 'translateX(0)' }))
      ])
    ]),
    // Fade alert messages in smoothly
    trigger('fadeAlert', [
      transition(':enter', [
        style({ opacity: 0, transform: 'translateY(-6px)' }),
        animate('250ms ease-out', style({ opacity: 1, transform: 'translateY(0)' }))
      ]),
      transition(':leave', [
        animate('200ms ease-in', style({ opacity: 0 }))
      ])
    ]),
    // Slide form content on tab switch
    trigger('formSlide', [
      transition(':enter', [
        style({ opacity: 0, transform: 'translateX(16px)' }),
        animate('300ms ease-out', style({ opacity: 1, transform: 'translateX(0)' }))
      ])
    ])
  ]
})
export class LoginComponent {
  loginForm: FormGroup;
  registerForm: FormGroup;
  isRegistering = false;
  isLoading = false;
  errorMessage = '';
  successMessage = '';
  showPassword = false;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    // Sign In form — username + password
    this.loginForm = this.fb.group({
      username: ['', [Validators.required, Validators.minLength(3)]],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });

    // Create Account form — username + email + password + confirm
    this.registerForm = this.fb.group({
      username: ['', [Validators.required, Validators.minLength(3)]],
      email: ['', [Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      confirmPassword: ['', [Validators.required]]
    }, { validators: this.passwordMatchValidator });
  }

  /** Cross-field validator: password and confirmPassword must match */
  private passwordMatchValidator(group: FormGroup): { [key: string]: boolean } | null {
    const pw = group.get('password')?.value;
    const cpw = group.get('confirmPassword')?.value;
    if (pw && cpw && pw !== cpw) {
      return { passwordMismatch: true };
    }
    return null;
  }

  /** Switch between Sign In and Create Account tabs */
  setMode(registering: boolean): void {
    this.isRegistering = registering;
    this.errorMessage = '';
    this.successMessage = '';
    this.showPassword = false;
  }

  /** Legacy toggle for backwards compatibility */
  toggleMode(): void {
    this.setMode(!this.isRegistering);
  }

  /** Submit the active form */
  onSubmit(): void {
    const activeForm = this.isRegistering ? this.registerForm : this.loginForm;
    if (activeForm.invalid) return;

    this.isLoading = true;
    this.errorMessage = '';
    this.successMessage = '';

    const username = activeForm.value.username;
    const password = activeForm.value.password;

    if (this.isRegistering) {
      this.authService.register(username, password).subscribe({
        next: () => {
          this.successMessage = 'Account created! Signing you in...';
          this.isRegistering = false;
          this.loginForm.reset();
          this.registerForm.reset();
          this.isLoading = false;
        },
        error: (err) => {
          // FIX 2: Friendly error messages instead of confusing defaults
          const serverMsg = err.error?.message?.toLowerCase() || '';
          if (serverMsg.includes('username') && serverMsg.includes('taken')) {
            this.errorMessage = 'This username is already taken. Try another.';
          } else if (serverMsg.includes('exists') || serverMsg.includes('duplicate')) {
            this.errorMessage = 'This username is already taken. Try another.';
          } else if (err.status === 0) {
            this.errorMessage = 'Connection failed. Please check your internet.';
          } else {
            this.errorMessage = err.error?.message || 'Registration failed. Please try again.';
          }
          this.isLoading = false;
        }
      });
    } else {
      this.authService.login(username, password).subscribe({
        next: (res) => {
          if (res.token) {
            this.router.navigate(['/']);
          }
          this.isLoading = false;
        },
        error: (err) => {
          // FIX 2: Friendly login error messages
          if (err.status === 401) {
            this.errorMessage = 'Incorrect password. Please try again.';
          } else if (err.status === 404) {
            this.errorMessage = 'No account found with this username.';
          } else if (err.status === 0) {
            this.errorMessage = 'Connection failed. Please check your internet.';
          } else {
            this.errorMessage = 'Login failed. Please verify your credentials.';
          }
          this.isLoading = false;
        }
      });
    }
  }

  // ─── Password Strength Indicator ─────────────────────────────────────────
  get passwordStrengthPercent(): number {
    const pw = this.registerForm.get('password')?.value || '';
    if (pw.length < 6) return 20;
    let score = 0;
    if (pw.length >= 6) score += 25;
    if (pw.length >= 10) score += 25;
    if (/[A-Z]/.test(pw)) score += 15;
    if (/[0-9]/.test(pw)) score += 15;
    if (/[^A-Za-z0-9]/.test(pw)) score += 20;
    return Math.min(100, score);
  }

  get passwordStrengthColor(): string {
    const p = this.passwordStrengthPercent;
    if (p <= 35) return '#f43f5e';     // Weak — red
    if (p <= 65) return '#f59e0b';     // Medium — yellow
    return '#10b981';                   // Strong — green
  }

  get passwordStrengthLabel(): string {
    const p = this.passwordStrengthPercent;
    if (p <= 35) return 'Weak';
    if (p <= 65) return 'Medium';
    return 'Strong';
  }
}
