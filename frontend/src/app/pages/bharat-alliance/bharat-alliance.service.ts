import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AllianceOrganization, Poc, PocDetail, RegisterPocData, AllianceKpi, QuarterlyStatus, AllianceReportOptions, KpiTemplate } from './bharat-alliance.interfaces';

@Injectable({ providedIn: 'root' })
export class BharatAllianceService {

  private api = '/api/bharat-alliance';

  constructor(private http: HttpClient) {}

  getOrganizationProfile(): Observable<AllianceOrganization> {
    return this.http.get<AllianceOrganization>(`${this.api}/organization`);
  }

  saveOrganizationProfile(org: Partial<AllianceOrganization>): Observable<AllianceOrganization> {
    return this.http.put<AllianceOrganization>(`${this.api}/organization`, org);
  }

  getMyPocs(): Observable<Poc[]> {
    return this.http.get<Poc[]>(`${this.api}/pocs`);
  }

  registerPoc(data: RegisterPocData): Observable<Poc> {
    return this.http.post<Poc>(`${this.api}/pocs`, data);
  }

  getPocDetail(pocId: number): Observable<PocDetail> {
    return this.http.get<PocDetail>(`${this.api}/pocs/${pocId}`);
  }

  linkSimulationToPoc(pocId: number, simulationId: number, trlEvidenceFor: number): Observable<void> {
    return this.http.post<void>(`${this.api}/pocs/${pocId}/simulations`, { simulationId, trlEvidenceFor });
  }

  updatePocTrl(pocId: number, newTrl: number, evidenceDescription: string): Observable<void> {
    return this.http.put<void>(`${this.api}/pocs/${pocId}/trl`, { newTrl, evidenceDescription });
  }

  updateKpiActualValue(kpiId: number, actualValue: number): Observable<void> {
    return this.http.put<void>(`${this.api}/kpis/${kpiId}/actual-value`, { actualValue });
  }

  getQuarterlyStatus(pocId: number): Observable<QuarterlyStatus[]> {
    return this.http.get<QuarterlyStatus[]>(`${this.api}/pocs/${pocId}/quarterly-status`);
  }

  submitQuarterlyStatus(pocId: number, quarter: string, year: number, status: string): Observable<void> {
    return this.http.put<void>(`${this.api}/pocs/${pocId}/quarterly-status`, { quarter, year, status });
  }

  generateReport(options: AllianceReportOptions): Observable<Blob> {
    return this.http.post(`${this.api}/report/generate`, options, { responseType: 'blob' });
  }

  getKpiTemplates(allianceTrack: string): Observable<KpiTemplate[]> {
    return this.http.get<KpiTemplate[]>(`${this.api}/kpi-templates/${allianceTrack}`);
  }
}
