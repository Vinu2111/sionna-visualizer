import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ChannelModelParams, ChannelModelResult } from './channel-model.interfaces';

@Injectable({
  providedIn: 'root'
})
export class ChannelModelService {
  private apiUrl = '/api/simulate/channel-model';

  constructor(private http: HttpClient) {}

  // Executes the primary REST call passing params dynamically to backend controllers
  runSimulation(params: ChannelModelParams): Observable<ChannelModelResult> {
    return this.http.post<ChannelModelResult>(this.apiUrl, params);
  }

  // Returns preconfigured Chart.js object layout strictly isolating BER arrays securely overlaying AWGN theory
  buildBerChart(result: ChannelModelResult): any {
    return {
      type: 'line',
      data: {
        labels: result.snrDbRange,
        datasets: [
          {
            label: `${result.channelModel} Simulated BER`,
            data: result.berValues,
            borderColor: '#3f51b5',
            backgroundColor: 'transparent',
            borderWidth: 2,
            pointRadius: 3,
            tension: 0.1
          },
          {
            label: 'AWGN Theoretical BER',
            data: result.theoreticalBer,
            borderColor: '#9e9e9e',
            backgroundColor: 'transparent',
            borderWidth: 2,
            borderDash: [5, 5],
            pointRadius: 0,
            tension: 0.1
          }
        ]
      },
      options: {
        responsive: true,
        scales: {
          x: { 
            title: { display: true, text: 'SNR (dB)' } 
          },
          y: { 
            type: 'logarithmic', 
            title: { display: true, text: 'BER' },
            min: 1e-8,
            max: 1
          }
        },
        plugins: {
          tooltip: { mode: 'index', intersect: false }
        }
      }
    };
  }

  // Returns preconfigured Chart.js Bar construct handling the variable delayed peaks uniquely natively
  buildDelayProfileChart(result: ChannelModelResult): any {
    const labels = result.delayProfile.map(p => p.tap_delay.toFixed(1));
    const powers = result.delayProfile.map(p => p.power);

    return {
      type: 'bar',
      data: {
        labels: labels,
        datasets: [{
          label: 'Relative Power (dB)',
          data: powers,
          backgroundColor: '#ff9800',
          barPercentage: 0.2
        }]
      },
      options: {
        responsive: true,
        scales: {
          x: { title: { display: true, text: 'Tap Delay (ns)' } },
          y: { title: { display: true, text: 'Power (dB)' } }
        }
      }
    };
  }

  // Retrieves generic human description arrays mapping securely
  getModelDescription(model: string): string {
    const desc: { [key: string]: string } = {
      'CDL-A': 'Dense urban environments with significant multipath scattering delays.',
      'CDL-B': 'Standard urban environments with moderate scattering.',
      'CDL-C': 'Suburban environments with light scattering geometry.',
      'TDL-A': 'Indoor/dense environments triggering high delay spread taps natively.',
      'TDL-B': 'Standard urban environments generating medium delay spread.',
      'TDL-C': 'Rural/Line-of-sight environments maintaining minimal delay spread.'
    };
    return desc[model] || 'Standard custom simulation channel construct.';
  }
}
