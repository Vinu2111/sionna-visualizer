import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { PackageOptions } from '../models/package-options.interface';

@Injectable({
  providedIn: 'root'
})
export class ReproducibilityService {
  private apiUrl = '/api/export';

  constructor(private http: HttpClient) {}

  // Calls backend API to generate the reproducibility package and returns a Blob
  generatePackage(simulationId: number, options: PackageOptions): Observable<Blob> {
    const payload = {
      simulationId,
      includeRawBerData: options.includeRawBerData,
      includeBeamPatternData: options.includeBeamPatternData,
      anonymizeForBlindReview: options.anonymizeForBlindReview
    };
    
    // We expect a binary response (Blob) for the ZIP download mapping
    return this.http.post(`${this.apiUrl}/reproducibility`, payload, {
      responseType: 'blob'
    });
  }

  // Uses temporary hidden anchor tag to trigger a native browser file download safely
  downloadZip(blob: Blob, filename: string): void {
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = filename;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    window.URL.revokeObjectURL(url);
  }
}
