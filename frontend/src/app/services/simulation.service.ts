import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { SimulationResult, SimulationHistoryItem, SimulationRequest, BeamPatternRequest, BeamPatternResult } from '../models/simulation-result.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class SimulationService {
  
  private demoUrl = `${environment.apiUrl}/api/simulate/demo`;
  private simulateUrl = `${environment.apiUrl}/api/simulate`;
  private beamPatternUrl = `${environment.apiUrl}/api/simulations/beam-pattern`;
  private historyUrl = `${environment.apiUrl}/api/simulations`;

  constructor(private http: HttpClient) { }

  /**
   * Fetches the demonstration simulation data from the backend.
   */
  getDemoSimulation(): Observable<SimulationResult> {
    return this.http.get<SimulationResult>(this.demoUrl);
  }

  /**
   * Explicitly triggers a new custom simulation via POST.
   */
  runNewSimulation(request: SimulationRequest): Observable<SimulationResult> {
    return this.http.post<SimulationResult>(this.simulateUrl, request);
  }

  /**
   * Triggers a new beam pattern simulation.
   */
  runBeamPattern(request: BeamPatternRequest): Observable<BeamPatternResult> {
    return this.http.post<BeamPatternResult>(this.beamPatternUrl, request);
  }

  /**
   * Retrieves the full history of all past simulations saved to PostgreSQL.
   */
  getAllSimulations(): Observable<SimulationHistoryItem[]> {
    return this.http.get<SimulationHistoryItem[]>(this.historyUrl);
  }

  /**
   * Fetches specifically identifying generated dynamic URL parameters.
   */
  getShareLink(simulationId: number): Observable<{ shareUrl: string, shareToken: string }> {
    return this.http.get<{ shareUrl: string, shareToken: string }>(`${environment.apiUrl}/api/simulations/${simulationId}/share-link`);
  }

  /**
   * Queries identical DTO payload strictly publicly.
   */
  getSimulationByShareToken(token: string): Observable<SimulationResult> {
    return this.http.get<SimulationResult>(`${environment.apiUrl}/api/share/${token}`);
  }
}
