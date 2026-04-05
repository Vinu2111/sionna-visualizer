import { Component, OnInit, ViewChild, ElementRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatSelectModule } from '@angular/material/select';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import Chart from 'chart.js/auto';
import { SigmfImportService } from './sigmf-import.service';
import { SigmfAnalysisResult, SigmfMetadata } from './sigmf.interfaces';

@Component({
  selector: 'app-sigmf-import',
  standalone: true,
  imports: [
    CommonModule, ReactiveFormsModule, MatCardModule, MatButtonModule,
    MatSelectModule, MatIconModule, MatProgressBarModule
  ],
  templateUrl: './sigmf-import.component.html',
  styleUrls: ['./sigmf-import.component.scss']
})
export class SigmfImportComponent implements OnInit {

  metaFile: File | null = null;
  dataFile: File | null = null;
  parsedMetadata: SigmfMetadata | null = null;
  
  simulations = [
    { id: 1, name: 'SIM-001 (QPSK, CDL-B)' },
    { id: 2, name: 'SIM-002 (16QAM, AWGN)' }
  ];

  form: FormGroup;
  isUploading = false;
  currentResult: SigmfAnalysisResult | null = null;

  @ViewChild('overlayCanvas') overlayCanvas!: ElementRef<HTMLCanvasElement>;
  @ViewChild('iqCanvas') iqCanvas!: ElementRef<HTMLCanvasElement>;
  
  overlayChartInstance: any = null;
  iqChartInstance: any = null;

  constructor(private fb: FormBuilder, private sigmfService: SigmfImportService) {
    this.form = this.fb.group({
       simulationId: ['', Validators.required]
    });
  }

  ngOnInit(): void {}

  // Selects Meta cleanly enforcing strict JSON structural integrity cleanly natively   
  onMetaFileSelected(event: any) {
    const file = event.target.files[0];
    if (file && file.name.endsWith('.sigmf-meta')) {
      this.metaFile = file;
      this.sigmfService.parseSigmfMeta(file).subscribe({
         next: (meta) => this.parsedMetadata = meta,
         error: (err) => console.error(err)
      });
    } else {
        alert('Please upload a valid .sigmf-meta file');
    }
  }

  // Binds the physical memory buffers tightly mapping structural block bounds natively 
  onDataFileSelected(event: any) {
    const file = event.target.files[0];
    if (file && file.name.endsWith('.sigmf-data')) {
      this.dataFile = file;
    } else {
        alert('Please upload a valid .sigmf-data binary file');
    }
  }

  canSubmit(): boolean {
    return this.metaFile !== null && this.dataFile !== null && this.form.valid;
  }

  uploadAndAnalyze() {
    if(!this.canSubmit()) return;

    this.isUploading = true;
    const simId = this.form.get('simulationId')?.value;

    this.sigmfService.uploadFiles(this.metaFile!, this.dataFile!, simId).subscribe({
       next: (res) => {
           this.currentResult = res;
           // If no metadata came explicitly from the response logic, use the one we mapped directly from the file initially natively 
           if (!this.currentResult.metadata && this.parsedMetadata) {
               this.currentResult.metadata = this.parsedMetadata;
           }
           this.isUploading = false;
           // Timeout purely allows angular view lifecycle hooks time rendering canvases before charting initiates
           setTimeout(() => this.renderCharts(), 100);
       },
       error: (err) => {
           console.error("Upload process crashed", err);
           this.isUploading = false;
       }
    });
  }

  renderCharts() {
    if (!this.currentResult) return;

    if (this.overlayChartInstance) this.overlayChartInstance.destroy();
    if (this.iqChartInstance) this.iqChartInstance.destroy();

    if (this.overlayCanvas) {
      const overlayConf = this.sigmfService.buildOverlayChart(this.currentResult);
      this.overlayChartInstance = new Chart(this.overlayCanvas.nativeElement, overlayConf);
    }

    if (this.iqCanvas) {
      const iqConf = this.sigmfService.buildConstellationChart(this.currentResult);
      this.iqChartInstance = new Chart(this.iqCanvas.nativeElement, iqConf);
    }
  }
}
