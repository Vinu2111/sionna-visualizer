import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ParsedSimulationParams, ParseHistory } from './nl-simulation.interfaces';

@Injectable({ providedIn: 'root' })
export class NlSimulationService {

  private api = '/api/nl-simulation';

  constructor(private http: HttpClient) {}

  // Send user's plain English query to backend, which calls Claude AI and returns structured params
  parseNaturalLanguage(query: string): Observable<ParsedSimulationParams> {
    return this.http.post<ParsedSimulationParams>(`${this.api}/parse`, { query });
  }

  // Fetch the last 10 parse records for the history panel
  getParseHistory(): Observable<ParseHistory[]> {
    return this.http.get<ParseHistory[]>(`${this.api}/history`);
  }
}
