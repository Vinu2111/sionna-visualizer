import { Component, Input, OnChanges, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { AnomalyDetectionService } from './anomaly-detection.service';
import { AnomalyReport, Anomaly, SEVERITY_CONFIG } from './anomaly.interfaces';
import { AnomalyResultDialogComponent } from './anomaly-result-dialog.component';

@Component({
  selector: 'app-anomaly-detection',
  standalone: true,
  imports: [
    CommonModule, MatButtonModule, MatIconModule,
    MatProgressSpinnerModule, MatDialogModule
  ],
  templateUrl: './anomaly-detection.component.html',
  styleUrls: ['./anomaly-detection.component.scss']
})
export class AnomalyDetectionComponent implements OnChanges {

  // The simulation ID this panel belongs to — passed from the parent result view
  @Input() simulationId!: number;

  // When true, the parent component just finished a new simulation run —
  // we auto-trigger analysis immediately without waiting for a button click
  @Input() autoAnalyze = false;

  report: AnomalyReport | null = null;
  isAnalyzing = false;
  error = '';
  dismissedIds = new Set<number>();

  severityConfig = SEVERITY_CONFIG;

  constructor(
    private anomalyService: AnomalyDetectionService,
    private dialog: MatDialog
  ) {}

  ngOnChanges(changes: SimpleChanges) {
    // When the parent gives us a new simulationId, try loading a saved report first
    if (changes['simulationId'] && this.simulationId) {
      this.loadSavedReport();
    }
    // If the parent flagged autoAnalyze=true (fresh simulation just ran), kick off analysis
    if (changes['autoAnalyze'] && this.autoAnalyze && this.simulationId) {
      this.runAnalysis();
    }
  }

  // Load a previously saved report to avoid re-running physics checks
  loadSavedReport() {
    this.anomalyService.getSavedReport(this.simulationId).subscribe({
      next: r => this.report = r,
      error: () => { /* No saved report yet — show the "Run Check" button */ }
    });
  }

  // Run fresh physics-based anomaly detection on this simulation's BER curve
  runAnalysis() {
    this.isAnalyzing = true;
    this.error = '';
    this.anomalyService.analyzeSimulation(this.simulationId).subscribe({
      next: r => { this.report = r; this.isAnalyzing = false; },
      error: err => {
        this.error = err.error?.error || 'Analysis failed. Please try again.';
        this.isAnalyzing = false;
      }
    });
  }

  dismiss(anomalyId: number) {
    this.dismissedIds.add(anomalyId);
  }

  // Opens the AI explanation dialog for a specific anomaly card
  askAiToExplain(anomaly: Anomaly) {
    this.dialog.open(AnomalyResultDialogComponent, {
      width: '620px',
      data: { anomaly, simulationId: this.simulationId }
    });
  }

  // Returns non-dismissed anomalies
  get visibleAnomalies(): Anomaly[] {
    return (this.report?.anomalies ?? []).filter(a => !this.dismissedIds.has(a.anomalyId));
  }

  // Returns true if the only anomaly is the positive INFO (PERFECT_CURVE) record
  get isClear(): boolean {
    const a = this.report?.anomalies ?? [];
    return a.length === 1 && a[0].severity === 'INFO';
  }

  getSeverityConfig(severity: string) {
    return this.severityConfig[severity] ?? this.severityConfig['MEDIUM'];
  }

  getStatusIcon(): string {
    const map: Record<string, string> = {
      CLEAR: 'check_circle', WARNING: 'warning_amber', CRITICAL: 'dangerous'
    };
    return map[this.report?.overallStatus ?? ''] ?? 'help';
  }
}
