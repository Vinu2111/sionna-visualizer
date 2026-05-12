import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { MatTableModule } from '@angular/material/table';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatRadioModule } from '@angular/material/radio';
import { MatSelectModule } from '@angular/material/select';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { BharatAllianceService } from './bharat-alliance.service';
import { AllianceOrganization, Poc, PocDetail, ALLIANCE_TRACKS, TRACK_COLORS, AllianceReportOptions } from './bharat-alliance.interfaces';
import { RegisterPocDialogComponent } from './register-poc-dialog.component';

@Component({
  selector: 'app-bharat-alliance-dashboard',
  standalone: true,
  imports: [
    CommonModule, FormsModule, MatCardModule, MatButtonModule, MatIconModule,
    MatChipsModule, MatTableModule, MatExpansionModule, MatDialogModule,
    MatRadioModule, MatSelectModule, MatProgressBarModule
  ],
  templateUrl: './bharat-alliance-dashboard.component.html',
  styleUrls: ['./bharat-alliance-dashboard.component.scss']
})
export class BharatAllianceDashboardComponent implements OnInit {

  Math = Math;

  organization!: AllianceOrganization;
  pocs: Poc[] = [];
  activePocDetail: PocDetail | null = null;
  activePocId: number | null = null;

  allianceTracks = ALLIANCE_TRACKS;
  trackColors = TRACK_COLORS;

  trlLabels: Record<number, string> = {
    1: 'Basic Research', 2: 'Technology Concept', 3: 'Proof of Concept',
    4: 'Lab Validation', 5: 'Relevant Environment', 6: 'Prototype Demo',
    7: 'System Prototype', 8: 'System Complete', 9: 'Proven System'
  };

  reportOptions: AllianceReportOptions = {
    reportType: 'QUARTERLY', pocId: 0, quarter: 'Q2', year: 2025
  };

  isGeneratingReport = false;
  simColumns = ['simulationId', 'trlEvidenceFor', 'modulation', 'berAt20db', 'linkedAt'];

  constructor(private service: BharatAllianceService, private dialog: MatDialog) {}

  ngOnInit() {
    this.service.getOrganizationProfile().subscribe(org => this.organization = org);
    this.loadPocs();
  }

  loadPocs() {
    this.service.getMyPocs().subscribe(data => {
      this.pocs = data;
      if (data.length > 0) this.reportOptions.pocId = data[0].pocId;
    });
  }

  managePoc(poc: Poc) {
    if (this.activePocId === poc.pocId) {
      this.activePocId = null;
      this.activePocDetail = null;
      return;
    }
    this.activePocId = poc.pocId;
    this.service.getPocDetail(poc.pocId).subscribe(d => this.activePocDetail = d);
  }

  openRegisterPoc() {
    const d = this.dialog.open(RegisterPocDialogComponent, { width: '650px', disableClose: true });
    d.afterClosed().subscribe(res => { if (res) this.loadPocs(); });
  }

  getTrackLabel(value: string): string {
    return this.allianceTracks.find(t => t.value === value)?.label || value;
  }

  getTrackColor(track: string): string {
    return this.trackColors[track] || '#666';
  }

  getTrlColor(trl: number): string {
    if (trl <= 3) return '#f44336';
    if (trl <= 6) return '#ff9800';
    return '#4caf50';
  }

  getStatusColor(status: string): string {
    const map: Record<string, string> = {
      ACTIVE: '#1976d2', SUBMITTED: '#7b1fa2', UNDER_REVIEW: '#ff9800', APPROVED: '#4caf50'
    };
    return map[status] || '#666';
  }

  getQStatusColor(status: string): string {
    const map: Record<string, string> = {
      NOT_DUE: '#bbb', DUE: '#ff9800', SUBMITTED: '#1976d2', APPROVED: '#4caf50'
    };
    return map[status] || '#bbb';
  }

  advanceTrl(poc: PocDetail) {
    const newTrl = poc.currentTrl + 1;
    if (newTrl > 9) return;
    const evidence = prompt(`Describe evidence demonstrating TRL ${newTrl}:`);
    if (evidence) {
      this.service.updatePocTrl(poc.pocId, newTrl, evidence).subscribe(() => {
        this.managePoc(poc as any); // Reload detail
      });
    }
  }

  linkSimulation(pocId: number) {
    const simId = prompt('Enter Simulation ID to link as TRL evidence:');
    const trl = prompt('This simulation proves TRL level (number):');
    if (simId && trl) {
      this.service.linkSimulationToPoc(pocId, Number(simId), Number(trl))
        .subscribe(() => this.service.getPocDetail(pocId).subscribe(d => this.activePocDetail = d));
    }
  }

  submitQuarter(pocId: number, quarter: string, year: number) {
    this.service.submitQuarterlyStatus(pocId, quarter, year, 'SUBMITTED')
      .subscribe(() => this.service.getPocDetail(pocId).subscribe(d => this.activePocDetail = d));
  }

  generateReport() {
    this.isGeneratingReport = true;
    this.service.generateReport(this.reportOptions).subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `BharatAlliance_${this.reportOptions.quarter}_${this.reportOptions.year}_Report.pdf`;
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
        this.isGeneratingReport = false;
      },
      error: () => this.isGeneratingReport = false
    });
  }

  saveOrgProfile() {
    this.service.saveOrganizationProfile(this.organization).subscribe();
  }
}
