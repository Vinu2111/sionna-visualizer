import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { TtdfProject, Milestone, CreateMilestoneData, ReportOptions } from './ttdf.interfaces';

@Injectable({
  providedIn: 'root'
})
export class TtdfService {

  private apiUrl = '/api/ttdf';

  constructor(private http: HttpClient) {}

  getProject(): Observable<TtdfProject> {
      return this.http.get<TtdfProject>(`${this.apiUrl}/project`);
  }

  saveProject(project: Partial<TtdfProject>): Observable<TtdfProject> {
      return this.http.put<TtdfProject>(`${this.apiUrl}/project`, project);
  }

  getMilestones(): Observable<Milestone[]> {
      return this.http.get<Milestone[]>(`${this.apiUrl}/milestones`);
  }

  addMilestone(milestone: CreateMilestoneData): Observable<Milestone> {
      return this.http.post<Milestone>(`${this.apiUrl}/milestones`, milestone);
  }

  updateMilestoneStatus(milestoneId: number, status: string): Observable<void> {
      return this.http.put<void>(`${this.apiUrl}/milestones/${milestoneId}/status`, { status });
  }

  linkSimulationToMilestone(milestoneId: number, simulationId: number): Observable<void> {
      return this.http.post<void>(`${this.apiUrl}/milestones/${milestoneId}/link-simulation`, { simulationId });
  }

  updateKpiActualValue(kpiId: number, actualValue: number): Observable<void> {
      return this.http.put<void>(`${this.apiUrl}/kpis/${kpiId}/actual-value`, { actualValue });
  }

  updateTrl(trlLevel: number): Observable<void> {
      return this.http.put<void>(`${this.apiUrl}/project/trl`, { trlLevel });
  }

  generateReport(options: ReportOptions): Observable<Blob> {
      return this.http.post(`${this.apiUrl}/report/generate`, options, { responseType: 'blob' });
  }
}
