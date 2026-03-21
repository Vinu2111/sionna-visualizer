import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatButtonModule } from '@angular/material/button';
import { MatTableModule } from '@angular/material/table';
import { MatIconModule } from '@angular/material/icon';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatTooltipModule } from '@angular/material/tooltip';
import { ChartConfiguration, ChartOptions } from 'chart.js';
import { NgChartsModule } from 'ng2-charts';
import { SimulationService } from '../../services/simulation.service';
import { SimulationHistoryItem } from '../../models/simulation-result.model';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { ShareLinkDialogComponent } from '../../components/share-dialog/share-dialog.component';
import { Router, RouterModule } from '@angular/router';

@Component({
  selector: 'app-history',
  standalone: true,
  imports: [
    CommonModule,
    NgChartsModule,
    MatCardModule,
    MatProgressSpinnerModule,
    MatButtonModule,
    MatTableModule,
    MatIconModule,
    MatCheckboxModule,
    MatSnackBarModule,
    MatTooltipModule,
    MatDialogModule,
    RouterModule
  ],
  templateUrl: './history.component.html',
  styleUrl: './history.component.scss'
})
export class HistoryComponent implements OnInit {
  simulationHistory: SimulationHistoryItem[] = [];
  selectedSimulation: SimulationHistoryItem | null = null;
  isLoading = true;
  isGenerating = false;
  errorMsg = '';

  // Compare selection — max 2
  compareSelection: SimulationHistoryItem[] = [];

  displayedColumns: string[] = ['compare', 'index', 'type', 'createdAt', 'hardwareUsed', 'action'];

  // Chart config for history view
  public lineChartData: ChartConfiguration<'line'>['data'] = {
    labels: [],
    datasets: [
      {
        data: [], label: 'Theoretical BER',
        fill: false, tension: 0.1,
        borderColor: '#64ffda', pointBackgroundColor: '#64ffda',
        pointBorderColor: '#fff', pointRadius: 0
      },
      {
        data: [], label: 'Simulated BER',
        fill: false, tension: 0.1,
        borderColor: '#ff6b6b', pointBackgroundColor: '#ff6b6b',
        pointBorderColor: '#fff'
      }
    ]
  };

  public lineChartOptions: ChartOptions<'line'> = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: { labels: { color: '#eeeeee' } },
      title: { display: true, text: 'Bit Error Rate vs SNR', color: '#eeeeee' }
    },
    scales: {
      x: {
        title: { display: true, text: 'Signal-to-Noise Ratio (dB)', color: '#aaaaaa' },
        ticks: { color: '#aaaaaa' },
        grid: { color: '#1e2a3a' }
      },
      y: {
        type: 'logarithmic',
        title: { display: true, text: 'Bit Error Rate (BER)', color: '#aaaaaa' },
        ticks: { color: '#aaaaaa', callback: (v: any) => Number(v).toExponential(0) },
        grid: { color: '#1e2a3a' }
      }
    }
  };

  public lineChartLegend = true;

  constructor(
    private simulationService: SimulationService,
    private dialog: MatDialog,
    private router: Router,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.fetchHistory();
  }

  fetchHistory(): void {
    this.isLoading = true;
    this.simulationService.getAllSimulations().subscribe({
      next: (history) => {
        this.simulationHistory = history;
        this.isLoading = false;
        this.selectedSimulation = null;
      },
      error: () => {
        this.errorMsg = 'Failed to load simulation history.';
        this.isLoading = false;
      }
    });
  }

  runNewSimulation(): void {
    this.router.navigate(['/dashboard']);
  }

  selectSimulation(simulation: SimulationHistoryItem): void {
    this.selectedSimulation = simulation;

    if (simulation.simulationType === 'BEAM_PATTERN' || simulation.simulationType === 'MOD_COMPARISON') {
      return;
    }

    const snrDbArray: number[]     = JSON.parse(simulation.snrDb || '[]');
    const berThArray: number[]     = simulation.berTheoretical ? JSON.parse(simulation.berTheoretical) : [];
    const berSimArray: number[]    = simulation.berSimulated   ? JSON.parse(simulation.berSimulated)   : [];

    this.lineChartData = {
      ...this.lineChartData,
      labels: snrDbArray,
      datasets: [
        { ...this.lineChartData.datasets[0], data: berThArray },
        { ...this.lineChartData.datasets[1], data: berSimArray }
      ]
    };
  }

  shareSimulation(id: number): void {
    this.simulationService.getShareLink(id).subscribe({
      next: (res) => {
        this.dialog.open(ShareLinkDialogComponent, {
          width: '500px',
          data: { shareUrl: res.shareUrl }
        });
      },
      error: (err) => console.error('Share link fetch failed', err)
    });
  }

  // ─── Compare selection logic ────────────────────────────────────────────

  isInCompareSelection(sim: SimulationHistoryItem): boolean {
    return this.compareSelection.some(s => s.id === sim.id);
  }

  compareSelectionIndex(sim: SimulationHistoryItem): number {
    return this.compareSelection.findIndex(s => s.id === sim.id);
  }

  toggleCompareSelection(sim: SimulationHistoryItem): void {
    const idx = this.compareSelectionIndex(sim);
    if (idx >= 0) {
      // Deselect
      this.compareSelection.splice(idx, 1);
    } else if (this.compareSelection.length >= 2) {
      this.snackBar.open('You can only compare 2 simulations at a time.', 'OK', {
        duration: 3000,
        panelClass: ['error-snackbar']
      });
    } else {
      this.compareSelection.push(sim);
    }
  }

  compareSelected(): void {
    if (this.compareSelection.length !== 2) return;
    this.router.navigate(['/compare'], {
      queryParams: { id1: this.compareSelection[0].id, id2: this.compareSelection[1].id }
    });
  }

  /** Quick "compare this with" — mark A and navigate when B is already known */
  quickCompare(sim: SimulationHistoryItem): void {
    if (this.compareSelection.length === 0) {
      this.compareSelection.push(sim);
      this.snackBar.open('Simulation A selected. Now pick Simulation B.', 'OK', { duration: 3000 });
    } else if (this.compareSelection.length === 1 && this.compareSelection[0].id !== sim.id) {
      this.router.navigate(['/compare'], {
        queryParams: { id1: this.compareSelection[0].id, id2: sim.id }
      });
    }
  }
}
