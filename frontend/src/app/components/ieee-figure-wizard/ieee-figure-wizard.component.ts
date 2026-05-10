import { Component, Inject, ViewChild, ElementRef, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef, MatDialogModule } from '@angular/material/dialog';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { MatStepperModule } from '@angular/material/stepper';
import { MatRadioModule } from '@angular/material/radio';
import { MatButtonModule } from '@angular/material/button';
import { FigureExportService } from '../../services/figure-export.service';
import Chart from 'chart.js/auto';

@Component({
  selector: 'app-ieee-figure-wizard',
  standalone: true,
  imports: [
    CommonModule, 
    ReactiveFormsModule,
    MatDialogModule, 
    MatStepperModule, 
    MatRadioModule, 
    MatButtonModule
  ],
  templateUrl: './ieee-figure-wizard.component.html',
  styleUrls: ['./ieee-figure-wizard.component.scss']
})
export class IeeeFigureWizardComponent implements OnInit {
  
  exportFormGroup: FormGroup;
  @ViewChild('previewCanvas', { static: false }) previewCanvas!: ElementRef<HTMLCanvasElement>;
  previewChartInstance: any = null;
  
  constructor(
    public dialogRef: MatDialogRef<IeeeFigureWizardComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private fb: FormBuilder,
    private exportService: FigureExportService
  ) {
    // Initialize the reactive form with default selections
    this.exportFormGroup = this.fb.group({
      journalStyle: ['IEEE', Validators.required],
      exportFormat: ['PDF', Validators.required]
    });
  }

  ngOnInit(): void {
      // Data injected should contain original chart config
  }
  
  // Creates or updates the preview chart on Step 3
  updatePreview(): void {
      if (this.previewChartInstance) {
          this.previewChartInstance.destroy();
      }
      
      const style = this.exportFormGroup.get('journalStyle')?.value;
      
      // Safety check if canvas is not rendered yet
      if (!this.previewCanvas || !this.previewCanvas.nativeElement) return;
      
      const ctx = this.previewCanvas.nativeElement.getContext('2d');
      if (!ctx || !this.data || !this.data.chartConfig) return;
      
      // Clone config deeply to prevent affecting the dashboard
      const configClone = JSON.parse(JSON.stringify(this.data.chartConfig));
      
      // Render the mock preview chart
      this.previewChartInstance = new Chart(ctx, configClone);
      
      // Apply the selected journal formatting
      if (style === 'IEEE') {
          this.exportService.applyIeeeFormatting(this.previewChartInstance);
      } else if (style === 'NATURE') {
          this.exportService.applyNatureFormatting(this.previewChartInstance);
      }
  }

  // Triggered when MatStepper changes step
  onStepChange(event: any): void {
      if (event.selectedIndex === 2) {
          // Entering Step 3: Preview - slight delay to allow rendering of canvas
          setTimeout(() => this.updatePreview(), 200);
      }
  }

  // Finishes the wizard, triggers download and logs export to backend
  exportFigure(): void {
      const style = this.exportFormGroup.get('journalStyle')?.value;
      const format = this.exportFormGroup.get('exportFormat')?.value;
      
      if (!this.previewCanvas) return;
      
      // Download the chart
      if (format === 'SVG') {
          this.exportService.exportAsSvg(this.previewCanvas.nativeElement);
      } else {
          this.exportService.exportAsPdf(this.previewCanvas.nativeElement);
      }
      
      // Track this export in Postgres via the backend
      const simId = this.data?.simulationId || 0; 
      this.exportService.logExport(simId, style, format, this.data?.chartType || 'UNKNOWN_CHART').subscribe(
        res => console.log('Export logged successfully.', res),
        err => console.error('Failed to log export', err)
      );
      
      this.dialogRef.close();
  }
}
