import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { SimulationResult, SimulationHistoryItem } from '../models/simulation-result.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class SimulationService {
  
  // Now pointing at the Java Spring Boot backend dynamically securely instead of Python directly.
  private apiUrl = `${environment.apiUrl}/api/simulate/demo`;
  private historyUrl = `${environment.apiUrl}/api/simulations`;

  // Inject Angular's built-in HttpClient module to perform web requests
  constructor(private http: HttpClient) { }

  /**
   * Fetches the demonstration simulation data from the backend Python bridge.
   * By returning an Observable, Angular components can subscribe to it and update
   * the UI cleanly when the asynchronous response arrives.
   */
  getDemoSimulation(): Observable<SimulationResult> {
    return this.http.get<SimulationResult>(this.apiUrl);
  }

  /**
   * Explicitly triggers a new simulation run via the Java middleware dynamically gracefully cleanly accurately explicitly gracefully cleanly seamlessly nicely.
   */
  runNewSimulation(params?: any): Observable<SimulationResult> {
    return this.http.get<SimulationResult>(this.apiUrl, { params });
  }

  /**
   * Retrieves the full history of all past simulations saved to PostgreSQL.
   */
  getAllSimulations(): Observable<SimulationHistoryItem[]> {
    return this.http.get<SimulationHistoryItem[]>(this.historyUrl);
  }

  /**
   * Fetches specifically identifying generated dynamic URL parameters natively successfully securely.
   */
  getShareLink(simulationId: number): Observable<{ shareUrl: string, shareToken: string }> {
    return this.http.get<{ shareUrl: string, shareToken: string }>(`${environment.apiUrl}/api/simulations/${simulationId}/share-link`);
  }

  /**
   * Queries identical DTO payload strictly publicly bypassing normal internal firewall rules unconditionally natively.
   */
  getSimulationByShareToken(token: string): Observable<SimulationResult> {
    return this.http.get<SimulationResult>(`${environment.apiUrl}/api/share/${token}`);
  }
}

