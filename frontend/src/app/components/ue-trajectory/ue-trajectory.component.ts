import { Component, OnInit, ViewChild, ElementRef, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatInputModule } from '@angular/material/input';
import { MatSliderModule } from '@angular/material/slider';
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatSnackBar } from '@angular/material/snack-bar';
import { NgChartsModule, BaseChartDirective } from 'ng2-charts';
import { ChartConfiguration, ChartOptions } from 'chart.js';
import * as d3 from 'd3';

import { SimulationService } from '../../services/simulation.service';
import { ExportService } from '../../services/export.service';
import { UeTrajectoryRequest, UeTrajectoryResult, UeWaypoint, ColormapOption } from '../../models/simulation-result.model';
import { ColormapSelectorComponent } from '../colormap-selector/colormap-selector.component';

@Component({
  selector: 'app-ue-trajectory',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatSelectModule,
    MatInputModule,
    MatSliderModule,
    MatButtonToggleModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatTooltipModule,
    NgChartsModule,
    ColormapSelectorComponent
  ],
  templateUrl: './ue-trajectory.component.html',
  styleUrls: ['./ue-trajectory.component.scss']
})
export class UeTrajectoryComponent implements OnInit, OnDestroy {
  ueForm: FormGroup;
  isSimulating = false;
  isEstimating = false;
  result: UeTrajectoryResult | null = null;
  estimate: any = null;

  @ViewChild('mapContainer', { static: false }) mapContainer!: ElementRef;
  @ViewChild('signalChart') signalChart?: BaseChartDirective;
  @ViewChild('distChart') distChart?: BaseChartDirective;

  // Chart configs
  public signalChartData: ChartConfiguration<'line'>['data'] = {
    labels: [],
    datasets: [
      {
        data: [],
        label: 'Signal Strength (dBm)',
        borderColor: '#64ffda',
        backgroundColor: 'rgba(100, 255, 218, 0.2)',
        fill: true,
        tension: 0.1,
        pointRadius: 4,
        pointBackgroundColor: []
      }
    ]
  };

  public signalChartOptions: ChartOptions<'line'> = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: { display: false },
      title: { display: true, text: 'Signal Strength Along UE Path', color: '#eeeeee' },
      tooltip: {
        callbacks: {
          label: (context) => `Signal: ${context.parsed.y} dBm`
        }
      }
    },
    scales: {
      x: { title: { display: true, text: 'Waypoint', color: '#aaaaaa' }, ticks: { color: '#aaaaaa' }, grid: { color: '#1e2a3a' } },
      y: { title: { display: true, text: 'Signal (dBm)', color: '#aaaaaa' }, ticks: { color: '#aaaaaa' }, grid: { color: '#1e2a3a' } }
    }
  };

  public distChartData: ChartConfiguration<'bar'>['data'] = {
    labels: [],
    datasets: [
      {
        type: 'bar',
        label: 'Segment Distance (m)',
        data: [],
        backgroundColor: '#42a5f5',
        yAxisID: 'y'
      },
      {
        type: 'line' as any,
        label: 'Cumulative Distance (m)',
        data: [],
        borderColor: '#ffca28',
        backgroundColor: '#ffca28',
        tension: 0.1,
        yAxisID: 'y1'
      } as any
    ]
  };

  public distChartOptions: ChartOptions<'bar'> = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: { labels: { color: '#eeeeee' } },
      title: { display: true, text: 'Segment Distance & Cumulative Path', color: '#eeeeee' }
    },
    scales: {
      x: { title: { display: true, text: 'Segment', color: '#aaaaaa' }, ticks: { color: '#aaaaaa' }, grid: { color: '#1e2a3a' } },
      y: { 
        type: 'linear', display: true, position: 'left',
        title: { display: true, text: 'Segment Dist (m)', color: '#aaaaaa' }, ticks: { color: '#aaaaaa' }, grid: { color: '#1e2a3a' } 
      },
      y1: {
        type: 'linear', display: true, position: 'right',
        title: { display: true, text: 'Cumulative Dist (m)', color: '#aaaaaa' }, ticks: { color: '#aaaaaa' }, grid: { drawOnChartArea: false }
      }
    }
  };

  // Animation state
  private animationInterval: any;
  public isPlaying = false;
  private currentAnimTime = 0;
  private animDuration = 3000; // 3 seconds total

  constructor(
    private fb: FormBuilder,
    private simService: SimulationService,
    private exportService: ExportService,
    private snackBar: MatSnackBar
  ) {
    this.ueForm = this.fb.group({
      numWaypoints: [6, [Validators.required]],
      frequencyGhz: [28, [Validators.required]],
      environment: ['urban', [Validators.required]],
      speedKmh: [30, [Validators.required, Validators.min(5), Validators.max(120)]],
      trajectoryType: ['random', [Validators.required]],
      txHeight: [25, [Validators.required, Validators.min(1), Validators.max(100)]],
      colormap: ['default']
    });
  }

  ngOnInit(): void {}

  ngOnDestroy(): void {
    if (this.animationInterval) {
      clearInterval(this.animationInterval);
    }
  }

  runSimulation(): void {
    if (this.ueForm.invalid) return;
    this.isSimulating = true;
    this.result = null;
    this.stopAnimation();

    const form = this.ueForm.value;
    const req: UeTrajectoryRequest = {
      num_waypoints: form.numWaypoints,
      frequency_ghz: form.frequencyGhz,
      environment: form.environment,
      tx_position: [0, 0, form.txHeight],
      speed_kmh: form.speedKmh,
      trajectory_type: form.trajectoryType,
      colormap: form.colormap
    };

    this.simService.runUeTrajectory(req).subscribe({
      next: (res) => {
        this.result = res;
        this.updateCharts(res);
        setTimeout(() => this.drawMap(res), 100);
        this.isSimulating = false;
      },
      error: (err) => {
        console.error(err);
        this.snackBar.open('Simulation failed', 'Close', { duration: 3000 });
        this.isSimulating = false;
      }
    });
  }

  estimateTime(): void {
    if (this.ueForm.invalid) return;
    this.isEstimating = true;
    const req: any = {
      simulation_type: 'UE_TRAJECTORY',
      parameters: this.ueForm.value
    };
    this.simService.runEstimate(req).subscribe({
      next: (res) => {
        this.estimate = res;
        this.isEstimating = false;
      },
      error: () => {
        this.snackBar.open('Estimate failed', 'Close', { duration: 3000 });
        this.isEstimating = false;
      }
    });
  }

  private updateCharts(res: UeTrajectoryResult): void {
    const wp = res.waypoints;
    const colors = wp.map(w => w.handover_required ? '#EF5350' : '#00BFA5');
    
    this.signalChartData.labels = wp.map((_, i) => `Wp ${i+1}`);
    this.signalChartData.datasets[0].data = wp.map(w => w.signal_dbm);
    this.signalChartData.datasets[0].pointBackgroundColor = colors;

    // Dist chart
    const segLabels = [];
    const segDist = [];
    const cumDist = [];
    let currentCum = 0;
    
    for (let i = 0; i < wp.length - 1; i++) {
      segLabels.push(`${i+1}→${i+2}`);
      const dx = wp[i+1].position[0] - wp[i].position[0];
      const dy = wp[i+1].position[1] - wp[i].position[1];
      const d = Math.sqrt(dx*dx + dy*dy);
      segDist.push(d);
      currentCum += d;
      cumDist.push(currentCum);
    }
    
    this.distChartData.labels = segLabels;
    this.distChartData.datasets[0].data = segDist;
    this.distChartData.datasets[1].data = cumDist;

    this.signalChart?.chart?.update();
    this.distChart?.chart?.update();
  }

  // --- D3 Map Drawing ---
  private drawMap(res: UeTrajectoryResult): void {
    if (!this.mapContainer) return;
    const element = this.mapContainer.nativeElement;
    d3.select(element).selectAll('*').remove();

    const width = element.clientWidth;
    const height = 400;
    
    const svg = d3.select(element).append('svg')
      .attr('width', width)
      .attr('height', height);

    // Padding
    const margin = 40;
    
    const allX = res.waypoints.map(w => w.position[0]).concat(res.tx_position[0]);
    const allY = res.waypoints.map(w => w.position[1]).concat(res.tx_position[1]);
    
    const minX = Math.min(...allX);
    const maxX = Math.max(...allX);
    const minY = Math.min(...allY);
    const maxY = Math.max(...allY);
    
    const radius = this.getCoverageRadius(this.ueForm.value.environment);
    
    const scaleX = d3.scaleLinear()
      .domain([minX - radius*1.1, maxX + radius*1.1])
      .range([margin, width - margin]);
      
    const scaleY = d3.scaleLinear()
      .domain([minY - radius*1.1, maxY + radius*1.1])
      .range([height - margin, margin]); // inverted Y

    // Coverage circle
    svg.append('circle')
      .attr('cx', scaleX(res.tx_position[0]))
      .attr('cy', scaleY(res.tx_position[1]))
      .attr('r', scaleX(res.tx_position[0] + radius) - scaleX(res.tx_position[0]))
      .attr('fill', 'rgba(100, 255, 218, 0.05)')
      .attr('stroke', 'rgba(100, 255, 218, 0.3)')
      .attr('stroke-dasharray', '5,5');

    // Path segments
    for (let i = 0; i < res.waypoints.length - 1; i++) {
        const w1 = res.waypoints[i];
        const w2 = res.waypoints[i+1];
        
        const avgSig = (w1.signal_dbm + w2.signal_dbm) / 2;
        let color = '#EF5350'; // red
        if (avgSig > -70) color = '#00BFA5'; // teal
        else if (avgSig > -85) color = '#FFD54F'; // yellow
        
        svg.append('line')
          .attr('x1', scaleX(w1.position[0]))
          .attr('y1', scaleY(w1.position[1]))
          .attr('x2', scaleX(w2.position[0]))
          .attr('y2', scaleY(w2.position[1]))
          .attr('stroke', color)
          .attr('stroke-width', 3);
    }

    // TX Triangle
    const txG = svg.append('g')
      .attr('transform', `translate(${scaleX(res.tx_position[0])}, ${scaleY(res.tx_position[1])})`);
      
    txG.append('polygon')
      .attr('points', '0,-8 8,8 -8,8')
      .attr('fill', '#00BFA5');
    
    // Waypoints
    const wpGroups = svg.selectAll('.wp').data(res.waypoints).enter().append('g')
      .attr('transform', (d: any) => `translate(${scaleX(d.position[0])}, ${scaleY(d.position[1])})`);
      
    wpGroups.append('circle')
      .attr('r', 8)
      .attr('fill', (d: any) => d.handover_required ? '#EF5350' : '#00BFA5');
      
    wpGroups.append('text')
      .text((d: any) => d.handover_required ? 'H' : '')
      .attr('text-anchor', 'middle')
      .attr('dominant-baseline', 'central')
      .attr('fill', 'white')
      .attr('font-size', '10px')
      .attr('font-weight', 'bold');
      
    wpGroups.append('text')
      .text((d: any, i: number) => String(i + 1))
      .attr('x', 12)
      .attr('y', 4)
      .attr('fill', '#aaaaaa')
      .attr('font-size', '12px');

    // Tooltips
    wpGroups.append('title')
      .text((d: any) => `Signal: ${d.signal_dbm.toFixed(1)} dBm\nDist: ${d.distance_m.toFixed(1)} m\nTime: ${d.time_s.toFixed(1)} s\nHandover: ${d.handover_required}`);

    // Setup animated UE Icon
    this.setupAnimation(svg, scaleX, scaleY, res.waypoints);
  }

  private getCoverageRadius(env: string): number {
    if (env === 'urban') return 400;
    if (env === 'suburban') return 800;
    return 2000;
  }

  // --- Animation ---
  private ueIcon: any;
  private animScaleX: any;
  private animScaleY: any;
  private animWaypoints: UeWaypoint[] = [];

  private setupAnimation(svg: any, scaleX: any, scaleY: any, waypoints: UeWaypoint[]) {
    this.animScaleX = scaleX;
    this.animScaleY = scaleY;
    this.animWaypoints = waypoints;
    
    this.ueIcon = svg.append('circle')
      .attr('r', 5)
      .attr('fill', '#ffffff')
      .attr('cx', scaleX(waypoints[0].position[0]))
      .attr('cy', scaleY(waypoints[0].position[1]))
      .attr('display', 'none');
  }

  playAnimation(): void {
    if (!this.result || this.animWaypoints.length < 2) return;
    this.isPlaying = true;
    this.ueIcon.attr('display', 'block');
    
    const totalTimeS = this.result.summary.total_time_s;
    const startTimeStamp = Date.now() - this.currentAnimTime;
    
    this.animationInterval = setInterval(() => {
      this.currentAnimTime = Date.now() - startTimeStamp;
      let progress = this.currentAnimTime / this.animDuration;
      
      if (progress >= 1.0) {
        progress = 1.0;
        this.stopAnimation();
      }
      
      const targetSimTime = progress * totalTimeS;
      
      // Find segment
      for (let i = 0; i < this.animWaypoints.length - 1; i++) {
        const w1 = this.animWaypoints[i];
        const w2 = this.animWaypoints[i+1];
        if (targetSimTime >= w1.time_s && targetSimTime <= w2.time_s) {
          const segDuration = w2.time_s - w1.time_s;
          const segProgress = segDuration > 0 ? (targetSimTime - w1.time_s) / segDuration : 1;
          const x = w1.position[0] + segProgress * (w2.position[0] - w1.position[0]);
          const y = w1.position[1] + segProgress * (w2.position[1] - w1.position[1]);
          this.ueIcon.attr('cx', this.animScaleX(x)).attr('cy', this.animScaleY(y));
          break;
        }
      }
    }, 30);
  }

  stopAnimation(): void {
    this.isPlaying = false;
    if (this.animationInterval) clearInterval(this.animationInterval);
  }

  resetAnimation(): void {
    this.stopAnimation();
    this.currentAnimTime = 0;
    if (this.ueIcon && this.animWaypoints.length > 0) {
      this.ueIcon.attr('cx', this.animScaleX(this.animWaypoints[0].position[0]))
                 .attr('cy', this.animScaleY(this.animWaypoints[0].position[1]));
    }
  }

  // --- Exports ---
  downloadCsv() {
    if (!this.result) return;
    const items = this.result.waypoints.map((w, i) => ({
      waypoint: i + 1,
      x: w.position[0],
      y: w.position[1],
      distance_m: w.distance_m,
      signal_dbm: w.signal_dbm,
      handover_required: w.handover_required,
      time_s: w.time_s
    }));
    const header = ['Waypoint', 'X', 'Y', 'Distance_m', 'Signal_dBm', 'Handover', 'Time_s'].join(',');
    const rows = items.map(w => `${w.waypoint},${w.x.toFixed(2)},${w.y.toFixed(2)},${w.distance_m.toFixed(2)},${w.signal_dbm.toFixed(2)},${w.handover_required},${w.time_s.toFixed(2)}`);
    const csvContext = [header, ...rows].join('\n');
    this.exportService.downloadCSV('ue_trajectory_waypoints.csv', csvContext);
  }

  downloadJson() {
    if (!this.result) return;
    this.exportService.downloadJSON('ue_trajectory.json', this.result);
  }

  copyJson() {
    if (!this.result) return;
    this.exportService.copyToClipboard(this.result);
    this.snackBar.open('JSON copied', 'Close', { duration: 2000 });
  }
}
