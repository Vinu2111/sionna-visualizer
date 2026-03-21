import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatSelectModule } from '@angular/material/select';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatTooltipModule } from '@angular/material/tooltip';
import { ChartConfiguration, ChartOptions, ChartData } from 'chart.js';
import { NgChartsModule } from 'ng2-charts';
import { FormsModule } from '@angular/forms';
import { SimulationService, ComparisonResponse, SimulationResultDto } from '../../services/simulation.service';
import { SimulationHistoryItem } from '../../models/simulation-result.model';

@Component({
  selector: 'app-compare',
  standalone: true,
  imports: [
    CommonModule, RouterModule, FormsModule, NgChartsModule,
    MatCardModule, MatButtonModule, MatSelectModule, MatFormFieldModule,
    MatProgressSpinnerModule, MatProgressBarModule, MatIconModule,
    MatSnackBarModule, MatTooltipModule
  ],
  templateUrl: './compare.component.html',
  styleUrl: './compare.component.scss'
})
export class CompareComponent implements OnInit {
  history: SimulationHistoryItem[] = [];
  isLoadingHistory = true;
  isComparing = false;

  selectedId1: number | null = null;
  selectedId2: number | null = null;

  comparisonResult: ComparisonResponse | null = null;

  // ─── BER overlay chart ───────────────────────────────────────────────────
  public berChartData: ChartConfiguration<'line'>['data'] = {
    labels: [],
    datasets: [
      { data: [], label: 'Sim A — Theoretical', fill: false, tension: 0.1, borderDash: [5, 5], borderColor: '#64ffda', pointRadius: 0, borderWidth: 2 },
      { data: [], label: 'Sim A — Simulated',   fill: false, tension: 0.1, borderColor: '#64ffda', pointRadius: 0, borderWidth: 2 },
      { data: [], label: 'Sim B — Theoretical', fill: false, tension: 0.1, borderDash: [5, 5], borderColor: '#ff6b6b', pointRadius: 0, borderWidth: 2 },
      { data: [], label: 'Sim B — Simulated',   fill: false, tension: 0.1, borderColor: '#ff6b6b', pointRadius: 0, borderWidth: 2 }
    ]
  };

  public berChartOptions: ChartOptions<'line'> = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: { position: 'top', labels: { color: '#eeeeee' } },
      title: { display: true, text: 'BER vs SNR — Overlay Comparison', color: '#eeeeee', font: { size: 14 } }
    },
    scales: {
      y: {
        type: 'logarithmic',
        title: { display: true, text: 'Bit Error Rate (BER)', color: '#aaaaaa' },
        ticks: { color: '#aaaaaa', callback: (v: any) => Number(v).toExponential(0) },
        grid: { color: '#1e2a3a' }
      },
      x: {
        title: { display: true, text: 'SNR (dB)', color: '#aaaaaa' },
        ticks: { color: '#aaaaaa' },
        grid: { color: '#1e2a3a' }
      }
    }
  };

  // ─── Beam overlay chart ──────────────────────────────────────────────────
  public beamChartData: ChartData<'line'> = {
    labels: [],
    datasets: [
      { data: [], label: 'Sim A Beam Pattern', fill: false, tension: 0.3, borderColor: '#64ffda', pointRadius: 0, borderWidth: 2 },
      { data: [], label: 'Sim B Beam Pattern', fill: false, tension: 0.3, borderColor: '#ff6b6b', pointRadius: 0, borderWidth: 2 }
    ]
  };

  public beamChartOptions: ChartOptions<'line'> = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: { position: 'top', labels: { color: '#eeeeee' } },
      title: { display: true, text: 'Beam Pattern — Overlay Comparison', color: '#eeeeee', font: { size: 14 } }
    },
    scales: {
      y: { title: { display: true, text: 'Gain (dB)', color: '#aaaaaa' }, ticks: { color: '#aaaaaa' }, grid: { color: '#1e2a3a' } },
      x: { title: { display: true, text: 'Angle (°)', color: '#aaaaaa' }, ticks: { color: '#aaaaaa' }, grid: { color: '#1e2a3a' } }
    }
  };

  constructor(
    private simulationService: SimulationService,
    private route: ActivatedRoute,
    private router: Router,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    // Load history for the dropdowns
    this.simulationService.getAllSimulations().subscribe({
      next: (h) => { this.history = h; this.isLoadingHistory = false; },
      error: () => { this.isLoadingHistory = false; }
    });

    // Pre-fill from query params e.g. /compare?id1=5&id2=9
    this.route.queryParams.subscribe(params => {
      if (params['id1']) this.selectedId1 = +params['id1'];
      if (params['id2']) this.selectedId2 = +params['id2'];
      if (this.selectedId1 && this.selectedId2) {
        this.runComparison();
      }
    });
  }

  runComparison(): void {
    if (!this.selectedId1 || !this.selectedId2) return;
    this.isComparing = true;
    this.comparisonResult = null;

    // Update URL without navigation
    this.router.navigate([], { queryParams: { id1: this.selectedId1, id2: this.selectedId2 }, replaceUrl: true });

    this.simulationService.compareSimulations(this.selectedId1, this.selectedId2).subscribe({
      next: (res) => {
        this.comparisonResult = res;
        this.buildCharts(res);
        this.isComparing = false;
      },
      error: () => {
        this.isComparing = false;
        this.snackBar.open('Failed to load comparison. Please try again.', 'Dismiss', {
          duration: 5000, panelClass: ['error-snackbar']
        });
      }
    });
  }

  private buildCharts(res: ComparisonResponse): void {
    const type = res.comparison_metadata.type1;

    if (res.comparison_metadata.can_overlay && type === 'BER_SNR') {
      const s1 = res.simulation1;
      const s2 = res.simulation2;
      const labels = JSON.parse(s1.snrDb || '[]');
      this.berChartData = {
        labels,
        datasets: [
          { ...this.berChartData.datasets[0], data: JSON.parse(s1.berTheoretical || '[]'), label: `Sim A Theoretical (${s1.modulationType})` },
          { ...this.berChartData.datasets[1], data: JSON.parse(s1.berSimulated   || '[]'), label: `Sim A Simulated (${s1.modulationType})` },
          { ...this.berChartData.datasets[2], data: JSON.parse(s2.berTheoretical || '[]'), label: `Sim B Theoretical (${s2.modulationType})` },
          { ...this.berChartData.datasets[3], data: JSON.parse(s2.berSimulated   || '[]'), label: `Sim B Simulated (${s2.modulationType})` }
        ]
      };
    }

    if (res.comparison_metadata.can_overlay && type === 'BEAM_PATTERN') {
      const s1 = res.simulation1;
      const s2 = res.simulation2;
      const labels = JSON.parse(s1.beamAngles || '[]');
      this.beamChartData = {
        labels,
        datasets: [
          { ...this.beamChartData.datasets[0], data: JSON.parse(s1.beamPatternDb || '[]'), label: `Sim A (${s1.numAntennas} ant, ${s1.frequencyGhz} GHz, ${s1.steeringAngle}°)` },
          { ...this.beamChartData.datasets[1], data: JSON.parse(s2.beamPatternDb || '[]'), label: `Sim B (${s2.numAntennas} ant, ${s2.frequencyGhz} GHz, ${s2.steeringAngle}°)` }
        ]
      };
    }
  }

  /** Determine winner for BER/SNR — lower theoretical BER at last (highest) SNR point wins */
  getWinner(res: ComparisonResponse): { label: string; reason: string } | null {
    const meta = res.comparison_metadata;
    if (!meta.same_type) {
      return { label: 'N/A', reason: 'Different simulation types — direct comparison not applicable.' };
    }
    if (meta.type1 === 'BER_SNR') {
      const a = JSON.parse(res.simulation1.berTheoretical || '[]') as number[];
      const b = JSON.parse(res.simulation2.berTheoretical || '[]') as number[];
      const aLast = a[a.length - 1];
      const bLast = b[b.length - 1];
      if (aLast < bLast) {
        return { label: 'Sim A', reason: `Sim A (${res.simulation1.modulationType}) achieves lower BER at high SNR (${aLast?.toExponential(2)} vs ${bLast?.toExponential(2)}).` };
      } else if (bLast < aLast) {
        return { label: 'Sim B', reason: `Sim B (${res.simulation2.modulationType}) achieves lower BER at high SNR (${bLast?.toExponential(2)} vs ${aLast?.toExponential(2)}).` };
      }
      return { label: 'Tie', reason: 'Both simulations achieve identical BER at high SNR.' };
    }
    if (meta.type1 === 'BEAM_PATTERN') {
      const aWidth = res.simulation1.mainLobeWidth;
      const bWidth = res.simulation2.mainLobeWidth;
      if (aWidth !== null && bWidth !== null && aWidth < bWidth) {
        return { label: 'Sim A', reason: `Sim A has a narrower main lobe (${aWidth}° vs ${bWidth}°).` };
      } else if (bWidth !== null && aWidth !== null && bWidth < aWidth) {
        return { label: 'Sim B', reason: `Sim B has a narrower main lobe (${bWidth}° vs ${aWidth}°).` };
      }
      return { label: 'Tie', reason: 'Both simulations have identical main lobe width.' };
    }
    return { label: 'N/A', reason: 'Modulation comparison results cannot be directly compared.' };
  }

  /** Short displayable label for a history item */
  historyLabel(sim: SimulationHistoryItem): string {
    if (sim.simulationType === 'BEAM_PATTERN') {
      return `Beam · ${new Date(sim.createdAt).toLocaleDateString()} · ${sim.numAntennas ?? '?'} ant`;
    }
    if (sim.simulationType === 'MOD_COMPARISON') {
      return `Mod Comp · ${new Date(sim.createdAt).toLocaleDateString()}`;
    }
    return `BER/SNR · ${new Date(sim.createdAt).toLocaleDateString()} · ${sim.modulationType ?? '?'}`;
  }

  /** Share current comparison URL */
  shareComparison(): void {
    navigator.clipboard.writeText(window.location.href).then(() => {
      this.snackBar.open('Comparison link copied to clipboard!', 'OK', { duration: 3000 });
    });
  }

  formatDate(d: string): string {
    return new Date(d).toLocaleString();
  }
}
