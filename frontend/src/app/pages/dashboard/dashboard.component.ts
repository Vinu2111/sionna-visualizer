import { Component, OnInit, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { MatCardModule } from '@angular/material/card';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import { MatSliderModule } from '@angular/material/slider';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { ReactiveFormsModule, FormsModule, FormBuilder, FormGroup, Validators, AbstractControl, ValidationErrors } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { environment } from '../../../environments/environment';
import { MatTabsModule } from '@angular/material/tabs';
import { MatTableModule } from '@angular/material/table';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatIconModule } from '@angular/material/icon';
import { MatTooltipModule } from '@angular/material/tooltip';
import { BaseChartDirective, NgChartsModule } from 'ng2-charts';
import { ExportService } from '../../services/export.service';
import { ChartConfiguration, ChartOptions, ChartData } from 'chart.js';
import { SimulationService } from '../../services/simulation.service';
import {
  SimulationResult, SimulationRequest,
  BeamPatternResult, BeamPatternRequest,
  ModulationComparisonRequest, ModulationComparisonResult,
  ChannelCapacityRequest, ChannelCapacityResult,
  PathLossRequest, PathLossResult, PathDto,
  SimulationEstimateRequest, SimulationEstimateResult,
  RayDirectionRequest, RayDirectionResult, RayDirectionPath, RayDirectionSummary,
  MeasurementOverlayRequest, MeasurementOverlayResult, ComparisonPoint, MeasurementPointInput,
  SinrSteeringRequest, SinrSteeringResult
} from '../../models/simulation-result.model';
import { ColormapSelectorComponent } from '../../components/colormap-selector/colormap-selector.component';
import { UeTrajectoryComponent } from '../../components/ue-trajectory/ue-trajectory.component';
import { AnomalyDetectionComponent } from '../../components/anomaly-detection/anomaly-detection.component';

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
    MatButtonToggleModule,
    MatSliderModule,
    MatCheckboxModule,
    MatTabsModule,
    MatTableModule,
    MatSnackBarModule,
    MatIconModule,
    MatTooltipModule,
    ReactiveFormsModule,
    FormsModule,
    RouterModule,
    ColormapSelectorComponent,
    UeTrajectoryComponent,
    AnomalyDetectionComponent
  ],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss'
})
export class DashboardComponent implements OnInit {
  simulationData: SimulationResult | null = null;
  beamData: BeamPatternResult | null = null;
  modData: ModulationComparisonResult | null = null;
  capData: ChannelCapacityResult | null = null;
  pathLossData: PathLossResult | null = null;
  rayData: RayDirectionResult | null = null;
  isLoading = true;
  isSimulating = false;
  isBeamSimulating = false;
  isModSimulating = false;
  isCapSimulating = false;
  isPathLossSimulating = false;
  isRaySimulating = false;
  loadError = false;
  retryAttempt = 0;
  retryMessage = '';

  calibData: MeasurementOverlayResult | null = null;
  sinrData: SinrSteeringResult | null = null;
  isCalibSimulating = false;
  isSinrSimulating = false;

  // ID of the most recently run BER simulation — drives the anomaly panel
  lastSimulationId: number | null = null;
  anomalyAutoAnalyze = false;
  
  // Calibration specific state
  calibInputMode: 'manual' | 'csv' = 'manual';
  measurementRows: MeasurementPointInput[] = [
    { snr_db: -5, ber_measured: 0.150, location: '' },
    { snr_db: 0, ber_measured: 0.080, location: '' },
    { snr_db: 5, ber_measured: 0.030, location: '' },
    { snr_db: 10, ber_measured: 0.005, location: '' }
  ];
  csvLoadedMsg = '';

  // SINR specific state
  sinrSteeringAngles: number[] = [-60, -45, -30, -15, 0, 15, 30, 45, 60];
  newAngleInput: number | null = null;


  @ViewChild('berChart') berChart?: BaseChartDirective;
  @ViewChild('beamChart') beamChart?: BaseChartDirective;
  @ViewChild('modChart') modChart?: BaseChartDirective;
  @ViewChild('capChart') capChart?: BaseChartDirective;
  @ViewChild('spectralChart') spectralChart?: BaseChartDirective;
  @ViewChild('plBarChart') plBarChart?: BaseChartDirective;
  @ViewChild('plScatterChart') plScatterChart?: BaseChartDirective;
  @ViewChild('plDelayChart') plDelayChart?: BaseChartDirective;
  @ViewChild('rayDepChart') rayDepChart?: BaseChartDirective;
  @ViewChild('rayArrChart') rayArrChart?: BaseChartDirective;

  @ViewChild('calibOverlayChart') calibOverlayChart?: BaseChartDirective;
  @ViewChild('calibErrorChart') calibErrorChart?: BaseChartDirective;
  @ViewChild('sinrLineChart') sinrLineChart?: BaseChartDirective;
  @ViewChild('sinrPolarChart') sinrPolarChart?: BaseChartDirective;


  apiKeys: any[] = [];
  newKeyDescription = '';
  isRevoking = false;

  simForm: FormGroup;
  beamForm: FormGroup;
  modForm: FormGroup;
  capForm: FormGroup;
  pathLossForm: FormGroup;
  rayForm: FormGroup;

  calibForm: FormGroup;
  sinrForm: FormGroup;


  // ─── Estimate state shared across all tabs ──────────────────────────────
  estimate: SimulationEstimateResult | null = null;
  isEstimating = false;
  estimateTab: string | null = null;  // tracks which tab owns the current estimate

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
      borderColor: '#8b5cf6',
      backgroundColor: 'rgba(139, 92, 246, 0.1)',
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
        borderColor: '#8b5cf6', pointBackgroundColor: '#8b5cf6',
        pointBorderColor: '#fff', pointRadius: 0
      },
      {
        data: [], label: 'Simulated BER',
        fill: false, tension: 0.1,
        borderColor: '#f43f5e', pointBackgroundColor: '#f43f5e',
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
      { data: [], label: 'QPSK',  fill: false, tension: 0.1, borderColor: '#8b5cf6', pointRadius: 0, borderWidth: 2 },
      { data: [], label: '16QAM', fill: false, tension: 0.1, borderColor: '#f59e0b', pointRadius: 0, borderWidth: 2 },
      { data: [], label: '64QAM', fill: false, tension: 0.1, borderColor: '#f43f5e', pointRadius: 0, borderWidth: 2 }
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

  // ─────────────────────────────────────────────────────────────────────────
  // Chart Config: Path Loss (Bar - per Ray)
  // ─────────────────────────────────────────────────────────────────────────
  public plBarChartData: ChartConfiguration<'bar'>['data'] = {
    labels: [],
    datasets: [{ data: [], label: 'Path Loss (dB)', backgroundColor: [] }]
  };

  public plBarChartOptions: ChartOptions<'bar'> = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: { display: false },
      title: { display: true, text: 'Path Loss per Ray', color: '#eeeeee' }
    },
    scales: {
      y: { title: { display: true, text: 'Path Loss (dB)', color: '#aaaaaa' }, ticks: { color: '#aaaaaa' }, grid: { color: '#1e2a3a' } },
      x: { title: { display: true, text: 'Path ID', color: '#aaaaaa' }, ticks: { color: '#aaaaaa' }, grid: { display: false } }
    }
  };

  // ─────────────────────────────────────────────────────────────────────────
  // Chart Config: Path Loss (Scatter vs Distance)
  // ─────────────────────────────────────────────────────────────────────────
  public plScatterChartData: ChartConfiguration<'scatter'>['data'] = {
    datasets: []
  };

  public plScatterChartOptions: ChartOptions<'scatter'> = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: { labels: { color: '#eeeeee' } },
      title: { display: true, text: 'Path Loss vs. Distance', color: '#eeeeee' }
    },
    scales: {
      y: { title: { display: true, text: 'Path Loss (dB)', color: '#aaaaaa' }, ticks: { color: '#aaaaaa' }, grid: { color: '#1e2a3a' } },
      x: { title: { display: true, text: 'Distance (m)', color: '#aaaaaa' }, ticks: { color: '#aaaaaa' }, grid: { color: '#1e2a3a' } }
    }
  };

  // ─────────────────────────────────────────────────────────────────────────
  // Chart Config: Path Loss (Delay)
  // ─────────────────────────────────────────────────────────────────────────
  public plDelayChartData: ChartConfiguration<'bar'>['data'] = {
    labels: [],
    datasets: [{ data: [], label: 'Delay (ns)', backgroundColor: [] }]
  };

  public plDelayChartOptions: ChartOptions<'bar'> = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: { display: false },
      title: { display: true, text: 'Propagation Delay per Path', color: '#eeeeee' }
    },
    scales: {
      y: { title: { display: true, text: 'Delay (ns)', color: '#aaaaaa' }, ticks: { color: '#aaaaaa' }, grid: { color: '#1e2a3a' } },
      x: { title: { display: true, text: 'Path ID', color: '#aaaaaa' }, ticks: { color: '#aaaaaa' }, grid: { display: false } }
    }
  };

  // ─────────────────────────────────────────────────────────────────────────
  // Chart Config: Polar Area (Ray Departure)
  // ─────────────────────────────────────────────────────────────────────────
  public rayDepChartData: ChartConfiguration<'polarArea'>['data'] = {
    labels: [],
    datasets: [{ data: [], backgroundColor: [], borderWidth: 1 }]
  };
  public rayDepChartOptions: ChartOptions<'polarArea'> = {
    responsive: true, maintainAspectRatio: false,
    plugins: { legend: { display: false }, title: { display: true, text: 'Departure Angles from TX', color: '#eeeeee' } },
    scales: { r: { grid: { color: '#1e2a3a' }, ticks: { display: false }, angleLines: { color: '#1e2a3a' } } }
  };

  // ─────────────────────────────────────────────────────────────────────────
  // Chart Config: Polar Area (Ray Arrival)
  // ─────────────────────────────────────────────────────────────────────────
  public rayArrChartData: ChartConfiguration<'polarArea'>['data'] = {
    labels: [],
    datasets: [{ data: [], backgroundColor: [], borderWidth: 1 }]
  };
  public rayArrChartOptions: ChartOptions<'polarArea'> = {
    responsive: true, maintainAspectRatio: false,
    plugins: { legend: { display: false }, title: { display: true, text: 'Arrival Angles at RX', color: '#eeeeee' } },
    scales: { r: { grid: { color: '#1e2a3a' }, ticks: { display: false }, angleLines: { color: '#1e2a3a' } } }
  };


  // ─────────────────────────────────────────────────────────────────────────
  // Chart Config: Calibration Overlay
  // ─────────────────────────────────────────────────────────────────────────
  public calibOverlayChartData: ChartConfiguration<'line'>['data'] = {
    labels: [],
    datasets: [
      { data: [], label: 'Theoretical AWGN', fill: false, tension: 0.1, borderDash: [5, 5], borderColor: '#8b5cf6', pointRadius: 0 },
      { data: [], label: 'Measured BER', fill: false, tension: 0, borderColor: '#f43f5e', backgroundColor: '#f43f5e', showLine: false, pointRadius: 5, pointStyle: 'rect' }
    ]
  };
  public calibOverlayChartOptions: ChartOptions<'line'> = {
    responsive: true, maintainAspectRatio: false,
    plugins: { legend: { labels: { color: '#eeeeee' } }, title: { display: true, text: 'Expected vs Actual BER', color: '#eeeeee' } },
    scales: {
      y: { type: 'logarithmic', title: { display: true, text: 'BER', color: '#aaaaaa' }, ticks: { color: '#aaaaaa' }, grid: { color: '#1e2a3a' } },
      x: { title: { display: true, text: 'SNR (dB)', color: '#aaaaaa' }, ticks: { color: '#aaaaaa' }, grid: { color: '#1e2a3a' } }
    }
  };

  // ─────────────────────────────────────────────────────────────────────────
  // Chart Config: Calibration Error (Bar)
  // ─────────────────────────────────────────────────────────────────────────
  public calibErrorChartData: ChartConfiguration<'bar'>['data'] = {
    labels: [],
    datasets: [{ data: [], label: 'Relative Error (%)', backgroundColor: [] }]
  };
  public calibErrorChartOptions: ChartOptions<'bar'> = {
    responsive: true, maintainAspectRatio: false,
    plugins: { legend: { display: false }, title: { display: true, text: 'Relative Error per Point', color: '#eeeeee' } },
    scales: {
      y: { title: { display: true, text: 'Error (%)', color: '#aaaaaa' }, ticks: { color: '#aaaaaa' }, grid: { color: '#1e2a3a' } },
      x: { title: { display: true, text: 'Measurement SNR (dB)', color: '#aaaaaa' }, ticks: { color: '#aaaaaa' }, grid: { display: false } }
    }
  };

  // ─────────────────────────────────────────────────────────────────────────
  // Chart Config: SINR Line
  // ─────────────────────────────────────────────────────────────────────────
  public sinrLineChartData: ChartConfiguration<'line'>['data'] = {
    labels: [],
    datasets: [
      { data: [], label: 'SINR (dB)', fill: false, tension: 0.1, borderColor: '#8b5cf6', pointBackgroundColor: '#8b5cf6' },
      { data: [], label: 'Array Gain (dB)', fill: false, tension: 0.1, borderDash: [5, 5], borderColor: '#aaaaaa', pointRadius: 0 }
    ]
  };
  public sinrLineChartOptions: ChartOptions<'line'> = {
    responsive: true, maintainAspectRatio: false,
    plugins: { legend: { labels: { color: '#eeeeee' } }, title: { display: true, text: 'Beamforming Performance', color: '#eeeeee' } },
    scales: {
      y: { title: { display: true, text: 'dB', color: '#aaaaaa' }, ticks: { color: '#aaaaaa' }, grid: { color: '#1e2a3a' } },
      x: { title: { display: true, text: 'Steering Angle (\xb0)', color: '#aaaaaa' }, ticks: { color: '#aaaaaa' }, grid: { color: '#1e2a3a' } }
    }
  };

  // ─────────────────────────────────────────────────────────────────────────
  // Chart Config: SINR Polar
  // ─────────────────────────────────────────────────────────────────────────
  public sinrPolarChartData: ChartConfiguration<'radar'>['data'] = {
    labels: [],
    datasets: [
      { data: [], label: 'Beam Pattern at Optimal Angle', borderColor: '#8b5cf6', backgroundColor: 'rgba(139, 92, 246, 0.2)', borderWidth: 2, pointRadius: 0 },
      { data: [], label: 'Interference Direction', borderColor: '#f43f5e', backgroundColor: 'rgba(244, 63, 94, 0.5)', borderWidth: 3, pointStyle: 'triangle', pointRadius: 5 }
    ]
  };
  public sinrPolarChartOptions: ChartOptions<'radar'> = {
    responsive: true, maintainAspectRatio: false,
    plugins: { title: { display: true, text: 'Spatial Filtering', color: '#eeeeee' }, legend: { labels: { color: '#eeeeee' } } },
    scales: { r: { angleLines: { color: 'rgba(255, 255, 255, 0.1)' }, grid: { color: 'rgba(255, 255, 255, 0.1)' }, pointLabels: { color: '#eeeeee', font: { size: 10 } }, ticks: { display: false } } }
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
      snrSteps: [25, [Validators.required, Validators.min(10), Validators.max(50)]],
      colormap: ['default']
    }, { validators: snrRangeValidator });

    this.beamForm = this.fb.group({
      numAntennas: [16, Validators.required],
      steeringAngle: [0, [Validators.required, Validators.min(-90), Validators.max(90)]],
      frequencyGhz: [28.0, Validators.required],
      arraySpacing: [0.5, [Validators.required, Validators.min(0.3), Validators.max(1.0)]],
      colormap: ['default']
    });

    this.calibForm = this.fb.group({
      simulationType: ['AWGN', Validators.required],
      environment: ['urban', Validators.required],
      frequencyGhz: [28, Validators.required]
    });

    this.sinrForm = this.fb.group({
      numAntennas: [16, Validators.required],
      frequencyGhz: [28, [Validators.required, Validators.min(1), Validators.max(100)]],
      interferenceAngle: [45, [Validators.required, Validators.min(-90), Validators.max(90)]],
      signalPower: [0, Validators.required],
      interferencePower: [-10, Validators.required],
      colormap: ['default']
    });


    this.modForm = this.fb.group({
      snrMin: [-5, [Validators.required, Validators.min(-15), Validators.max(10)]],
      snrMax: [25, [Validators.required, Validators.min(15), Validators.max(40)]],
      snrSteps: [50, [Validators.required, Validators.min(20), Validators.max(100)]],
      colormap: ['default']
    }, { validators: snrRangeValidator });

    this.capForm = this.fb.group({
      snrMin: [-10, [Validators.required, Validators.min(-20), Validators.max(10)]],
      snrMax: [30, [Validators.required, Validators.min(15), Validators.max(50)]],
      snrSteps: [50, [Validators.required, Validators.min(20), Validators.max(100)]],
      bw10: [true],
      bw100: [true],
      bw400: [true],
      bw1000: [true],
      colormap: ['default']
    }, { validators: snrRangeValidator });

    this.pathLossForm = this.fb.group({
      num_paths: [8, Validators.required],
      frequency_ghz: [28, [Validators.required, Validators.min(1), Validators.max(100)]],
      environment: ['urban', Validators.required],
      colormap: ['default']
    });

    this.calibForm = this.fb.group({
      simulationType: ['AWGN', Validators.required],
      environment: ['urban', Validators.required],
      frequencyGhz: [28, Validators.required]
    });

    this.sinrForm = this.fb.group({
      numAntennas: [16, Validators.required],
      frequencyGhz: [28, [Validators.required, Validators.min(1), Validators.max(100)]],
      interferenceAngle: [45, [Validators.required, Validators.min(-90), Validators.max(90)]],
      signalPower: [0, Validators.required],
      interferencePower: [-10, Validators.required],
      colormap: ['default']
    });


    this.rayForm = this.fb.group({
      num_paths: [8, Validators.required],
      frequency_ghz: [28, [Validators.required, Validators.min(1), Validators.max(100)]],
      environment: ['urban', Validators.required],
      tx_x: [0, Validators.required],
      tx_y: [0, Validators.required],
      tx_h: [10, Validators.required],
      rx_x: [100, Validators.required],
      rx_y: [50, Validators.required],
      rx_h: [1.5, Validators.required],
      colormap: ['default']
    });

    this.calibForm = this.fb.group({
      simulationType: ['AWGN', Validators.required],
      environment: ['urban', Validators.required],
      frequencyGhz: [28, Validators.required]
    });

    this.sinrForm = this.fb.group({
      numAntennas: [16, Validators.required],
      frequencyGhz: [28, [Validators.required, Validators.min(1), Validators.max(100)]],
      interferenceAngle: [45, [Validators.required, Validators.min(-90), Validators.max(90)]],
      signalPower: [0, Validators.required],
      interferencePower: [-10, Validators.required],
      colormap: ['default']
    });

  }

  ngOnInit(): void {
    this.fetchSimulationData();
    this.loadApiKeys();
  }

  fetchSimulationData(): void {
    this.isLoading = true;
    this.loadError = false;
    this.retryAttempt = 0;
    this.tryFetchWithRetry();
  }

  private tryFetchWithRetry(): void {
    const MAX_RETRIES = 3;
    this.retryAttempt++;
    this.retryMessage = this.retryAttempt > 1
      ? `Connecting to simulation engine... (attempt ${this.retryAttempt} of ${MAX_RETRIES})`
      : 'Connecting to simulation engine...';

    this.simulationService.getDemoSimulation().subscribe({
      next: (result: any) => {
        // If engine is warming up, it returns { status: 'demo_unavailable' }
        if (result && result.status === 'demo_unavailable') {
          if (this.retryAttempt < MAX_RETRIES) {
            setTimeout(() => this.tryFetchWithRetry(), 3000);
          } else {
            this.loadError = true;
            this.isLoading = false;
          }
          return;
        }
        this.updateChartData(result);
        this.retryMessage = '';
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Failed to load simulation data', err);
        if (this.retryAttempt < MAX_RETRIES) {
          setTimeout(() => this.tryFetchWithRetry(), 3000);
        } else {
          this.loadError = true;
          this.isLoading = false;
        }
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
      snr_steps: formVal.snrSteps,
      colormap: formVal.colormap
    };

    this.simulationService.runNewSimulation(request).subscribe({
      next: (result) => {
        this.updateChartData(result);
        this.isSimulating = false;
        // Store the simulation ID so the anomaly panel can target it
        // The panel watches for lastSimulationId changes and auto-runs analysis
        if (result.id) {
          this.lastSimulationId = result.id;
          this.anomalyAutoAnalyze = true;
          // Reset flag after one cycle so future manual re-runs work correctly
          setTimeout(() => this.anomalyAutoAnalyze = false, 500);
        }
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
        { 
          ...this.lineChartData.datasets[0], 
          data: result.ber_theoretical,
          borderColor: result.colors?.[0] || '#8b5cf6',
          pointBackgroundColor: result.colors?.[0] || '#8b5cf6'
        },
        { 
          ...this.lineChartData.datasets[1], 
          data: result.ber_simulated,
          borderColor: result.colors?.[1] || '#f43f5e',
          pointBackgroundColor: result.colors?.[1] || '#f43f5e'
        }
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
      array_spacing: vals.arraySpacing,
      colormap: vals.colormap
    };

    this.simulationService.runBeamPattern(req).subscribe({
      next: (res) => {
        this.beamData = res;
        this.radarChartData = {
          labels: res.angles.map(a => a + '°'),
          datasets: [{ 
            ...this.radarChartData.datasets[0], 
            data: res.pattern_db,
            borderColor: res.colors?.[0] || '#8b5cf6',
            backgroundColor: res.colors?.[0] ? `${res.colors[0]}1a` : 'rgba(139, 92, 246, 0.1)'
          }]
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
      snr_steps: this.modForm.value.snrSteps,
      colormap: this.modForm.value.colormap
    };

    this.simulationService.runModulationComparison(req).subscribe({
      next: (res) => {
        this.modData = res;
        this.modChartData = {
          ...this.modChartData,
          labels: res.snr_db,
          datasets: [
            { ...this.modChartData.datasets[0], data: res.bpsk, borderColor: res.colors?.[0] || '#ffffff' },
            { ...this.modChartData.datasets[1], data: res.qpsk, borderColor: res.colors?.[1] || '#8b5cf6' },
            { ...this.modChartData.datasets[2], data: res.qam16, borderColor: res.colors?.[2] || '#f59e0b' },
            { ...this.modChartData.datasets[3], data: res.qam64, borderColor: res.colors?.[3] || '#f43f5e' }
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
      bandwidths_mhz: bws,
      colormap: this.capForm.value.colormap
    };

    this.simulationService.runChannelCapacity(req).subscribe({
      next: (res: ChannelCapacityResult) => {
        this.capData = res;
        
        // build capacity datasets
        const ds = res.capacity_curves.map((curve, i) => ({
          data: curve.capacity_gbps,
          label: curve.label,
          fill: false,
          tension: 0.1,
          borderColor: res.colors?.[i] || curve.color_hint,
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
            backgroundColor: 'rgba(139, 92, 246, 0.1)',
            borderColor: res.colors?.[res.colors.length - 1] || '#8b5cf6',
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

  showSuccess(msg: string): void {
    this.snackBar.open(msg, 'Close', { duration: 3000, panelClass: ['success-snackbar'] });
  }

  dismissEstimate(): void {
    this.estimate = null;
    this.estimateTab = null;
  }

  runEstimateFor(tab: string, params: Record<string, any>): void {
    this.isEstimating = true;
    this.estimateTab = tab;
    this.estimate = null;

    const req: SimulationEstimateRequest = {
      simulation_type: tab.toUpperCase(),
      parameters: params
    };

    this.simulationService.runEstimate(req).subscribe({
      next: (res) => {
        this.estimate = res;
        this.isEstimating = false;
      },
      error: (err) => {
        console.error('Estimate failed', err);
        this.showError('Could not fetch estimate. Please try again.');
        this.isEstimating = false;
      }
    });
  }

  /** Builds params for each tab and delegates to runEstimateFor() */
  estimateAwgn(): void {
    const v = this.simForm.value;
    const modMap: Record<number, string> = { 2: 'BPSK', 4: 'QPSK', 16: '16QAM', 64: '64QAM' };
    this.runEstimateFor('AWGN', {
      modulation: modMap[v.modulationOrder] ?? 'QPSK',
      snr_steps: v.snrSteps
    });
  }

  estimateBeam(): void {
    const v = this.beamForm.value;
    this.runEstimateFor('BEAM_PATTERN', {
      num_antennas: v.numAntennas,
      frequency_ghz: v.frequencyGhz
    });
  }

  estimateMod(): void {
    const v = this.modForm.value;
    this.runEstimateFor('MODULATION_COMPARISON', { snr_steps: v.snrSteps });
  }

  estimateCap(): void {
    const v = this.capForm.value;
    this.runEstimateFor('CHANNEL_CAPACITY', { snr_steps: v.snrSteps });
  }

  estimatePathLoss(): void {
    const v = this.pathLossForm.value;
    this.runEstimateFor('PATH_LOSS', { num_paths: v.num_paths });
  }

  estimateRay(): void {
    const v = this.rayForm.value;
    this.runEstimateFor('RAY_DIRECTIONS', { num_paths: v.num_paths });
  }

  /** Map complexity_color string to a CSS hex color */
  complexityColor(color: string): string {
    const map: Record<string, string> = {
      green: '#4caf50',
      yellow: '#ffeb3b',
      orange: '#ff9800',
      red: '#f44336'
    };
    return map[color] ?? '#ffffff';
  }

  runPathLoss(): void {
    if (this.pathLossForm.invalid) return;

    this.isPathLossSimulating = true;
    const vals = this.pathLossForm.value;

    const req: PathLossRequest = {
      num_paths: Number(vals.num_paths),
      frequency_ghz: Number(vals.frequency_ghz),
      environment: vals.environment,
      colormap: vals.colormap
    };

    this.simulationService.runPathLoss(req).subscribe({
      next: (res) => {
        this.pathLossData = res;
        
        // Update Bar Chart (Path Loss per Ray)
        this.plBarChartData.labels = res.paths.map(p => `Path ${p.path_id}`);
        this.plBarChartData.datasets[0].data = res.paths.map(p => p.path_loss_db);
        this.plBarChartData.datasets[0].backgroundColor = res.colors || res.paths.map(p => 
          p.path_type === 'LOS' ? 'rgba(139, 92, 246, 0.8)' : 'rgba(244, 63, 94, 0.8)'
        );
        this.plBarChart?.update();

        // Update Scatter Chart (Path Loss vs Distance)
        this.plScatterChartData.datasets = [
          {
            label: 'LOS Paths',
            data: res.paths.filter(p => p.path_type === 'LOS').map(p => ({ x: p.distance_m, y: p.path_loss_db })),
            backgroundColor: res.colors?.[0] || '#8b5cf6',
            pointRadius: 6
          },
          {
            label: 'NLOS Paths',
            data: res.paths.filter(p => p.path_type === 'NLOS').map(p => ({ x: p.distance_m, y: p.path_loss_db })),
            backgroundColor: res.colors?.[1] || '#f43f5e',
            pointRadius: 6
          }
        ];
        this.plScatterChart?.update();

        // Update Delay Chart (Delay per Path)
        this.plDelayChartData.labels = res.paths.map(p => `Path ${p.path_id}`);
        this.plDelayChartData.datasets[0].data = res.paths.map(p => p.delay_ns);
        this.plDelayChartData.datasets[0].backgroundColor = res.colors || '#f59e0b';
        this.plDelayChart?.update();

        this.isPathLossSimulating = false;
        this.showSuccess('Path Loss simulation completed');
      },
      error: (err) => {
        console.error(err);
        this.showError('Path Loss simulation failed');
        this.isPathLossSimulating = false;
      }
    });
  }

  runRayDirections(): void {
    if (this.rayForm.invalid) return;

    this.isRaySimulating = true;
    const vals = this.rayForm.value;

    const req: RayDirectionRequest = {
      num_paths: Number(vals.num_paths),
      frequency_ghz: Number(vals.frequency_ghz),
      environment: vals.environment,
      tx_position: [Number(vals.tx_x), Number(vals.tx_y), Number(vals.tx_h)],
      rx_position: [Number(vals.rx_x), Number(vals.rx_y), Number(vals.rx_h)],
      colormap: vals.colormap
    };

    this.simulationService.runRayDirections(req).subscribe({
      next: (res: RayDirectionResult) => {
        this.rayData = res;
        
        // Departure Polar Chart
        this.rayDepChartData.labels = res.paths.map((p: RayDirectionPath) => `Path ${p.path_id} (Dep Az: ${p.departure_azimuth_deg.toFixed(1)}°)`);
        this.rayDepChartData.datasets[0].data = res.paths.map((p: RayDirectionPath) => 100 / Math.max(1, p.path_loss_db)); // Inverse of path loss for spoke length
        this.rayDepChartData.datasets[0].backgroundColor = res.colors || res.paths.map((p: RayDirectionPath) => 
          p.path_type === 'LOS' ? 'rgba(139, 92, 246, 0.6)' : 'rgba(244, 63, 94, 0.4)'
        );
        this.rayDepChart?.update();

        // Arrival Polar Chart
        this.rayArrChartData.labels = res.paths.map((p: RayDirectionPath) => `Path ${p.path_id} (Arr Az: ${p.arrival_azimuth_deg.toFixed(1)}°)`);
        this.rayArrChartData.datasets[0].data = res.paths.map((p: RayDirectionPath) => 100 / Math.max(1, p.path_loss_db)); // Same length logic
        this.rayArrChartData.datasets[0].backgroundColor = res.colors || res.paths.map((p: RayDirectionPath) => 
          p.path_type === 'LOS' ? 'rgba(139, 92, 246, 0.6)' : 'rgba(244, 63, 94, 0.4)'
        );
        this.rayArrChart?.update();

        this.updateSvgSceneArgs(res);

        this.isRaySimulating = false;
        this.showSuccess('Ray Directions simulation completed');
      },
      error: (err: any) => {
        console.error(err);
        this.showError('Ray Directions simulation failed');
        this.isRaySimulating = false;
      }
    });
  }

  sceneViewBox = '0 0 100 100';
  scenePaths: any[] = [];
  sceneTx = { x: 0, y: 0 };
  sceneRx = { x: 100, y: 50 };

  private updateSvgSceneArgs(res: RayDirectionResult) {
    const tx = res.tx_position;
    const rx = res.rx_position;
    this.sceneTx = { x: tx[0], y: tx[1] };
    this.sceneRx = { x: rx[0], y: rx[1] };

    const padding = Math.max(20, res.los_distance_m * 0.2);
    const min_x = Math.min(tx[0], rx[0]) - padding;
    const max_x = Math.max(tx[0], rx[0]) + padding;
    const min_y = Math.min(tx[1], rx[1]) - padding;
    const max_y = Math.max(tx[1], rx[1]) + padding;

    this.sceneViewBox = `${min_x} ${min_y} ${max_x - min_x} ${max_y - min_y}`;

    // Generate arcs
    this.scenePaths = res.paths.map((p, i) => {
      let isLOS = p.path_type === 'LOS';
      let offset = isLOS ? 0 : (p.path_id % 2 === 0 ? 1 : -1) * (10 + (p.path_id * 2));
      let midX = (tx[0] + rx[0]) / 2 + offset;
      let midY = (tx[1] + rx[1]) / 2 - offset;
      let d = `M ${tx[0]},${tx[1]} Q ${midX},${midY} ${rx[0]},${rx[1]}`;
      return {
        path_id: p.path_id,
        path_loss_db: p.path_loss_db,
        delay_ns: p.delay_ns,
        path_type: p.path_type,
        d: d,
        color: res.colors?.[i] || (isLOS ? '#8b5cf6' : '#f43f5e'),
        isLOS: isLOS
      };
    });
  }

  // ─────────────────────────────────────────────────────────────────────────
  // EXPORT METHODS
  // ─────────────────────────────────────────────────────────────────────────

  downloadPng(chartType: 'ber' | 'beam' | 'mod' | 'cap' | 'spectral' | 'path-loss-bar' | 'path-loss-scatter' | 'path-loss-delay' | 'ray-dep' | 'ray-arr' | 'calibOverlay' | 'calibError' | 'sinrLine' | 'sinrPolar'): void {
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
      case 'path-loss-bar':
        chart = this.plBarChart;
        filename = `sionna-pathloss-bar-${new Date().toISOString().slice(0, 10)}.png`;
        break;
      case 'path-loss-scatter':
        chart = this.plScatterChart;
        filename = `sionna-pathloss-scatter-${new Date().toISOString().slice(0, 10)}.png`;
        break;
      case 'path-loss-delay':
        chart = this.plDelayChart;
        filename = `sionna-pathloss-delay-${new Date().toISOString().slice(0, 10)}.png`;
        break;
      case 'ray-dep':
        chart = this.rayDepChart;
        filename = `sionna-ray-departure-${new Date().toISOString().slice(0, 10)}.png`;
        break;
      case 'ray-arr':
        chart = this.rayArrChart;
        filename = `sionna-ray-arrival-${new Date().toISOString().slice(0, 10)}.png`;
        break;
    }

    if (chart?.chart) {
      this.exportService.downloadCanvasPng(chart.chart.canvas, filename);
    }
  }

  downloadCsv(type: 'ber' | 'beam' | 'mod' | 'cap' | 'path-loss' | 'ray-dir'): void {
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
      case 'path-loss':
        csv = this.exportService.generatePathLossCsv(this.pathLossData);
        filename = `sionna_pathloss_${new Date().toISOString().slice(0, 10)}.csv`;
        break;
      case 'ray-dir':
        csv = this.exportService.generateRayDirectionsCsv(this.rayData);
        filename = `sionna_ray_directions_${new Date().toISOString().slice(0, 10)}.csv`;
        break;
    }
    this.exportService.downloadCSV(filename, csv);
  }

  downloadJson(type: 'ber' | 'beam' | 'mod' | 'cap' | 'path-loss' | 'ray-dir'): void {
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
      case 'path-loss':
        data = this.pathLossData;
        filename = `sionna_pathloss_${new Date().toISOString().slice(0, 10)}.json`;
        simType = 'PATH_LOSS';
        break;
      case 'ray-dir':
        data = this.rayData;
        filename = `sionna_raydirections_${new Date().toISOString().slice(0, 10)}.json`;
        simType = 'RAY_DIRECTIONS';
        break;
    }
    const wrapped = this.exportService.wrapWithMetadata(simType, data);
    this.exportService.downloadJSON(filename, wrapped);
  }



  // ─────────────────────────────────────────────────────────────────────────
  // Calibration Features
  // ─────────────────────────────────────────────────────────────────────────
  addCalibRow() {
    this.measurementRows.push({ snr_db: 0, ber_measured: 0.05, location: '' });
  }

  removeCalibRow(index: number) {
    if (this.measurementRows.length > 2) {
      this.measurementRows.splice(index, 1);
    }
  }

  onCsvFileSelected(event: any) {
    const file = event.target.files[0];
    if (file) this.processCsvFile(file);
  }

  onCsvDrop(event: DragEvent) {
    event.preventDefault();
    if (event.dataTransfer?.files.length) {
      this.processCsvFile(event.dataTransfer.files[0]);
    }
  }

  private processCsvFile(file: File) {
    const reader = new FileReader();
    reader.onload = (e) => {
      const text = e.target?.result as string;
      const lines = text.split('\n').filter(l => l.trim().length > 0);
      const parsed: MeasurementPointInput[] = [];
      
      // Skip header if it exists
      let startIdx = 0;
      if (lines[0].toLowerCase().includes('snr')) startIdx = 1;

      for (let i = startIdx; i < lines.length; i++) {
        const parts = lines[i].split(',');
        if (parts.length >= 2) {
          const snr = parseFloat(parts[0]);
          const ber = parseFloat(parts[1]);
          if (!isNaN(snr) && !isNaN(ber)) {
            parsed.push({ snr_db: snr, ber_measured: ber, location: parts[2] ? parts[2].trim() : '' });
          }
        }
      }
      
      if (parsed.length >= 2) {
        this.measurementRows = parsed;
        this.csvLoadedMsg = `Loaded ${parsed.length} measurement points successfully.`;
      } else {
        this.snackBar.open('Invalid CSV format. Need at least 2 data rows.', 'Close', { duration: 3000 });
      }
    };
    reader.readAsText(file);
  }

  runCalibration() {
    if (this.calibForm.invalid || this.measurementRows.length < 2) return;
    this.isCalibSimulating = true;
    this.calibData = null;
    this.dismissEstimate();

    const request: MeasurementOverlayRequest = {
      ...this.calibForm.value,
      measurements: this.measurementRows
    };

    this.simulationService.runMeasurementOverlay(request).subscribe({
      next: (res) => {
        this.calibData = res;
        this.updateCalibCharts(res);
        this.isCalibSimulating = false;
      },
      error: (err) => {
        this.isCalibSimulating = false;
        this.snackBar.open('Calibration failed: ' + (err.error || err.message), 'Close', { duration: 5000 });
      }
    });
  }

  estimateCalib() {
    if (this.calibForm.invalid) return;
    this.isEstimating = true;
    this.estimateTab = 'AWGN';
    const req = {
      simulation_type: 'MEASUREMENT_OVERLAY',
      parameters: { ...this.calibForm.value, points: this.measurementRows.length }
    };
    this.simulationService.runEstimate(req).subscribe({
      next: (res) => { this.estimate = res; this.isEstimating = false; },
      error: () => { this.isEstimating = false; this.snackBar.open('Estimate failed', 'OK', {duration:2000}); }
    });
  }

  updateCalibCharts(data: MeasurementOverlayResult) {
    if (!data.comparison_points || data.comparison_points.length === 0) return;
    
    const sorted = [...data.comparison_points].sort((a, b) => a.snr_db - b.snr_db);
    const snrs = sorted.map(p => p.snr_db);
    const berSim = sorted.map(p => p.ber_simulated);
    const berMeas = sorted.map(p => Math.max(p.ber_measured, 1e-10)); // prevent log(0) error
    
    // Overlay Chart
    this.calibOverlayChartData.labels = snrs;
    this.calibOverlayChartData.datasets[0].data = berSim;
    this.calibOverlayChartData.datasets[1].data = berMeas;
    if (this.calibOverlayChart) this.calibOverlayChart.update();

    // Error Bar Chart
    const errors = sorted.map(p => p.relative_error_percent);
    const colors = errors.map(e => e < 10 ? '#8b5cf6' : e < 30 ? '#f59e0b' : '#f43f5e');
    
    this.calibErrorChartData.labels = snrs.map(s => s + ' dB');
    this.calibErrorChartData.datasets[0].data = errors;
    this.calibErrorChartData.datasets[0].backgroundColor = colors;
    if (this.calibErrorChart) this.calibErrorChart.update();
  }

  calibQualityColor(quality: string): string {
    switch(quality.toLowerCase()) {
      case 'excellent': return '#8b5cf6';
      case 'good': return '#45aaf2';
      case 'fair': return '#f59e0b';
      case 'poor': return '#f43f5e';
      default: return '#aaaaaa';
    }
  }

  downloadCsvCalib() {
    if (!this.calibData) return;
    let csv = 'SNR_dB,BER_Simulated,BER_Measured,AbsoluteError,RelativeErrorPct,Location\n';
    this.calibData.comparison_points.forEach(p => {
      csv += `${p.snr_db},${p.ber_simulated},${p.ber_measured},${p.absolute_error},${p.relative_error_percent},${p.location || ''}\n`;
    });
    const blob = new Blob([csv], { type: 'text/csv;charset=utf-8;' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `calibration_overlay_${new Date().toISOString().slice(0, 10)}.csv`;
    a.click();
    URL.revokeObjectURL(url);
  }

  // ─────────────────────────────────────────────────────────────────────────
  // SINR Steering Features
  // ─────────────────────────────────────────────────────────────────────────
  addSinrAngle() {
    if (this.newAngleInput !== null && this.newAngleInput >= -90 && this.newAngleInput <= 90) {
      if (!this.sinrSteeringAngles.includes(this.newAngleInput)) {
        this.sinrSteeringAngles.push(this.newAngleInput);
        this.sinrSteeringAngles.sort((a,b) => a - b);
      }
      this.newAngleInput = null;
    }
  }

  removeSinrAngle(index: number) {
    if (this.sinrSteeringAngles.length > 1) {
      this.sinrSteeringAngles.splice(index, 1);
    }
  }

  runSinrAnalysis() {
    if (this.sinrForm.invalid || this.sinrSteeringAngles.length === 0) return;
    this.isSinrSimulating = true;
    this.sinrData = null;
    this.dismissEstimate();

    const request: SinrSteeringRequest = {
      ...this.sinrForm.value,
      steering_angles: this.sinrSteeringAngles
    };

    this.simulationService.runSinrSteering(request).subscribe({
      next: (res) => {
        this.sinrData = res;
        this.updateSinrCharts(res);
        this.isSinrSimulating = false;
      },
      error: (err) => {
        this.isSinrSimulating = false;
        this.snackBar.open('SINR analysis failed: ' + (err.error || err.message), 'Close', { duration: 5000 });
      }
    });
  }

  estimateSinr() {
    if (this.sinrForm.invalid) return;
    this.isEstimating = true;
    this.estimateTab = 'SINR_STEERING';
    const req = {
      simulation_type: 'SINR_STEERING',
      parameters: { ...this.sinrForm.value, angles: this.sinrSteeringAngles.length }
    };
    this.simulationService.runEstimate(req).subscribe({
      next: (res) => { this.estimate = res; this.isEstimating = false; },
      error: () => { this.isEstimating = false; this.snackBar.open('Estimate failed', 'OK', {duration:2000}); }
    });
  }

  updateSinrCharts(data: SinrSteeringResult) {
    if (!data.steering_results || data.steering_results.length === 0) return;

    // Line chart
    const angles = data.steering_results.map(r => r.steering_angle_deg);
    const sinr = data.steering_results.map(r => r.sinr_db);
    const ag = data.steering_results.map(r => r.array_gain_db);

    this.sinrLineChartData.labels = angles;
    this.sinrLineChartData.datasets[0].data = sinr;
    this.sinrLineChartData.datasets[1].data = ag;
    if (this.sinrLineChart) this.sinrLineChart.update();

    // Polar chart mapping to simulate an antenna pattern
    // The true pattern requires dense sampling, but we map steering array responses onto it.
    // We create a mock dense pattern for visual effect, highlighting the interference angle.
    const mockAngles = [];
    const patternDb = [];
    const intLayer = [];
    const optAngle = data.optimal_steering.angle_deg;
    const intAngle = this.sinrForm.get('interferenceAngle')?.value || 0;

    for (let a = -90; a <= 90; a += 5) {
      mockAngles.push(a + '\xb0');
      // Simple sync function approx for ULA pattern visual centered at optimal angle
      const diff = Math.abs(a - optAngle);
      const val = diff === 0 ? 0 : 20 * Math.log10(Math.abs(Math.sin((Math.PI * diff)/30) / ((Math.PI * diff)/30) + 1e-2));
      patternDb.push(Math.max(-40, val));
      
      // Interference vector: spike at exactly intAngle
      if (Math.abs(a - intAngle) <= 5) {
        intLayer.push(0); // OdB (edge of radar)
      } else {
        intLayer.push(null);
      }
    }

    this.sinrPolarChartData.labels = mockAngles;
    this.sinrPolarChartData.datasets[0].data = patternDb;
    this.sinrPolarChartData.datasets[1].data = intLayer;
    
    // Auto adjust scales
    if (this.sinrPolarChartOptions && this.sinrPolarChartOptions.scales && this.sinrPolarChartOptions.scales['r']) {
      this.sinrPolarChartOptions.scales['r'].min = -40;
    }
    if (this.sinrPolarChart) this.sinrPolarChart.update();
  }

  downloadCsvSinr() {
    if (!this.sinrData) return;
    let csv = 'SteeringAngleDeg,ArrayGainDb,InterferenceGainDb,SinrDb,EfficiencyPct,IsOptimal\n';
    this.sinrData.steering_results.forEach(r => {
      csv += `${r.steering_angle_deg},${r.array_gain_db},${r.interference_gain_db},${r.sinr_db},${r.efficiency_percent},${r.is_optimal}\n`;
    });
    const blob = new Blob([csv], { type: 'text/csv;charset=utf-8;' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `sinr_steering_${new Date().toISOString().slice(0, 10)}.csv`;
    a.click();
    URL.revokeObjectURL(url);
  }

  copyJson(type: 'ber' | 'beam' | 'mod' | 'cap' | 'path-loss' | 'ray-dir'): void {
    let data: any;
    let simType = '';

    switch (type) {
      case 'ber': data = this.simulationData; simType = 'BER_SNR'; break;
      case 'beam': data = this.beamData; simType = 'BEAM_PATTERN'; break;
      case 'mod': data = this.modData; simType = 'MOD_COMPARISON'; break;
      case 'cap': data = this.capData; simType = 'CHANNEL_CAPACITY'; break;
      case 'path-loss': data = this.pathLossData; simType = 'PATH_LOSS'; break;
      case 'ray-dir': data = this.rayData; simType = 'RAY_DIRECTIONS'; break;
    }

    const wrapped = this.exportService.wrapWithMetadata(simType, data);
    this.exportService.copyToClipboard(wrapped).then(() => {
      this.snackBar.open('JSON copied to clipboard', 'OK', { duration: 2000 });
    });
  }
}
