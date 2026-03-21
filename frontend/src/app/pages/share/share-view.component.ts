import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { ChartConfiguration, ChartOptions } from 'chart.js';
import { NgChartsModule } from 'ng2-charts';
import { SimulationService } from '../../services/simulation.service';
import { SimulationHistoryItem } from '../../models/simulation-result.model';
import { ExportService } from '../../services/export.service';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatTooltipModule } from '@angular/material/tooltip';
import { ViewChild } from '@angular/core';
import { BaseChartDirective } from 'ng2-charts';

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
    NgChartsModule,
    MatSnackBarModule,
    MatTooltipModule
  ],
  templateUrl: './share-view.component.html',
  styleUrl: './share-view.component.scss'
})
export class ShareViewComponent implements OnInit {
  simulationData: any = null; // Storing the DTO returned directly essentially
  isLoading = true;
  errorMessage = '';

  @ViewChild(BaseChartDirective) chart?: BaseChartDirective;

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
    private simulationService: SimulationService,
    private exportService: ExportService,
    private snackBar: MatSnackBar
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
        this.errorMessage = 'Simulation result unavailable.';
        this.isLoading = false;
      }
    });
  }

  // ─── Export Actions ───────────────────────────────────────────────────────

  downloadPng(): void {
    if (this.chart?.chart) {
      const filename = this.exportService.berFilename(this.simulationData?.modulation || 'qpsk', 'png');
      this.exportService.downloadCanvasPng(this.chart.chart.canvas, filename);
    }
  }

  downloadCsv(): void {
    if (!this.simulationData) return;
    const filename = this.exportService.berFilename(this.simulationData.modulation || 'qpsk', 'csv');
    // Normalize share DTO to generator format
    const normalized = {
      modulation: this.simulationData.modulation,
      snr_db: this.simulationData.snr_db,
      ber_simulated: this.simulationData.ber
    };
    const content = this.exportService.generateBerCsv(normalized);
    this.exportService.downloadCSV(filename, content);
  }

  downloadJson(): void {
    if (!this.simulationData) return;
    const filename = this.exportService.berFilename(this.simulationData.modulation || 'qpsk', 'json');
    const wrapped = this.exportService.wrapWithMetadata('SHARED_RESULT', this.simulationData);
    this.exportService.downloadJSON(filename, wrapped);
  }

  copyJson(): void {
    if (!this.simulationData) return;
    const wrapped = this.exportService.wrapWithMetadata('SHARED_RESULT', this.simulationData);
    this.exportService.copyToClipboard(wrapped).then(() => {
      this.snackBar.open('JSON copied to clipboard', 'OK', { duration: 2000 });
    });
  }
}
