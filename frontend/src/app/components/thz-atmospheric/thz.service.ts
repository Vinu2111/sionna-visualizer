import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ThzParams, ThzResult, ThzScenario, LinkBudget, SpectrumPoint } from './thz.interfaces';

@Injectable({
  providedIn: 'root'
})
export class ThzService {
  private apiUrl = `${environment.apiUrl}/thz`;

  constructor(private http: HttpClient) {}

  /**
   * Sends slider values to backend.
   */
  calculateAtmosphericEffects(params: ThzParams): Observable<ThzResult> {
    // We map properties as backend uses snake_case keys (using our DTO shapes)
    const body = {
      frequency_ghz: params.frequencyGhz,
      humidity_percent: params.humidityPercent,
      temperature_celsius: params.temperatureCelsius,
      pressure_hpa: params.pressureHpa,
      rain_rate_mm_per_hr: params.rainRateMmPerHr,
      link_distance_meters: params.linkDistanceMeters,
      tx_power_dbm: params.txPowerDbm
    };
    return this.http.post<ThzResult>(`${this.apiUrl}/calculate`, body);
  }

  /**
   * Save named atmospheric scenario
   */
  saveScenario(name: string, params: ThzParams): Observable<ThzScenario> {
    const body = {
      name,
      params: {
        frequency_ghz: params.frequencyGhz,
        humidity_percent: params.humidityPercent,
        temperature_celsius: params.temperatureCelsius,
        pressure_hpa: params.pressureHpa,
        rain_rate_mm_per_hr: params.rainRateMmPerHr,
        link_distance_meters: params.linkDistanceMeters,
        tx_power_dbm: params.txPowerDbm
      }
    };
    return this.http.post<ThzScenario>(`${this.apiUrl}/scenarios`, body);
  }

  /**
   * Load all saved scenarios
   */
  getSavedScenarios(): Observable<ThzScenario[]> {
    return this.http.get<ThzScenario[]>(`${this.apiUrl}/scenarios`);
  }

  /**
   * Pure frontend calculation — no API call needed
   * Calculates Free Space Path Loss explicitly based on parameters.
   */
  calculateLinkBudget(params: ThzParams, result: ThzResult): LinkBudget {
    return {
      txPowerDbm: params.txPowerDbm,
      txAntennaGainDbi: 10,
      freeSpacePathLossDb: result.free_space_path_loss_db,
      molecularAbsorptionDb: result.molecular_absorption_db_per_km * (params.linkDistanceMeters / 1000.0),
      rainAttenuationDb: result.rain_attenuation_db_per_km * (params.linkDistanceMeters / 1000.0),
      rxAntennaGainDbi: 10,
      receivedPowerDbm: result.received_power_dbm
    };
  }
}
