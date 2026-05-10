import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AnomalyReport, AiExplanation } from './anomaly.interfaces';

@Injectable({ providedIn: 'root' })
export class AnomalyDetectionService {

  private api = '/api/anomaly';

  constructor(private http: HttpClient) {}

  // Run a fresh physics analysis on the simulation's BER curve via the backend engine
  analyzeSimulation(simulationId: number): Observable<AnomalyReport> {
    return this.http.post<AnomalyReport>(`${this.api}/analyze/${simulationId}`, {});
  }

  // Fetch a previously saved report without re-running checks (avoids Claude API cost)
  getSavedReport(simulationId: number): Observable<AnomalyReport> {
    return this.http.get<AnomalyReport>(`${this.api}/report/${simulationId}`);
  }

  // Call Claude AI to get a plain-English explanation of one specific anomaly
  getAnomalyExplanation(anomalyId: number): Observable<AiExplanation> {
    return this.http.post<AiExplanation>(`${this.api}/${anomalyId}/explain`, {});
  }
}
