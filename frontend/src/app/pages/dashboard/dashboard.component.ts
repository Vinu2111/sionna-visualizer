import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ChartConfiguration, ChartOptions } from 'chart.js';
import { NgChartsModule } from 'ng2-charts';
import { SimulationService } from '../../services/simulation.service';
import { SimulationResult } from '../../models/simulation-result.model';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    CommonModule, 
    NgChartsModule, 
    MatCardModule, 
    MatProgressSpinnerModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    ReactiveFormsModule
  ],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss'
})
export class DashboardComponent implements OnInit {
  simulationData: SimulationResult | null = null;
  isLoading = true;
  isSimulating = false;
  errorMsg = '';
  
  simForm: FormGroup;

  public lineChartData: ChartConfiguration<'line'>['data'] = {
    labels: [],
    datasets: [
      {
        data: [],
        label: 'Bit Error Rate',
        fill: false,
        tension: 0.1,
        borderColor: '#00ff88',
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
      y: {
        type: 'logarithmic',
        title: {
          display: true,
          text: 'Bit Error Rate (BER)',
          color: '#aaaaaa'
        },
        ticks: {
          color: '#aaaaaa',
          callback: function(value: any) {
            return Number(value).toExponential(0);
          }
        },
        grid: { color: '#333333' }
      },
      x: {
        title: {
          display: true,
          text: 'Signal-to-Noise Ratio (dB)',
          color: '#aaaaaa'
        },
        ticks: { color: '#aaaaaa' },
        grid: { color: '#333333' }
      }
    }
  };

  public lineChartLegend = true;

  constructor(
    private simulationService: SimulationService,
    private fb: FormBuilder
  ) {
    this.simForm = this.fb.group({
      snrMin: [0, [Validators.required, Validators.min(-10), Validators.max(10)]],
      snrMax: [20, [Validators.required, Validators.min(10), Validators.max(40)]],
      modulation: ['QPSK', Validators.required],
      numSymbols: [10000, Validators.required]
    });
  }

  ngOnInit(): void {
    this.fetchSimulationData();
  }

  fetchSimulationData(): void {
    this.isLoading = true;
    this.simulationService.getDemoSimulation().subscribe({
      next: (result) => {
        this.simulationData = result;
        this.lineChartData.labels = result.snr_db;
        this.lineChartData.datasets[0].data = result.ber;
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Failed to load simulation data', err);
        this.errorMsg = 'Failed to load simulation results from the backend.';
        this.isLoading = false;
      }
    });
  }

  runSimulation(): void {
    if (this.simForm.invalid) {
      return;
    }
    
    this.isSimulating = true;
    this.errorMsg = '';
    
    // Call the API with these parameters properly injected dynamically gracefully
    this.simulationService.runNewSimulation(this.simForm.value).subscribe({
      next: (result) => {
        this.simulationData = result;
        this.lineChartData.labels = result.snr_db;
        this.lineChartData.datasets[0].data = result.ber;
        this.isSimulating = false;
      },
      error: (err) => {
        console.error('Failed to run simulation', err);
        this.errorMsg = 'Failed to run the Custom Simulation via the backend.';
        this.isSimulating = false;
      }
    });
  }
}
