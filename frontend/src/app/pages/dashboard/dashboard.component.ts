import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { MatCardModule } from '@angular/material/card';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatSliderModule } from '@angular/material/slider';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { ReactiveFormsModule, FormsModule, FormBuilder, FormGroup, Validators, AbstractControl, ValidationErrors } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { environment } from '../../../environments/environment';
import { MatTabsModule } from '@angular/material/tabs';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatIconModule } from '@angular/material/icon';
import { MatTooltipModule } from '@angular/material/tooltip';
import { ViewChild } from '@angular/core';
import { BaseChartDirective } from 'ng2-charts';
import { ExportService } from '../../services/export.service';
import { ChartConfiguration, ChartOptions, ChartData } from 'chart.js';
import { NgChartsModule } from 'ng2-charts';
import { SimulationService } from '../../services/simulation.service';
import {
  SimulationResult, SimulationRequest,
  BeamPatternResult, BeamPatternRequest,
  ModulationComparisonRequest, ModulationComparisonResult,
  ChannelCapacityRequest, ChannelCapacityResult
} from '../../models/simulation-result.model';

/** Custom validator: SNR min must be less than SNR max */
function snrRangeValidator(control: AbstractControl): ValidationErrors | null {
  const min = control.get('snrMin')?.value;
  const max = control.get('snrMax')?.value;
  if (min !== null && max !== null && min >= max) {
    return { snrRangeInvalid: true };
  }
  return null;
}

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    NgChartsModule,
    MatCardModule,
    MatProgressSpinnerModule,
    MatProgressBarModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatSliderModule,
    MatCheckboxModule,
    MatTabsModule,
    MatSnackBarModule,
    MatIconModule,
    MatTooltipModule,
    ReactiveFormsModule,
    FormsModule,
    RouterModule
  ],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss'
})
export class DashboardComponent implements OnInit {
  simulationData: SimulationResult | null = null;
  beamData: BeamPatternResult | null = null;
  modData: ModulationComparisonResult | null = null;
  capData: ChannelCapacityResult | null = null;
  isLoading = true;
  isSimulating = false;
  isBeamSimulating = false;
  isModSimulating = false;
  isCapSimulating = false;
  loadError = false;

  @ViewChild('berChart') berChart?: BaseChartDirective;
  @ViewChild('beamChart') beamChart?: BaseChartDirective;
  @ViewChild('modChart') modChart?: BaseChartDirective;
  @ViewChild('capChart') capChart?: BaseChartDirective;
  @ViewChild('spectralChart') spectralChart?: BaseChartDirective;

  apiKeys: any[] = [];
  newKeyDescription = '';
  isRevoking = false;

  simForm: FormGroup;
  beamForm: FormGroup;
  modForm: FormGroup;
  capForm: FormGroup;

  // ─────────────────────────────────────────────────────────────────────────
  // Chart Config: Radar (Beam)
  // ─────────────────────────────────────────────────────────────────────────
  public radarChartOptions: ChartConfiguration<'radar'>['options'] = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      title: { display: true, text: 'Antenna Beam Pattern', color: '#eeeeee' },
      legend: { display: false }
    },
    scales: {
      r: {
        angleLines: { color: 'rgba(255, 255, 255, 0.1)' },
        grid: { color: 'rgba(255, 255, 255, 0.1)' },
        pointLabels: { color: '#eeeeee', font: { size: 10 } },
        ticks: { backdropColor: 'transparent', color: '#aaaaaa' },
        min: -40,
        max: 0
      }
    }
  };

  public radarChartData: ChartData<'radar'> = {
    labels: [],
    datasets: [{
      data: [],
      label: 'Signal Strength (dB)',
      borderColor: '#64ffda',
      backgroundColor: 'rgba(100, 255, 218, 0.1)',
      borderWidth: 2,
      pointRadius: 0
    }]
  };

  // ─────────────────────────────────────────────────────────────────────────
  // Chart Config: Line (BER/SNR)
  // ─────────────────────────────────────────────────────────────────────────
  public lineChartData: ChartConfiguration<'line'>['data'] = {
    labels: [],
    datasets: [
      {
        data: [], label: 'Theoretical BER',
        fill: false, tension: 0.1, borderDash: [5, 5],
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
      title: { display: true, text: 'BER vs SNR', color: '#eeeeee' }
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

  // ─────────────────────────────────────────────────────────────────────────
  // Chart Config: Line (Mod Comparison)
  // ─────────────────────────────────────────────────────────────────────────
  public modChartData: ChartConfiguration<'line'>['data'] = {
    labels: [],
    datasets: [
      { data: [], label: 'BPSK',  fill: false, tension: 0.1, borderColor: '#ffffff', pointRadius: 0, borderWidth: 2 },
      { data: [], label: 'QPSK',  fill: false, tension: 0.1, borderColor: '#64ffda', pointRadius: 0, borderWidth: 2 },
      { data: [], label: '16QAM', fill: false, tension: 0.1, borderColor: '#f7b731', pointRadius: 0, borderWidth: 2 },
      { data: [], label: '64QAM', fill: false, tension: 0.1, borderColor: '#ff6b6b', pointRadius: 0, borderWidth: 2 }
    ]
  };

  public modChartOptions: ChartOptions<'line'> = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: { position: 'top', align: 'end', labels: { color: '#eeeeee' } },
      title: { display: true, text: 'Modulation Comparison — Theoretical BER', color: '#eeeeee' }
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

  // ─────────────────────────────────────────────────────────────────────────
  // Chart Config: Line (Channel Capacity)
  // ─────────────────────────────────────────────────────────────────────────
  public capChartData: ChartConfiguration<'line'>['data'] = {
    labels: [],
    datasets: []
  };

  public capChartOptions: ChartOptions<'line'> = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: { position: 'top', align: 'end', labels: { color: '#eeeeee' } },
      title: { display: true, text: 'Channel Capacity vs SNR', color: '#eeeeee' }
    },
    scales: {
      y: {
        title: { display: true, text: 'Capacity (Gbps)', color: '#aaaaaa' },
        ticks: { color: '#aaaaaa' },
        grid: { color: '#1e2a3a' }
      },
      x: {
        title: { display: true, text: 'SNR (dB)', color: '#aaaaaa' },
        ticks: { color: '#aaaaaa' },
        grid: { color: '#1e2a3a' }
      }
    }
  };

  // ─────────────────────────────────────────────────────────────────────────
  // Chart Config: Line (Spectral Efficiency)
  // ─────────────────────────────────────────────────────────────────────────
  public spectralChartData: ChartConfiguration<'line'>['data'] = {
    labels: [],
    datasets: []
  };

  public spectralChartOptions: ChartOptions<'line'> = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: { display: false },
      title: { display: true, text: 'Spectral Efficiency vs SNR', color: '#eeeeee' }
    },
    scales: {
      y: {
        title: { display: true, text: 'Efficiency (bits/s/Hz)', color: '#aaaaaa' },
        ticks: { color: '#aaaaaa' },
        grid: { color: '#1e2a3a' }
      },
      x: {
        title: { display: true, text: 'SNR (dB)', color: '#aaaaaa' },
        ticks: { color: '#aaaaaa' },
        grid: { color: '#1e2a3a' }
      }
    }
  };

  public lineChartLegend = true;

  constructor(
    private simulationService: SimulationService,
    private fb: FormBuilder,
    private snackBar: MatSnackBar,
    private http: HttpClient,
    private exportService: ExportService
  ) {
    this.simForm = this.fb.group({
      modulation: ['QPSK', Validators.required],
      snrMin: [-5, [Validators.required, Validators.min(-15), Validators.max(10)]],
      snrMax: [20, [Validators.required, Validators.min(10), Validators.max(40)]],
      codeRate: [0.5, [Validators.required, Validators.min(0.1), Validators.max(1.0)]],
      snrSteps: [25, [Validators.required, Validators.min(10), Validators.max(50)]]
    }, { validators: snrRangeValidator });

    this.beamForm = this.fb.group({
      numAntennas: [16, Validators.required],
      steeringAngle: [0, [Validators.required, Validators.min(-90), Validators.max(90)]],
      frequencyGhz: [28.0, Validators.required],
      arraySpacing: [0.5, [Validators.required, Validators.min(0.3), Validators.max(1.0)]]
    });

    this.modForm = this.fb.group({
      snrMin: [-5, [Validators.required, Validators.min(-15), Validators.max(10)]],
      snrMax: [25, [Validators.required, Validators.min(15), Validators.max(40)]],
      snrSteps: [50, [Validators.required, Validators.min(20), Validators.max(100)]]
    }, { validators: snrRangeValidator });

    this.capForm = this.fb.group({
      snrMin: [-10, [Validators.required, Validators.min(-20), Validators.max(10)]],
      snrMax: [30, [Validators.required, Validators.min(15), Validators.max(50)]],
      snrSteps: [50, [Validators.required, Validators.min(20), Validators.max(100)]],
      bw10: [true],
      bw100: [true],
      bw400: [true],
      bw1000: [true]
    }, { validators: snrRangeValidator });
  }

  ngOnInit(): void {
    this.fetchSimulationData();
    this.loadApiKeys();
  }

  fetchSimulationData(): void {
    this.isLoading = true;
    this.loadError = false;
    this.simulationService.getDemoSimulation().subscribe({
      next: (result) => {
        this.updateChartData(result);
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Failed to load simulation data', err);
        this.loadError = true;
        this.isLoading = false;
      }
    });
  }

  runSimulation(): void {
    if (this.simForm.invalid) return;

    this.isSimulating = true;
    const formVal = this.simForm.value;

    let modOrder = 4, bitsPerSym = 2;
    if (formVal.modulation === 'BPSK')  { modOrder = 2;  bitsPerSym = 1; }
    else if (formVal.modulation === 'QPSK')  { modOrder = 4;  bitsPerSym = 2; }
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
        console.error('Simulation failed', err);
        this.isSimulating = false;
        this.showError('Simulation failed. Please try again.');
      }
    });
  }

  private updateChartData(result: SimulationResult): void {
    this.simulationData = result;
    this.lineChartData = {
      ...this.lineChartData,
      labels: result.snr_db,
      datasets: [
        { ...this.lineChartData.datasets[0], data: result.ber_theoretical },
        { ...this.lineChartData.datasets[1], data: result.ber_simulated }
      ]
    };
    if (this.lineChartOptions?.plugins?.title) {
      this.lineChartOptions.plugins.title.text = `BER vs SNR — ${result.modulation}`;
    }
  }

  runBeamSimulation(): void {
    if (this.beamForm.invalid) return;
    this.isBeamSimulating = true;

    const vals = this.beamForm.value;
    const req: BeamPatternRequest = {
      num_antennas: vals.numAntennas,
      steering_angle: vals.steeringAngle,
      frequency_ghz: vals.frequencyGhz,
      array_spacing: vals.arraySpacing
    };

    this.simulationService.runBeamPattern(req).subscribe({
      next: (res) => {
        this.beamData = res;
        this.radarChartData = {
          labels: res.angles.map(a => a + '°'),
          datasets: [{ ...this.radarChartData.datasets[0], data: res.pattern_db }]
        };
        if (this.radarChartOptions?.plugins?.title) {
          this.radarChartOptions.plugins.title.text = `Beam Pattern — ${res.num_antennas} Element ULA at ${res.frequency_ghz} GHz`;
        }
        this.radarChartData = { ...this.radarChartData };
        this.isBeamSimulating = false;
      },
      error: (err) => {
        console.error(err);
        this.isBeamSimulating = false;
        this.showError('Failed to generate beam pattern. Please try again.');
      }
    });
  }

  runModulationComparison(): void {
    if (this.modForm.invalid) return;
    this.isModSimulating = true;

    const req: ModulationComparisonRequest = {
      snr_min: this.modForm.value.snrMin,
      snr_max: this.modForm.value.snrMax,
      snr_steps: this.modForm.value.snrSteps
    };

    this.simulationService.runModulationComparison(req).subscribe({
      next: (res) => {
        this.modData = res;
        this.modChartData = {
          ...this.modChartData,
          labels: res.snr_db,
          datasets: [
            { ...this.modChartData.datasets[0], data: res.bpsk },
            { ...this.modChartData.datasets[1], data: res.qpsk },
            { ...this.modChartData.datasets[2], data: res.qam16 },
            { ...this.modChartData.datasets[3], data: res.qam64 }
          ]
        };
        this.isModSimulating = false;
      },
      error: (err) => {
        console.error(err);
        this.isModSimulating = false;
        this.showError('Modulation comparison failed. Please try again.');
      }
    });
  }

  runChannelCapacity(): void {
    if (this.capForm.invalid) return;
    this.isCapSimulating = true;

    const bws: number[] = [];
    if (this.capForm.value.bw10) bws.push(10);
    if (this.capForm.value.bw100) bws.push(100);
    if (this.capForm.value.bw400) bws.push(400);
    if (this.capForm.value.bw1000) bws.push(1000);

    const req: ChannelCapacityRequest = {
      snr_min: this.capForm.value.snrMin,
      snr_max: this.capForm.value.snrMax,
      snr_steps: this.capForm.value.snrSteps,
      bandwidths_mhz: bws
    };

    this.simulationService.runChannelCapacity(req).subscribe({
      next: (res: ChannelCapacityResult) => {
        this.capData = res;
        
        // build capacity datasets
        const ds = res.capacity_curves.map(curve => ({
          data: curve.capacity_gbps,
          label: curve.label,
          fill: false,
          tension: 0.1,
          borderColor: curve.color_hint,
          pointRadius: 0,
          borderWidth: 2
        }));

        this.capChartData = {
          labels: res.snr_db,
          datasets: ds
        };

        this.spectralChartData = {
          labels: res.snr_db,
          datasets: [{
            data: res.spectral_efficiency,
            label: 'Spectral Eff',
            fill: true,
            tension: 0.1,
            backgroundColor: 'rgba(100, 255, 218, 0.1)',
            borderColor: '#64ffda',
            pointRadius: 0,
            borderWidth: 2
          }]
        };

        this.isCapSimulating = false;
      },
      error: (err) => {
        console.error(err);
        this.isCapSimulating = false;
        this.showError('Channel capacity generation failed. Please try again.');
      }
    });
  }

  private showError(msg: string): void {
    this.snackBar.open(msg, 'Dismiss', {
      duration: 5000,
      panelClass: ['error-snackbar'],
      verticalPosition: 'bottom',
      horizontalPosition: 'center'
    });
  }

  loadApiKeys() {
    this.http.get<any[]>(`${environment.apiUrl}/api/keys/my-keys`).subscribe({
      next: (keys) => this.apiKeys = keys,
      error: (err) => console.error('Error loading API keys', err)
    });
  }

  generateApiKey() {
    if (!this.newKeyDescription) return;
    this.http.post<any>(`${environment.apiUrl}/api/keys/generate`, { description: this.newKeyDescription }).subscribe({
      next: (res) => {
        this.snackBar.open('Generated new API key successfully.', 'Close', { duration: 3000 });
        this.newKeyDescription = '';
        this.loadApiKeys();
      },
      error: (err) => console.error('Error generating API key', err)
    });
  }

  revokeApiKey(keyValue: string) {
    this.isRevoking = true;
    this.http.delete(`${environment.apiUrl}/api/keys/${keyValue}`).subscribe({
      next: () => {
        this.snackBar.open('API key revoked.', 'Close', { duration: 3000 });
        this.loadApiKeys();
        this.isRevoking = false;
      },
      error: (err) => {
        console.error('Error revoking API key', err);
        this.isRevoking = false;
      }
    });
  }

  // ─────────────────────────────────────────────────────────────────────────
  // EXPORT METHODS
  // ─────────────────────────────────────────────────────────────────────────

  downloadPng(chartType: 'ber' | 'beam' | 'mod' | 'cap' | 'spectral'): void {
    let chart: BaseChartDirective | undefined;
    let filename = '';

    switch (chartType) {
      case 'ber':
        chart = this.berChart;
        filename = this.exportService.berFilename(this.simulationData?.modulation || 'qpsk', 'png');
        break;
      case 'beam':
        chart = this.beamChart;
        filename = this.exportService.beamFilename(this.beamData?.num_antennas || 16, 'png');
        break;
      case 'mod':
        chart = this.modChart;
        filename = this.exportService.modFilename('png');
        break;
      case 'cap':
        chart = this.capChart;
        filename = this.exportService.capFilename('png');
        break;
      case 'spectral':
        chart = this.spectralChart;
        filename = `sionna-spectral-efficiency-${new Date().toISOString().slice(0, 10)}.png`;
        break;
    }

    if (chart?.chart) {
      this.exportService.downloadCanvasPng(chart.chart.canvas, filename);
    }
  }

  downloadCsv(type: 'ber' | 'beam' | 'mod' | 'cap'): void {
    let csv = '';
    let filename = '';

    switch (type) {
      case 'ber':
        csv = this.exportService.generateBerCsv(this.simulationData);
        filename = this.exportService.berFilename(this.simulationData?.modulation || 'qpsk', 'csv');
        break;
      case 'beam':
        csv = this.exportService.generateBeamCsv(this.beamData);
        filename = this.exportService.beamFilename(this.beamData?.num_antennas || 16, 'csv');
        break;
      case 'mod':
        csv = this.exportService.generateModComparisonCsv(this.modData);
        filename = this.exportService.modFilename('csv');
        break;
      case 'cap':
        csv = this.exportService.generateCapacityCsv(this.capData);
        filename = this.exportService.capFilename('csv');
        break;
    }
    this.exportService.downloadCSV(filename, csv);
  }

  downloadJson(type: 'ber' | 'beam' | 'mod' | 'cap'): void {
    let data: any;
    let filename = '';
    let simType = '';

    switch (type) {
      case 'ber':
        data = this.simulationData;
        filename = this.exportService.berFilename(data?.modulation || 'qpsk', 'json');
        simType = 'BER_SNR';
        break;
      case 'beam':
        data = this.beamData;
        filename = this.exportService.beamFilename(data?.num_antennas || 16, 'json');
        simType = 'BEAM_PATTERN';
        break;
      case 'mod':
        data = this.modData;
        filename = this.exportService.modFilename('json');
        simType = 'MOD_COMPARISON';
        break;
      case 'cap':
        data = this.capData;
        filename = this.exportService.capFilename('json');
        simType = 'CHANNEL_CAPACITY';
        break;
    }
    const wrapped = this.exportService.wrapWithMetadata(simType, data);
    this.exportService.downloadJSON(filename, wrapped);
  }

  copyJson(type: 'ber' | 'beam' | 'mod' | 'cap'): void {
    let data: any;
    let simType = '';

    switch (type) {
      case 'ber': data = this.simulationData; simType = 'BER_SNR'; break;
      case 'beam': data = this.beamData; simType = 'BEAM_PATTERN'; break;
      case 'mod': data = this.modData; simType = 'MOD_COMPARISON'; break;
      case 'cap': data = this.capData; simType = 'CHANNEL_CAPACITY'; break;
    }

    const wrapped = this.exportService.wrapWithMetadata(simType, data);
    this.exportService.copyToClipboard(wrapped).then(() => {
      this.snackBar.open('JSON copied to clipboard', 'OK', { duration: 2000 });
    });
  }
}
