import { Component, OnInit, ViewChild, ElementRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatSelectModule } from '@angular/material/select';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatTooltipModule } from '@angular/material/tooltip';
import Chart from 'chart.js/auto';
import { MultiSimulatorService } from './multi-simulator.service';
import { ComparisonResult, ExternalSimData, SimulationSummary } from './multi-simulator.interfaces';

@Component({
  selector: 'app-multi-simulator-comparison',
  standalone: true,
  imports: [
    CommonModule, ReactiveFormsModule, MatCardModule, MatButtonModule,
    MatSelectModule, MatIconModule, MatProgressBarModule, MatTooltipModule
  ],
  templateUrl: './multi-simulator-comparison.component.html',
  styleUrls: ['./multi-simulator-comparison.component.scss']
})
export class MultiSimulatorComponent implements OnInit {

  simulations: SimulationSummary[] = [];
  selectedSimulation: SimulationSummary | null = null;
  csvFile: File | null = null;
  detectedColumns: string[] = [];

  form: FormGroup;
  isProcessing = false;
  currentResult: ComparisonResult | null = null;
  Math = Math; // Template mapping
  shareUrl = '';

  @ViewChild('berCanvas') berCanvas!: ElementRef<HTMLCanvasElement>;
  @ViewChild('throughputCanvas') throughputCanvas!: ElementRef<HTMLCanvasElement>;
  
  berChartInstance: any = null;
  throughputChartInstance: any = null;

  constructor(private fb: FormBuilder, private multiService: MultiSimulatorService) {
    this.form = this.fb.group({
       sionnaSimulationId: ['', Validators.required],
       simulatorType: ['MATLAB', Validators.required]
    });
  }

  ngOnInit(): void {
    // Standard async mapped loading correctly cleanly syncing
    this.multiService.loadUserSimulations().subscribe(data => {
       this.simulations = data;
    });

    this.form.get('sionnaSimulationId')?.valueChanges.subscribe(val => {
       this.selectedSimulation = this.simulations.find(s => s.id === val) || null;
    });
  }

  // Parses physical CSV bounds completely synchronously safely isolating structural arrays 
  onCsvFileSelected(event: any) {
    const file = event.target.files[0];
    if (file && file.name.endsWith('.csv')) {
      this.csvFile = file;
      this.multiService.parseLocalCsv(file).subscribe({
         next: (data) => this.detectedColumns = data.detectedColumns,
         error: () => this.detectedColumns = ['Error: Invalid CSV format']
      });
    } else {
        alert('Please upload a valid .csv file exported from your external simulator.');
    }
  }

  canSubmit(): boolean {
    return this.form.valid && this.csvFile !== null;
  }

  compareNow() {
    if(!this.canSubmit()) return;

    this.isProcessing = true;
    const simId = this.form.get('sionnaSimulationId')?.value;
    const type = this.form.get('simulatorType')?.value;

    this.multiService.uploadAndCompare(simId, type, this.csvFile!).subscribe({
       next: (res) => {
           this.currentResult = res;
           this.shareUrl = this.multiService.generateShareableUrl(res.comparisonId);
           this.isProcessing = false;
           setTimeout(() => this.renderCharts(), 100);
       },
       error: (err) => {
           console.error("Comparison crashed natively", err);
           this.isProcessing = false;
       }
    });
  }

  // Generates physical mapped arrays safely destroying previous elements cleanly
  renderCharts() {
    if (!this.currentResult) return;

    if (this.berChartInstance) this.berChartInstance.destroy();
    if (this.throughputChartInstance) this.throughputChartInstance.destroy();

    if (this.berCanvas) {
      const berConf = this.multiService.buildBerComparisonChart(this.currentResult);
      this.berChartInstance = new Chart(this.berCanvas.nativeElement, berConf);
    }

    if (this.currentResult.sionnaThroughput && this.currentResult.sionnaThroughput.length > 0 && this.throughputCanvas) {
      const thruConf = this.multiService.buildThroughputChart(this.currentResult);
      this.throughputChartInstance = new Chart(this.throughputCanvas.nativeElement, thruConf);
    }
  }

  copyUrl() {
     navigator.clipboard.writeText(this.shareUrl);
     alert("URL copied to clipboard!");
  }
}
