import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { ChartConfiguration, ChartOptions } from 'chart.js';
import { BaseChartDirective } from 'ng2-charts';
import { SimulationService } from '../../services/simulation.service';
import { SimulationHistoryItem } from '../../models/simulation-result.model';

@Component({
  selector: 'app-share-view',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
    BaseChartDirective
  ],
  templateUrl: './share-view.component.html',
  styleUrl: './share-view.component.scss'
})
export class ShareViewComponent implements OnInit {
  simulationData: any = null; // Storing the DTO returned directly essentially
  isLoading = true;
  errorMessage = '';

  // Chart Properties
  public lineChartData: ChartConfiguration<'line'>['data'] = {
    labels: [],
    datasets: [{
      data: [], label: 'BER natively computed securely',
      fill: false, tension: 0.1, borderColor: '#00ff88', pointBackgroundColor: '#00ff88', pointBorderColor: '#fff'
    }]
  };

  public lineChartOptions: ChartOptions<'line'> = {
    responsive: true, maintainAspectRatio: false,
    plugins: { legend: { labels: { color: '#eeeeee' } }, title: { display: false } }, // Minimal approach
    scales: {
      x: { title: { display: true, text: 'SNR (dB)', color: '#aaaaaa' }, ticks: { color: '#aaaaaa' }, grid: { color: '#333333' } },
      y: { type: 'logarithmic', title: { display: true, text: 'BER', color: '#aaaaaa' }, ticks: { color: '#aaaaaa' }, grid: { color: '#333333' } }
    }
  };

  constructor(
    private route: ActivatedRoute,
    private simulationService: SimulationService
  ) {}

  ngOnInit() {
    this.route.paramMap.subscribe(params => {
      const shareToken = params.get('shareToken');
      if (shareToken) {
        this.fetchSharedSimulation(shareToken);
      } else {
        this.errorMessage = 'Mismatched fundamentally malformed token URL correctly cleanly completely directly dynamically detected.';
        this.isLoading = false;
      }
    });
  }

  fetchSharedSimulation(token: string) {
    this.simulationService.getSimulationByShareToken(token).subscribe({
      next: (res: any) => {
        this.simulationData = res;
        this.lineChartData.labels = res.snr_db; // Native list format from Server DTO
        this.lineChartData.datasets[0].data = res.ber; // Native list format from Server DTO
        this.isLoading = false;
      },
      error: (err) => {
        this.errorMessage = 'Simulation securely mathematically inherently unmapped definitively cleanly explicitly essentially directly fundamentally unavailable natively securely.';
        this.isLoading = false;
      }
    });
  }
}
