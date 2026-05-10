import { Component, OnInit, OnDestroy } from '@angular/core';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatRadioModule } from '@angular/material/radio';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatInputModule } from '@angular/material/input';
import { MatNativeDateModule } from '@angular/material/core';
import { TtdfService } from './ttdf.service';
import { TtdfProject, Milestone, ReportOptions } from './ttdf.interfaces';
import { AddMilestoneDialogComponent } from './add-milestone-dialog.component';

@Component({
  selector: 'app-ttdf-dashboard',
  standalone: true,
  imports: [
    CommonModule, MatCardModule, MatButtonModule, MatIconModule, MatProgressBarModule,
    MatDialogModule, MatRadioModule, MatCheckboxModule, MatDatepickerModule, 
    MatNativeDateModule, MatInputModule, FormsModule
  ],
  templateUrl: './ttdf-dashboard.component.html',
  styleUrls: ['./ttdf-dashboard.component.scss']
})
export class TtdfDashboardComponent implements OnInit, OnDestroy {
  destroy$ = new Subject<void>();

  project!: TtdfProject;
  milestones: Milestone[] = [];
  
  trlLevels = [
      { num: 1, name: 'Basic Research' },
      { num: 2, name: 'Technology Concept' },
      { num: 3, name: 'Proof of Concept' },
      { num: 4, name: 'Lab Validation' },
      { num: 5, name: 'Relevant Environment Test' },
      { num: 6, name: 'Prototype Demo' },
      { num: 7, name: 'System Prototype' },
      { num: 8, name: 'System Complete' },
      { num: 9, name: 'Proven System' }
  ];

  reportOptions: ReportOptions = {
      reportType: 'MONTHLY',
      fromDate: '',
      toDate: '',
      includeSections: ['executive', 'milestones', 'simulations', 'trl', 'nextplan']
  };
  
  isGeneratingReport = false;

  constructor(
      private ttdfService: TtdfService,
      private dialog: MatDialog
  ) {}

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }

  ngOnInit() {
      this.loadData();
  }

  loadData() {
      this.ttdfService.getProject().pipe(takeUntil(this.destroy$)).subscribe(p => this.project = p);
      this.ttdfService.getMilestones().pipe(takeUntil(this.destroy$)).subscribe(m => this.milestones = m);
  }

  getCompletedMilestonesCount(): number {
      return this.milestones.filter(m => m.status === 'COMPLETED').length;
  }

  getTrlColor(trl: number): string {
      if (trl <= 3) return '#f44336'; // Red
      if (trl <= 6) return '#ff9800'; // Yellow
      return '#4caf50'; // Green
  }

  updateTrl(level: number) {
      if (confirm(`Confirm advancing project to TRL ${level}?`)) {
          this.ttdfService.updateTrl(level).pipe(takeUntil(this.destroy$)).subscribe(() => this.loadData());
      }
  }

  openAddMilestone() {
      const d = this.dialog.open(AddMilestoneDialogComponent, { width: '600px' });
      d.afterClosed().pipe(takeUntil(this.destroy$)).subscribe(res => { if(res) this.loadData(); });
  }

  linkSimulation(milestone: Milestone) {
      // Direct hardcoded payload binding mocking selection workflow natively
      const simId = prompt("Enter Simulation ID to link as proof natively:");
      if(simId) {
          this.ttdfService.linkSimulationToMilestone(milestone.milestoneId, Number(simId))
              .pipe(takeUntil(this.destroy$)).subscribe(() => this.loadData());
      }
  }

  generateReport() {
      this.isGeneratingReport = true;
      this.ttdfService.generateReport(this.reportOptions).pipe(takeUntil(this.destroy$)).subscribe({
          next: (blob) => {
              const url = window.URL.createObjectURL(blob);
              const a = document.createElement('a');
              a.href = url;
              a.download = `TTDF_Report_${this.project.ttdfGrantId}.pdf`;
              document.body.appendChild(a);
              a.click();
              document.body.removeChild(a);
              this.isGeneratingReport = false;
          },
          error: () => this.isGeneratingReport = false
      });
  }
}
