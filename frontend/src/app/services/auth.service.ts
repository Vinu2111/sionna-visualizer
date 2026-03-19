import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private authUrl = `${environment.apiUrl}/api/auth`;
  private TOKEN_KEY = 'auth-token';

  constructor(private http: HttpClient) { }

  /**
   * Submit raw username and password credentials.
   * If strictly cleared by Java mapping to PostgreSQL securely, saves the JWT to localStorage.
   */
  login(username: string, password: string): Observable<any> {
    return this.http.post<any>(`${this.authUrl}/login`, { username, password }).pipe(
      tap(response => {
        if (response.token) {
          localStorage.setItem(this.TOKEN_KEY, response.token);
        }
      })
    );
  }

  /**
   * Submits a fresh new user securely to the Database mapping entity layer.
   */
  register(username: string, password: string): Observable<any> {
    return this.http.post<any>(`${this.authUrl}/register`, { username, password });
  }

  /**
   * Discards the securely stored local JWT.
   */
  logout(): void {
    localStorage.removeItem(this.TOKEN_KEY);
  }

  /**
   * Evaluates natively whether we currently hold an authorization pass.
   */
  isLoggedIn(): boolean {
    return !!this.getToken();
  }

  /**
   * Retrieves the JSON Web Token actively protecting endpoints.
   */
  getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }
}
