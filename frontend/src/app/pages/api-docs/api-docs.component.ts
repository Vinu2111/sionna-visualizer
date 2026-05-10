import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-api-docs',
  standalone: true,
  imports: [
    CommonModule, 
    MatCardModule, 
    MatButtonModule, 
    MatIconModule, 
    MatInputModule, 
    MatFormFieldModule, 
    MatSnackBarModule, 
    FormsModule, 
    RouterModule
  ],
  templateUrl: './api-docs.component.html',
  styleUrl: './api-docs.component.scss'
})
export class ApiDocsComponent implements OnInit {
  apiKeys: any[] = [];
  newKeyDescription = '';
  isRevoking = false;

  constructor(
    public authService: AuthService,
    private http: HttpClient,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit() {
    if (this.authService.isLoggedIn()) {
      this.loadApiKeys();
    }
  }

  loadApiKeys() {
    this.http.get<any[]>(`${environment.apiUrl}/api/keys/my-keys`).subscribe({
      next: (keys) => this.apiKeys = keys,
      error: (err) => console.error('Error loading API keys', err)
    });
  }

  generateApiKey() {
    if (!this.newKeyDescription) return;
    this.http.post<any>(`${environment.apiUrl}/api/keys/generate`, { description: this.newKeyDescription }).subscribe({
      next: (res) => {
        this.snackBar.open('Generated new API key successfully.', 'Close', { duration: 3000 });
        this.newKeyDescription = '';
        this.loadApiKeys();
      },
      error: (err) => console.error('Error generating API key', err)
    });
  }

  revokeApiKey(keyValue: string) {
    this.isRevoking = true;
    this.http.delete(`${environment.apiUrl}/api/keys/${keyValue}`).subscribe({
      next: () => {
        this.snackBar.open('API key revoked.', 'Close', { duration: 3000 });
        this.loadApiKeys();
        this.isRevoking = false;
      },
      error: (err) => {
        console.error('Error revoking API key', err);
        this.isRevoking = false;
      }
    });
  }
}

