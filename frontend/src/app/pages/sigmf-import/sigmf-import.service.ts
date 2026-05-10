import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { SigmfAnalysisResult, SigmfMetadata, IqSample } from './sigmf.interfaces';

@Injectable({
  providedIn: 'root'
})
export class SigmfImportService {
  private apiUrl = '/api/sigmf/import';

  constructor(private http: HttpClient) {}

  // Parses raw browser File API components extracting pure text JSON layouts securely  
  parseSigmfMeta(file: File): Observable<SigmfMetadata> {
    return new Observable(observer => {
      const reader = new FileReader();
      reader.onload = (e) => {
        try {
          const contents = e.target?.result as string;
          const json = JSON.parse(contents);
          const global = json.global || {};
          
          let parsed: SigmfMetadata = {
            sampleRate: global['core:sample_rate'] || 0,
            centerFrequency: json.captures && json.captures[0] ? json.captures[0]['core:frequency'] || 0 : 0,
            dataType: global['core:datatype'] || 'unknown',
            description: global['core:description'] || '',
            hardware: global['core:hw'] || '',
            author: global['core:author'] || '',
            numSamples: 0
          };
          observer.next(parsed);
          observer.complete();
        } catch (err) {
          observer.error('Failed to parse valid SigMF JSON structurally.');
        }
      };
      reader.onerror = () => observer.error('Error reading file.');
      reader.readAsText(file);
    });
  }

  // Prepares the structural FormData boundaries explicitly securing multipart blocks over the network automatically
  uploadFiles(metaFile: File, dataFile: File, simulationId: number): Observable<SigmfAnalysisResult> {
    const formData = new FormData();
    formData.append('metaFile', metaFile);
    formData.append('dataFile', dataFile);
    formData.append('simulationId', simulationId.toString());

    return this.http.post<SigmfAnalysisResult>(this.apiUrl, formData);
  }

  // Generates dual-dataset line mappings displaying physical hardware limits heavily overlaid against purely simulated software baselines safely
  buildOverlayChart(result: SigmfAnalysisResult): any {
    // Generate AWGN theoretical baseline dynamically based on exact same SNR layout
    const theoreticalBer = result.snrRange.map(snr => {
        const linSnr = Math.pow(10, snr / 10);
        return 0.5 * Math.exp(-linSnr / 2);
    });

    return {
      type: 'line',
      data: {
        labels: result.snrRange,
        datasets: [
          {
            label: 'Sionna Simulated BER',
            data: result.simulatedBer || [],
            borderColor: '#3f51b5',
            backgroundColor: 'transparent',
            borderWidth: 2,
            pointRadius: 3,
            tension: 0.1
          },
          {
            label: 'Hardware Measured BER Estimate',
            data: result.berEstimate,
            borderColor: '#ff4081',
            backgroundColor: 'transparent',
            borderWidth: 2,
            borderDash: [5, 5],
            pointRadius: 0,
            tension: 0.1
          },
          {
            label: 'AWGN Theoretical Reference',
            data: theoreticalBer,
            borderColor: '#9e9e9e',
            backgroundColor: 'transparent',
            borderWidth: 1.5,
            borderDash: [2, 2],
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

  // Parses heavy numeric blocks creating mapped geometric constraints strictly for visualization pipelines directly 
  buildConstellationChart(result: SigmfAnalysisResult): any {
    // Reconstruct structural geometric points securely
    const points = [];
    for(let i=0; i<result.iqSamplesI.length; i++) {
        points.push({ x: result.iqSamplesI[i], y: result.iqSamplesQ[i] });
    }

    return {
      type: 'scatter',
      data: {
        datasets: [{
          label: 'IQ Symbols',
          data: points,
          backgroundColor: 'rgba(63, 81, 181, 0.5)',
          borderColor: 'rgba(63, 81, 181, 0.8)',
          pointRadius: 3
        }]
      },
      options: {
        responsive: true,
        aspectRatio: 1, // Keep plot square strictly mapping standard geometric boundaries natively
        scales: {
          x: { 
              title: { display: true, text: 'In-Phase (I)' },
              suggestedMin: -2, suggestedMax: 2
          },
          y: { 
              title: { display: true, text: 'Quadrature (Q)' },
              suggestedMin: -2, suggestedMax: 2 
          }
        },
        plugins: {
            legend: { display: false }
        }
      }
    };
  }
}
