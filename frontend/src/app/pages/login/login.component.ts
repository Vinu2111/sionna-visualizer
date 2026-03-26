import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatInputModule,
    MatButtonModule,
    MatFormFieldModule,
    MatProgressSpinnerModule
  ],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {
  loginForm: FormGroup;
  isRegistering = false;
  isLoading = false;
  errorMessage = '';
  successMessage = '';

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    // Both Login and Register use identical payload fields natively.
    this.loginForm = this.fb.group({
      username: ['', [Validators.required, Validators.minLength(3)]],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });
  }

  /**
   * Invoked seamlessly specifically when flipping the lower "Register" vs "Login" modes visually.
   */
  toggleMode(): void {
    this.isRegistering = !this.isRegistering;
    // Clear out residual warnings
    this.errorMessage = '';
    this.successMessage = '';
  }

  /**
   * Consolidates authentication flow parsing exclusively utilizing Java JWT parameters dynamically.
   */
  onSubmit(): void {
    if (this.loginForm.invalid) {
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';
    this.successMessage = '';

    const { username, password } = this.loginForm.value;

    if (this.isRegistering) {
      // Flow 1: Register entirely dynamically
      this.authService.register(username, password).subscribe({
        next: (res) => {
          this.successMessage = 'Registration successful! You may now firmly Login.';
          this.isRegistering = false; // flip dynamically backing
          this.loginForm.reset();
          this.isLoading = false;
        },
        error: (err) => {
          this.errorMessage = err.error?.message || 'Firm registration failed. Ensure database matches.';
          this.isLoading = false;
        }
      });
    } else {
      // Flow 2: Authenticate exclusively providing stored BCrypt validations!
      this.authService.login(username, password).subscribe({
        next: (res) => {
          if (res.token) {
            this.router.navigate(['/']); // Success strictly maps home
          }
          this.isLoading = false;
        },
        error: (err) => {
          this.errorMessage = 'Login failed. Verify your username and password.';
          this.isLoading = false;
        }
      });
    }
  }
}
