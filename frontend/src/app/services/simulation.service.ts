import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { SimulationResult, SimulationHistoryItem, SimulationRequest, BeamPatternRequest, BeamPatternResult, ModulationComparisonRequest, ModulationComparisonResult, PathLossRequest, PathLossResult, SimulationEstimateRequest, SimulationEstimateResult, ColormapOption, RayDirectionRequest, RayDirectionResult, UeTrajectoryRequest, UeTrajectoryResult } from '../models/simulation-result.model';
import { environment } from '../../environments/environment';

export interface ComparisonMetadata {
  same_type: boolean;
  type1: string;
  type2: string;
  can_overlay: boolean;
}

export interface SimulationResultDto {
  id: number;
  simulationType: string;
  modulationType: string;
  codeRate: number;
  snrMin: number;
  snrMax: number;
  snrDb: string;
  berTheoretical: string;
  berSimulated: string;
  beamAngles: string;
  beamPatternDb: string;
  steeringAngle: number;
  numAntennas: number;
  frequencyGhz: number;
  mainLobeWidth: number;
  sideLobeLevel: number;
  bpskBer: string;
  qpskBer: string;
  qam16Ber: string;
  qam64Ber: string;
  crossoverPoints: string;
  hardwareUsed: string;
  simulationTimeMs: number;
  shareToken: string;
  isPublic: boolean;
  createdAt: string;
  colormapUsed?: string;
}

export interface ComparisonResponse {
  simulation1: SimulationResultDto;
  simulation2: SimulationResultDto;
  comparison_metadata: ComparisonMetadata;
}

@Injectable({
  providedIn: 'root'
})
export class SimulationService {
  private demoUrl = `${environment.apiUrl}/api/simulate/demo`;
  private simulateUrl = `${environment.apiUrl}/api/simulate`;
  private beamPatternUrl = `${environment.apiUrl}/api/simulations/beam-pattern`;
  private modComparisonUrl = `${environment.apiUrl}/api/simulations/modulation-comparison`;
  private compareUrl = `${environment.apiUrl}/api/simulations/compare`;
  private historyUrl = `${environment.apiUrl}/api/simulations`;

  constructor(private http: HttpClient) { }

  /**
   * Triggers a quick demo simulation (QPSK defaults) for the initial chart load.
   */
  getDemoSimulation(): Observable<SimulationResult> {
    return this.http.get<SimulationResult>(this.demoUrl);
  }

  /**
   * Triggers a full custom simulation with user-supplied parameters.
   */
  runNewSimulation(request: SimulationRequest): Observable<SimulationResult> {
    return this.http.post<SimulationResult>(this.simulateUrl, request);
  }

  /**
   * Triggers a beam pattern simulation.
   */
  runBeamPattern(request: BeamPatternRequest): Observable<BeamPatternResult> {
    return this.http.post<BeamPatternResult>(this.beamPatternUrl, request);
  }

  /**
   * Triggers a new modulation comparison simulation.
   */
  runModulationComparison(request: ModulationComparisonRequest): Observable<ModulationComparisonResult> {
    return this.http.post<ModulationComparisonResult>(this.modComparisonUrl, request);
  }

  /**
   * Triggers a Shannon channel capacity simulation.
   */
  runChannelCapacity(request: any): Observable<any> {
    return this.http.post<any>(`${environment.apiUrl}/api/simulations/channel-capacity`, request);
  }

  /**
   * Triggers a Path Loss per-ray analysis simulation.
   */
  runPathLoss(request: PathLossRequest): Observable<PathLossResult> {
    return this.http.post<PathLossResult>(`${environment.apiUrl}/api/simulations/path-loss`, request);
  }

  /**
   * Estimates compute time for a given simulation — no DB write.
   */
  runEstimate(request: SimulationEstimateRequest): Observable<SimulationEstimateResult> {
    return this.http.post<SimulationEstimateResult>(`${environment.apiUrl}/api/simulations/estimate`, request);
  }

  /**
   * Triggers a new Ray Directions simulation and saves it.
   */
  runRayDirections(request: RayDirectionRequest): Observable<RayDirectionResult> {
    return this.http.post<RayDirectionResult>(`${environment.apiUrl}/api/simulations/ray-directions`, request);
  }

  /**
   * Triggers a new UE Trajectory simulation and saves it.
   */
  runUeTrajectory(request: UeTrajectoryRequest): Observable<UeTrajectoryResult> {
    return this.http.post<UeTrajectoryResult>(`${environment.apiUrl}/api/simulations/ue-trajectory`, request);
  }

  /**
   * Compares two saved simulations by their IDs.
   */
  compareSimulations(id1: number, id2: number): Observable<ComparisonResponse> {
    return this.http.get<ComparisonResponse>(`${this.compareUrl}?id1=${id1}&id2=${id2}`);
  }

  /**
   * Retrieves the full history of all past simulations saved to PostgreSQL.
   */
  getAllSimulations(): Observable<SimulationHistoryItem[]> {
    return this.http.get<SimulationHistoryItem[]>(this.historyUrl);
  }

  /**
   * Fetches the share link data for a saved simulation.
   */
  getShareLink(id: number): Observable<{ shareToken: string; shareUrl: string }> {
    return this.http.get<{ shareToken: string; shareUrl: string }>(`${this.historyUrl}/${id}/share-link`);
  }

  /**
   * Retrieves a simulation result by its public share token.
   */
  getSimulationByShareToken(token: string): Observable<any> {
    return this.http.get<any>(`${environment.apiUrl}/api/simulations/public/${token}`);
  }

  /**
   * Fetches the list of available chart colormaps.
   */
  getColormaps(): Observable<ColormapOption[]> {
    return this.http.get<ColormapOption[]>(`${environment.apiUrl}/api/simulations/colormaps`);
  }
}
