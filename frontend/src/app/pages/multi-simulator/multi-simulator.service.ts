import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { ComparisonResult, ExternalSimData, SimulationSummary } from './multi-simulator.interfaces';

@Injectable({
  providedIn: 'root'
})
export class MultiSimulatorService {

  private apiUrl = '/api/compare/multi-simulator';

  constructor(private http: HttpClient) {}

  // Standard mock fetch matching the structure natively resolving UI bounds perfectly seamlessly  
  loadUserSimulations(): Observable<SimulationSummary[]> {
    return of([
      { id: 1, modulation: 'QPSK', channelModel: 'AWGN', frequency: 28.0, createdAt: new Date().toISOString() },
      { id: 2, modulation: '16QAM', channelModel: 'CDL-B', frequency: 5.0, createdAt: new Date().toISOString() }
    ]);
  }

  // Translates complex multipart requirements synchronously enforcing binary mappings smoothly natively
  uploadAndCompare(sionnaSimulationId: number, simulatorType: string, csvFile: File): Observable<ComparisonResult> {
    const formData = new FormData();
    formData.append('sionnaSimulationId', sionnaSimulationId.toString());
    formData.append('simulatorType', simulatorType);
    formData.append('csvFile', csvFile);

    return this.http.post<ComparisonResult>(this.apiUrl, formData);
  }

  // Validates standard client mappings rapidly extracting base definitions smoothly cleanly strictly avoiding crashes 
  parseLocalCsv(file: File): Observable<ExternalSimData> {
    return new Observable(observer => {
        const reader = new FileReader();
        reader.onload = (e) => {
            const text = e.target?.result as string;
            const lines = text.split('\n');
            const headers = lines[0].split(',').map(h => h.trim().toLowerCase());
            
            observer.next({
               simulatorType: 'detected',
               snrValues: [], berValues: [], throughputValues: [],
               detectedColumns: headers
            });
            observer.complete();
        };
        reader.readAsText(file);
    });
  }

  // Orchestrates dual-line geometric configurations specifically mapped for exact mathematical benchmarks directly  
  buildBerComparisonChart(result: ComparisonResult): any {
     const theoreticalBer = result.snrPoints.map(snr => {
        const linSnr = Math.pow(10, snr / 10);
        return 0.5 * Math.exp(-linSnr / 2);
     });

     return {
      type: 'line',
      data: {
        labels: result.snrPoints,
        datasets: [
          {
            label: 'Sionna BER',
            data: result.sionnaBer,
            borderColor: '#3f51b5',
            backgroundColor: 'transparent',
            borderWidth: 2.5,
            pointRadius: 3,
            tension: 0.1
          },
          {
            label: `${result.simulatorType} BER`,
            data: result.externalBer,
            borderColor: '#ff9800',
            backgroundColor: 'transparent',
            borderWidth: 2.5,
            pointRadius: 3,
            tension: 0.1
          },
          {
            label: 'AWGN Theoretical',
            data: theoreticalBer,
            borderColor: '#9e9e9e',
            backgroundColor: 'transparent',
            borderWidth: 1,
            borderDash: [5, 5],
            pointRadius: 0,
            tension: 0.1
          }
        ]
      },
      options: {
        responsive: true,
        scales: {
          x: { title: { display: true, text: 'SNR (dB)' } },
          y: { 
            type: 'logarithmic', 
            title: { display: true, text: 'BER' },
            min: 1e-8,
            max: 1
          }
        },
        plugins: { tooltip: { mode: 'index', intersect: false } }
      }
    };
  }

  // Configures the logical visual mappings specifically mapping scale correctly  
  buildThroughputChart(result: ComparisonResult): any {
    return {
      type: 'line',
      data: {
        labels: result.snrPoints,
        datasets: [
          {
            label: 'Sionna Throughput',
            data: result.sionnaThroughput,
            borderColor: '#3f51b5',
            backgroundColor: '#3f51b5',
            borderWidth: 2,
            tension: 0.1
          },
          {
            label: `${result.simulatorType} Throughput`,
            data: result.externalThroughput,
            borderColor: '#ff9800',
            backgroundColor: '#ff9800',
            borderWidth: 2,
            tension: 0.1
          }
        ]
      },
      options: {
        responsive: true,
        scales: {
          x: { title: { display: true, text: 'SNR (dB)' } },
          y: { title: { display: true, text: 'Throughput (Mbps)' } }
        },
        plugins: { tooltip: { mode: 'index', intersect: false } }
      }
    };
  }

  generateShareableUrl(comparisonId: number): string {
     return `${window.location.origin}/compare/multi/${comparisonId}`;
  }
}
