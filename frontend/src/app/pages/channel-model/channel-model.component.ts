import { Component, OnInit, ViewChild, ElementRef, AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import { MatSliderModule } from '@angular/material/slider';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import Chart from 'chart.js/auto';
import { ChannelModelService } from './channel-model.service';
import { ChannelModelResult } from './channel-model.interfaces';

@Component({
  selector: 'app-channel-model',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatButtonToggleModule,
    MatSliderModule,
    MatSelectModule,
    MatButtonModule,
    MatProgressSpinnerModule,
    MatCardModule,
    MatIconModule
  ],
  templateUrl: './channel-model.component.html',
  styleUrls: ['./channel-model.component.scss']
})
export class ChannelModelComponent implements OnInit, AfterViewInit {

  form: FormGroup;
  isLoading = false;
  currentResult: ChannelModelResult | null = null;
  modelDescriptionText = '';

  @ViewChild('berCanvas') berCanvas!: ElementRef<HTMLCanvasElement>;
  @ViewChild('delayCanvas') delayCanvas!: ElementRef<HTMLCanvasElement>;
  
  berChartInstance: any = null;
  delayChartInstance: any = null;

  constructor(private fb: FormBuilder, private channelService: ChannelModelService) {
    // Form mapping perfectly locking baseline structural models locally
    this.form = this.fb.group({
      channelModel: ['CDL-B'],
      modulation: ['QPSK'],
      snrMin: [-10],
      snrMax: [40],
      snrSteps: [25],
      numAntennasTx: [16],
      numAntennasRx: [4],
      carrierFrequency: [28.0],
      delaySpread: [100.0],
      numTimeSteps: [1000]
    });
  }

  ngOnInit(): void {}

  ngAfterViewInit(): void {}

  // Executes API and triggers UI rebuild blocks safely isolating Canvas arrays securely
  runSimulation(): void {
    if(this.form.invalid) return;

    this.isLoading = true;
    const params = this.form.value;

    this.channelService.runSimulation(params).subscribe({
      next: (res) => {
        this.currentResult = res;
        this.modelDescriptionText = this.channelService.getModelDescription(res.channelModel);
        this.renderCharts(res);
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Simulation Failed', err);
        this.isLoading = false;
      }
    });
  }

  // Parses results destroying old canvases manually freeing resources cleanly via service abstraction completely 
  renderCharts(result: ChannelModelResult): void {
    if(this.berChartInstance) this.berChartInstance.destroy();
    if(this.delayChartInstance) this.delayChartInstance.destroy();

    if(this.berCanvas && this.delayCanvas) {
      const berConfig = this.channelService.buildBerChart(result);
      const delayConfig = this.channelService.buildDelayProfileChart(result);

      this.berChartInstance = new Chart(this.berCanvas.nativeElement, berConfig);
      this.delayChartInstance = new Chart(this.delayCanvas.nativeElement, delayConfig);
    }
  }
}
