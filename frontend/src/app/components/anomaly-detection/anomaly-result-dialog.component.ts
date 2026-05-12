import { Component, Inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { AnomalyDetectionService } from './anomaly-detection.service';
import { Anomaly, SEVERITY_CONFIG } from './anomaly.interfaces';

export interface AnomalyDialogData {
  anomaly: Anomaly;
  simulationId: number;
}

@Component({
  selector: 'app-anomaly-result-dialog',
  standalone: true,
  imports: [
    CommonModule, MatDialogModule, MatButtonModule, MatIconModule, MatProgressSpinnerModule
  ],
  templateUrl: './anomaly-result-dialog.component.html',
  styleUrls: ['./anomaly-result-dialog.component.scss']
})
export class AnomalyResultDialogComponent implements OnInit {

  anomaly: Anomaly;
  explanation = '';
  isLoading = false;
  copied = false;
  severityConfig = SEVERITY_CONFIG;

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: AnomalyDialogData,
    public dialogRef: MatDialogRef<AnomalyResultDialogComponent>,
    private anomalyService: AnomalyDetectionService
  ) {
    this.anomaly = data.anomaly;
  }

  ngOnInit() {
    // If Claude already explained this anomaly in a previous session, show it immediately
    if (this.anomaly.aiExplanation) {
      this.explanation = this.anomaly.aiExplanation;
    } else {
      // Otherwise call Claude AI now — this is the researcher's first time asking
      this.fetchExplanation();
    }
  }

  fetchExplanation() {
    this.isLoading = true;
    this.anomalyService.getAnomalyExplanation(this.anomaly.anomalyId).subscribe({
      next: (resp) => {
        this.explanation = resp.fullExplanation;
        this.anomaly.aiExplanation = resp.fullExplanation; // Update local cache
        this.isLoading = false;
      },
      error: () => {
        this.explanation = 'Claude AI is unavailable. Please check your API key configuration or try again later.';
        this.isLoading = false;
      }
    });
  }

  copyExplanation() {
    navigator.clipboard.writeText(this.explanation).then(() => {
      this.copied = true;
      setTimeout(() => this.copied = false, 2000);
    });
  }

  getSeverityConfig() {
    return this.severityConfig[this.anomaly.severity] ?? this.severityConfig['MEDIUM'];
  }
}
