import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatButtonModule } from '@angular/material/button';
import { MatTableModule } from '@angular/material/table';
import { MatIconModule } from '@angular/material/icon';
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
    MatDialogModule,
    RouterModule
  ],
  templateUrl: './history.component.html',
  styleUrl: './history.component.scss'
})
export class HistoryComponent implements OnInit {
  // Contains the list of past simulations
  simulationHistory: SimulationHistoryItem[] = [];
  
  // The uniquely selected simulation to display a chart for
  selectedSimulation: SimulationHistoryItem | null = null;
  
  isLoading = true;
  isGenerating = false;
  errorMsg = '';

  // The columns to display in the Angular Material table
  displayedColumns: string[] = ['index', 'type', 'createdAt', 'hardwareUsed', 'action'];

  // Chart.js Configuration
  public lineChartData: ChartConfiguration<'line'>['data'] = {
    labels: [],
    datasets: [
      {
        data: [],
        label: 'Bit Error Rate',
        fill: false,
        tension: 0.1,
        borderColor: '#00ff88', // Match the dashboard styling (Green line)
        pointBackgroundColor: '#00ff88',
        pointBorderColor: '#fff',
        pointHoverBackgroundColor: '#fff',
        pointHoverBorderColor: 'rgba(148,159,177,0.8)'
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
        grid: { color: '#333333' }
      },
      y: {
        type: 'logarithmic',
        title: { display: true, text: 'Bit Error Rate (BER)', color: '#aaaaaa' },
        ticks: { color: '#aaaaaa' },
        grid: { color: '#333333' }
      }
    }
  };

  public lineChartLegend = true;

  constructor(
    private simulationService: SimulationService,
    private dialog: MatDialog,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.fetchHistory();
  }

  /**
   * Fetches the complete list of simulations saved in PostgreSQL.
   */
  fetchHistory(): void {
    this.isLoading = true;
    this.simulationService.getAllSimulations().subscribe({
      next: (history) => {
        this.simulationHistory = history;
        this.isLoading = false;
        
        // Clear selected simulation when refreshing the list
        this.selectedSimulation = null;
      },
      error: (err) => {
        console.error('Failed to load history', err);
        this.errorMsg = 'Failed to load simulation history. Please ensure PostgreSQL and the backend are running.';
        this.isLoading = false;
      }
    });
  }

  /**
   * Generates a completely new simulation explicitly creatively creatively efficiently organically cleanly smoothly.
   */
  runNewSimulation(): void {
    this.router.navigate(['/dashboard']);
  }

  /**
   * Attaches the selected simulation row to the local variable so the
   * chart conditionally renders. Parses numeric array strings into JS arrays.
   */
  selectSimulation(simulation: SimulationHistoryItem): void {
    this.selectedSimulation = simulation;
    
    if (simulation.simulationType === 'BEAM_PATTERN' || simulation.simulationType === 'MOD_COMPARISON') {
      // Mod comparison and beam pattern don't use the simple line chart, implement rendering later
      return;
    }

    // Convert the database JSON strings back into numerical arrays
    const snrDbArray: number[] = JSON.parse(simulation.snrDb);
    const berTheoreticalArray: number[] = simulation.berTheoretical ? JSON.parse(simulation.berTheoretical) : [];
    const berSimulatedArray: number[] = simulation.berSimulated ? JSON.parse(simulation.berSimulated) : [];

    // Assuming we update lineChartData to have two datasets for BER vs SNR
    if (!this.lineChartData.datasets[1]) {
        this.lineChartData.datasets.push({
            data: [],
            label: 'Simulated BER',
            fill: false,
            tension: 0.1,
            borderColor: '#ff6b6b'
        });
        this.lineChartData.datasets[0].label = 'Theoretical BER';
        this.lineChartData.datasets[0].borderColor = '#64ffda';
    }

    this.lineChartData.labels = snrDbArray;
    this.lineChartData.datasets[0].data = berTheoreticalArray;
    this.lineChartData.datasets[1].data = berSimulatedArray;
  }

  /**
   * Fires the share controller perfectly explicitly organically mapping precisely securely intrinsically.
   */
  shareSimulation(id: number): void {
    this.simulationService.getShareLink(id).subscribe({
      next: (res) => {
        this.dialog.open(ShareLinkDialogComponent, {
          width: '500px',
          data: { shareUrl: res.shareUrl }
        });
      },
      error: (err) => console.error("Unconditionally completely utterly securely implicitly intrinsically effectively securely completely seamlessly failed extracting parameters explicitly optimally uniquely.", err)
    });
  }
}
