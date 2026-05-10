import { Component, OnInit, OnDestroy, ViewChild, ElementRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatSliderModule } from '@angular/material/slider';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatChipsModule } from '@angular/material/chips';
import { MatDialogModule, MatDialog } from '@angular/material/dialog';
import { MatInputModule } from '@angular/material/input';
import { MatTooltipModule } from '@angular/material/tooltip';

import { Subject, timer } from 'rxjs';
import { debounceTime, switchMap, takeUntil } from 'rxjs/operators';
import { Chart, ChartConfiguration, registerables } from 'chart.js';

import { ThzService } from '../../components/thz-atmospheric/thz.service';
import { ThzParams, ThzResult, ThzScenario, LinkBudget } from '../../components/thz-atmospheric/thz.interfaces';

Chart.register(...registerables);

@Component({
  selector: 'app-thz-atmospheric',
  standalone: true,
  imports: [
    CommonModule, FormsModule, MatCardModule, MatSliderModule,
    MatIconModule, MatButtonModule, MatChipsModule, MatDialogModule,
    MatInputModule, MatTooltipModule
  ],
  templateUrl: './thz-atmospheric.component.html',
  styleUrls: ['./thz-atmospheric.component.scss']
})
export class ThzAtmosphericComponent implements OnInit, OnDestroy {
  @ViewChild('spectrumChartCanvas') spectrumChartCanvas!: ElementRef;
  @ViewChild('linkBudgetChartCanvas') linkBudgetChartCanvas!: ElementRef;
  @ViewChild('berChartCanvas') berChartCanvas!: ElementRef;

  spectrumChart: Chart | undefined;
  linkBudgetChart: Chart | undefined;
  berChart: Chart | undefined;

  bands = [100, 200, 300, 400, 500, 700, 1000];

  params: ThzParams = {
    frequencyGhz: 300,
    humidityPercent: 50,
    temperatureCelsius: 20,
    pressureHpa: 1013,
    rainRateMmPerHr: 0,
    linkDistanceMeters: 100,
    txPowerDbm: 20
  };

  result: ThzResult | null = null;
  linkBudget: LinkBudget | null = null;
  savedScenarios: ThzScenario[] = [];

  private destroy$ = new Subject<void>();
  private paramsChanged$ = new Subject<ThzParams>();

  isCalculating = false;

  constructor(private thzService: ThzService) {}

  ngOnInit() {
    this.loadScenarios();

    // Debounce the slider inputs by 100ms before calling the API
    this.paramsChanged$.pipe(
      takeUntil(this.destroy$),
      debounceTime(100),
      switchMap((p) => {
        this.isCalculating = true;
        return this.thzService.calculateAtmosphericEffects(p);
      })
    ).subscribe((res) => {
      this.isCalculating = false;
      this.result = res;
      this.linkBudget = this.thzService.calculateLinkBudget(this.params, res);
      this.updateCharts();
    });

    // Run first calculation
    this.onParamsChange();
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
    if (this.spectrumChart) this.spectrumChart.destroy();
    if (this.linkBudgetChart) this.linkBudgetChart.destroy();
    if (this.berChart) this.berChart.destroy();
  }

  onParamsChange() {
    this.paramsChanged$.next(this.params);
  }

  selectBand(hz: number) {
    this.params.frequencyGhz = hz;
    this.onParamsChange();
  }

  getBandBadge(hz: number): { text: string; color: string } | null {
    if (hz === 60 || hz === 119) return { text: 'O2 Peak', color: 'red' };
    if (hz === 300 || hz === 350) return { text: 'Low Absorption Window', color: 'green' };
    return null;
  }

  getLinkDistanceLogLabel(val: number): string {
    // Sliders are tricky with log scales without custom components
    // We bind the slider 0-100 to map to 1-1000m
    return `${val}m`; // Math logic handled in template with formatting if needed
  }

  formatRainRate(rain: number): string {
    if (rain === 0) return 'Clear';
    if (rain <= 5) return 'Light Rain';
    if (rain <= 25) return 'Moderate Rain';
    if (rain <= 50) return 'Heavy Rain';
    return 'Extreme Rain';
  }

  loadScenarios() {
    this.thzService.getSavedScenarios().subscribe(res => {
      this.savedScenarios = res;
    });
  }

  applyScenario(s: ThzScenario) {
    const p = s.params as any;
    this.params = {
       frequencyGhz: p.frequency_ghz || p.frequencyGhz,
       humidityPercent: p.humidity_percent || p.humidityPercent,
       temperatureCelsius: p.temperature_celsius || p.temperatureCelsius,
       pressureHpa: p.pressure_hpa || p.pressureHpa,
       rainRateMmPerHr: p.rain_rate_mm_per_hr || p.rainRateMmPerHr,
       linkDistanceMeters: p.link_distance_meters || p.linkDistanceMeters,
       txPowerDbm: p.tx_power_dbm || p.txPowerDbm
    };
    this.onParamsChange();
  }

  saveCurrentScenario() {
    const name = prompt('Name this scenario (e.g. Mumbai Monsoon):');
    if (!name) return;
    this.thzService.saveScenario(name, this.params).subscribe(() => {
      this.loadScenarios();
    });
  }

  private updateCharts() {
    if (!this.result) return;
    
    // Defer to next tick so canvases exist
    setTimeout(() => {
      this.buildSpectrumChart();
      this.buildLinkBudgetChart();
      this.buildBerChart();
    }, 0);
  }

  private buildSpectrumChart() {
    if (this.spectrumChart) this.spectrumChart.destroy();
    if (!this.spectrumChartCanvas) return;

    const freqs = this.result!.absorption_spectrum.map(s => s.frequencyGhz);
    const total = this.result!.absorption_spectrum.map(s => s.totalAbsorptionDbPerKm);
    const h2o = this.result!.absorption_spectrum.map(s => s.h2oAbsorptionDbPerKm);
    const o2 = this.result!.absorption_spectrum.map(s => s.o2AbsorptionDbPerKm);

    this.spectrumChart = new Chart(this.spectrumChartCanvas.nativeElement, {
      type: 'line',
      data: {
        labels: freqs,
        datasets: [
          { label: 'Total Absorption', data: total, borderColor: '#3b82f6', borderWidth: 2, pointRadius: 0, borderDash: [] },
          { label: 'H2O', data: h2o, borderColor: '#60a5fa', borderWidth: 1, pointRadius: 0, borderDash: [5, 5] },
          { label: 'O2', data: o2, borderColor: '#ef4444', borderWidth: 1, pointRadius: 0, borderDash: [5, 5] },
        ]
      },
      options: {
        responsive: true,
        plugins: {
          legend: { position: 'top' },
          title: { display: true, text: 'Absorption Coefficient vs Frequency' }
        },
        scales: {
          x: { title: { display: true, text: 'Frequency (GHz)' } },
          y: { type: 'logarithmic', title: { display: true, text: 'Absorption (dB/km)' } }
        }
      }
    });
  }

  private buildLinkBudgetChart() {
    if (this.linkBudgetChart) this.linkBudgetChart.destroy();
    if (!this.linkBudgetChartCanvas) return;

    const p = this.linkBudget!;
    
    // Represent Gains as positive additions, Losses as negative for waterfall effect or just magnitude
    // We'll use a standard bar chart
    const labels = ['TX Power', 'TX Antenna', 'FSPL', 'Atmos Loss', 'Rain Loss', 'RX Antenna', 'RX Power'];
    const values = [p.txPowerDbm, p.txAntennaGainDbi, -p.freeSpacePathLossDb, -p.molecularAbsorptionDb, -p.rainAttenuationDb, p.rxAntennaGainDbi, p.receivedPowerDbm];
    const colors: string[] = values.map(v => v >= 0 ? '#10b981' : '#ef4444');

    if (p.receivedPowerDbm < -100) colors[6] = '#ef4444';
    else if (p.receivedPowerDbm < -80) colors[6] = '#eab308';
    
    this.linkBudgetChart = new Chart(this.linkBudgetChartCanvas.nativeElement, {
      type: 'bar',
      data: {
        labels: labels,
        datasets: [{
          label: 'dB or dBm',
          data: values,
          backgroundColor: colors
        }]
      },
      options: {
        indexAxis: 'y',
        responsive: true,
        plugins: {
          legend: { display: false },
          title: { display: true, text: `Link Budget at ${this.params.linkDistanceMeters}m` }
        }
      }
    });
  }

  private buildBerChart() {
    if (this.berChart) this.berChart.destroy();
    if (!this.berChartCanvas) return;

    const distances = this.result!.distance_range_meters;
    const berActual = this.result!.ber_at_distances;

    this.berChart = new Chart(this.berChartCanvas.nativeElement, {
      type: 'line',
      data: {
        labels: distances.map(d => Math.round(d)),
        datasets: [
          { label: 'BER (Current Conditions)', data: berActual, borderColor: '#ef4444', borderWidth: 2, pointRadius: 0 }
        ]
      },
      options: {
        responsive: true,
        plugins: {
          legend: { position: 'top' },
          title: { display: true, text: 'BER vs Link Distance' }
        },
        scales: {
          x: { type: 'linear', title: { display: true, text: 'Distance (meters)' } },
          y: { type: 'logarithmic', title: { display: true, text: 'BER (QPSK)' } }
        }
      }
    });
  }
}
