import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Experiment, SimulationHistoryItem, TagCount, SimulationFilters, CreateExperimentData } from './experiment.interfaces';

@Injectable({
  providedIn: 'root'
})
export class ExperimentService {
  private apiUrl = '/api';

  constructor(private http: HttpClient) {}

  getExperiments(): Observable<Experiment[]> {
    return this.http.get<Experiment[]>(`${this.apiUrl}/experiments`);
  }

  createExperiment(data: CreateExperimentData): Observable<Experiment> {
    return this.http.post<Experiment>(`${this.apiUrl}/experiments`, data);
  }

  searchSimulations(query: string, filters: SimulationFilters): Observable<SimulationHistoryItem[]> {
    let params = new HttpParams();
    if (query) params = params.set('q', query);
    if (filters.experimentId) params = params.set('experimentId', filters.experimentId.toString());
    if (filters.tags && filters.tags.length > 0) params = params.set('tags', filters.tags.join(','));
    if (filters.starred) params = params.set('starred', 'true');

    return this.http.get<SimulationHistoryItem[]>(`${this.apiUrl}/simulations/search`, { params });
  }

  addTagToSimulation(simulationId: number, tag: string): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/simulations/${simulationId}/tags`, { tag });
  }

  removeTagFromSimulation(simulationId: number, tag: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/simulations/${simulationId}/tags/${tag}`);
  }

  bulkAddTags(simulationIds: number[], tags: string[]): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/simulations/bulk-tag`, { simulationIds, tags });
  }

  // Rapidly binds free text structurally isolating UI block latency natively automatically saving behind scenes  
  updateSimulationNote(simulationId: number, note: string): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/simulations/${simulationId}/note`, { note });
  }

  assignToExperiment(simulationId: number, experimentId: number): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/simulations/${simulationId}/experiment`, { experimentId });
  }

  toggleStar(simulationId: number): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/simulations/${simulationId}/star`, {});
  }

  getAllTags(): Observable<TagCount[]> {
    return this.http.get<TagCount[]>(`${this.apiUrl}/simulations/tags`);
  }
}
