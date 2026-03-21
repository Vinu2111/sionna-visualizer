import { Component, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatIconModule } from '@angular/material/icon';
import { ExportService } from '../../services/export.service';
import { SimulationHistoryItem } from '../../models/simulation-result.model';

@Component({
  selector: 'app-export-dialog',
  standalone: true,
  imports: [CommonModule, FormsModule, MatDialogModule, MatButtonModule, MatCheckboxModule, MatIconModule],
  template: `
    <h2 mat-dialog-title style="display:flex;align-items:center;gap:8px;">
      <mat-icon style="color:#64ffda;">file_download</mat-icon>
      Export Selected Simulations
    </h2>
    <mat-dialog-content style="min-width:340px;padding:16px 24px;">
      <p style="color:#aaa;margin-bottom:16px;">
        {{ data.simulations.length }} simulation{{ data.simulations.length !== 1 ? 's' : '' }} selected.
      </p>
      <div style="display:flex;flex-direction:column;gap:12px;">
        <mat-checkbox [(ngModel)]="exportCsv" color="primary">
          <span style="color:#eeeeee;">CSV <span style="color:#888;font-size:12px;">(one file per simulation)</span></span>
        </mat-checkbox>
        <mat-checkbox [(ngModel)]="exportJson" color="primary">
          <span style="color:#eeeeee;">JSON <span style="color:#888;font-size:12px;">(one file per simulation, includes _metadata)</span></span>
        </mat-checkbox>
      </div>
      <p style="color:#666;font-size:12px;margin-top:16px;">
        <mat-icon style="font-size:14px;vertical-align:middle;">info</mat-icon>
        PNG export is only available from the chart cards directly.
      </p>
    </mat-dialog-content>
    <mat-dialog-actions align="end" style="padding:16px 24px;">
      <button mat-button (click)="close()">Cancel</button>
      <button mat-raised-button color="primary" (click)="download()" [disabled]="!exportCsv && !exportJson">
        <mat-icon>download</mat-icon>
        Download Selected
      </button>
    </mat-dialog-actions>
  `
})
export class ExportDialogComponent {
  exportCsv = true;
  exportJson = true;

  constructor(
    private dialogRef: MatDialogRef<ExportDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { simulations: SimulationHistoryItem[] },
    private exportService: ExportService
  ) {}

  close() { this.dialogRef.close(); }

  async download() {
    const date = new Date().toISOString().slice(0, 10);
    const items: { filename: string; content: string; type: 'csv' | 'json' }[] = [];

    for (const sim of this.data.simulations) {
      const typeKey = this.typeKey(sim.simulationType);

      if (this.exportCsv) {
        const csv = this.generateCsv(sim);
        if (csv) items.push({ filename: `sionna-${typeKey}-${date}.csv`, content: csv, type: 'csv' });
      }

      if (this.exportJson) {
        const wrapped = this.exportService.wrapWithMetadata(sim.simulationType, sim);
        items.push({ filename: `sionna-${typeKey}-${date}.json`, content: JSON.stringify(wrapped), type: 'json' });
      }
    }

    await this.exportService.bulkDownload(items, 250);
    this.dialogRef.close();
  }

  private typeKey(type: string): string {
    const map: Record<string, string> = {
      'BER_SNR': 'ber-snr',
      'BEAM_PATTERN': 'beam-pattern',
      'MOD_COMPARISON': 'mod-comparison',
      'CHANNEL_CAPACITY': 'channel-capacity'
    };
    return map[type] ?? type.toLowerCase();
  }

  private generateCsv(sim: SimulationHistoryItem): string | null {
    try {
      switch (sim.simulationType) {
        case 'BER_SNR':
          return this.exportService.generateBerCsv({
            snr_db: JSON.parse(sim.snrDb || '[]'),
            ber_theoretical: JSON.parse(sim.berTheoretical || '[]'),
            ber_simulated: JSON.parse(sim.berSimulated || '[]'),
            modulation: sim.modulationType,
            code_rate: sim.codeRate
          });
        case 'BEAM_PATTERN':
          return this.exportService.generateBeamCsv({
            angles: JSON.parse(sim.beamAngles || '[]'),
            pattern_db: JSON.parse(sim.beamPatternDb || '[]'),
            num_antennas: sim.numAntennas,
            steering_angle: sim.steeringAngle,
            frequency_ghz: sim.frequencyGhz
          });
        case 'MOD_COMPARISON':
          return this.exportService.generateModComparisonCsv({
            snr_db: JSON.parse(sim.snrDb || '[]'),
            bpsk: JSON.parse(sim.bpskBer || '[]'),
            qpsk: JSON.parse(sim.qpskBer || '[]'),
            qam16: JSON.parse(sim.qam16Ber || '[]'),
            qam64: JSON.parse(sim.qam64Ber || '[]'),
            snr_min: sim.comparisonSnrMin,
            snr_max: sim.comparisonSnrMax
          });
        case 'CHANNEL_CAPACITY':
          return this.exportService.generateCapacityCsv({
            snr_db: JSON.parse(sim.snrDb || '[]'),
            spectral_efficiency: JSON.parse(sim.spectralEfficiencyJson || '[]'),
            capacity_curves: JSON.parse(sim.capacityCurvesJson || '[]'),
            snr_min: sim.snrMin,
            snr_max: sim.snrMax
          });
        default:
          return null;
      }
    } catch {
      return null;
    }
  }
}
