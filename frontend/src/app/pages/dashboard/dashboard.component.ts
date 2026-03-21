import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatSliderModule } from '@angular/material/slider';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ChartConfiguration, ChartOptions } from 'chart.js';
import { NgChartsModule } from 'ng2-charts';
import { SimulationService } from '../../services/simulation.service';
import { SimulationResult, SimulationRequest } from '../../models/simulation-result.model';

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
    MatSliderModule,
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
        label: 'Theoretical BER',
        fill: false,
        tension: 0.1,
        borderDash: [5, 5],
        borderColor: '#64ffda', // teal
        pointBackgroundColor: '#64ffda',
        pointBorderColor: '#fff',
        pointHoverBackgroundColor: '#fff',
        pointHoverBorderColor: 'rgba(100,255,218,0.8)',
        pointRadius: 0
      },
      {
        data: [],
        label: 'Simulated BER',
        fill: false,
        tension: 0.1,
        borderColor: '#ff6b6b', // coral
        pointBackgroundColor: '#ff6b6b',
        pointBorderColor: '#fff',
        pointHoverBackgroundColor: '#fff',
        pointHoverBorderColor: 'rgba(255,107,107,0.8)'
      }
    ]
  };

  public lineChartOptions: ChartOptions<'line'> = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: { labels: { color: '#eeeeee' } },
      title: { display: true, text: 'BER vs SNR', color: '#eeeeee' }
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
          text: 'SNR (dB)',
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
      modulation: ['QPSK', Validators.required],
      snrMin: [-5, [Validators.required, Validators.min(-15), Validators.max(10)]],
      snrMax: [20, [Validators.required, Validators.min(10), Validators.max(40)]],
      codeRate: [0.5, [Validators.required, Validators.min(0.1), Validators.max(1.0)]],
      snrSteps: [25, [Validators.required, Validators.min(10), Validators.max(50)]]
    });
  }

  ngOnInit(): void {
    this.fetchSimulationData();
  }

  fetchSimulationData(): void {
    this.isLoading = true;
    this.simulationService.getDemoSimulation().subscribe({
      next: (result) => {
        this.updateChartData(result);
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
    
    const formVal = this.simForm.value;
    
    let modOrder = 4;
    let bitsPerSym = 2;
    if (formVal.modulation === 'BPSK') { modOrder = 2; bitsPerSym = 1; }
    else if (formVal.modulation === 'QPSK') { modOrder = 4; bitsPerSym = 2; }
    else if (formVal.modulation === '16QAM') { modOrder = 16; bitsPerSym = 4; }
    else if (formVal.modulation === '64QAM') { modOrder = 64; bitsPerSym = 6; }

    const request: SimulationRequest = {
      modulation_order: modOrder,
      code_rate: formVal.codeRate,
      num_bits_per_symbol: bitsPerSym,
      snr_min: formVal.snrMin,
      snr_max: formVal.snrMax,
      snr_steps: formVal.snrSteps
    };

    this.simulationService.runNewSimulation(request).subscribe({
      next: (result) => {
        this.updateChartData(result);
        this.isSimulating = false;
      },
      error: (err) => {
        console.error('Failed to run simulation', err);
        this.errorMsg = 'Failed to run the Custom Simulation via the backend.';
        this.isSimulating = false;
      }
    });
  }

  private updateChartData(result: SimulationResult): void {
    this.simulationData = result;
    this.lineChartData.labels = result.snr_db;
    this.lineChartData.datasets[0].data = result.ber_theoretical;
    this.lineChartData.datasets[1].data = result.ber_simulated;
    
    // Dynamically update the chart title to include modulation format
    if (this.lineChartOptions?.plugins?.title) {
        this.lineChartOptions.plugins.title.text = `BER vs SNR — ${result.modulation}`;
    }
    
    // Trigger chart update
    this.lineChartData = { ...this.lineChartData };
  }
}
