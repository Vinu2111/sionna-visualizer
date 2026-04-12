import { Component, OnInit, OnDestroy, ViewChild, ElementRef, AfterViewInit } from '@angular/core';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { CommonModule } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTabsModule } from '@angular/material/tabs';
import { MatInputModule } from '@angular/material/input';
import Chart from 'chart.js/auto';
import { WorkspaceService } from './workspace.service';
import { Comment, Annotation, Version } from './workspace.interfaces';

@Component({
  selector: 'app-workspace-detail',
  standalone: true,
  imports: [
    CommonModule, FormsModule, MatCardModule, MatButtonModule,
    MatIconModule, MatTabsModule, MatInputModule
  ],
  templateUrl: './workspace-detail.component.html',
  styleUrls: ['./workspace-detail.component.scss']
})
export class WorkspaceDetailComponent implements OnInit, AfterViewInit, OnDestroy {
  destroy$ = new Subject<void>();
  
  simulationId!: number;
  comments: Comment[] = [];
  annotations: Annotation[] = [];
  versions: Version[] = [];
  
  newCommentText = '';
  replyingTo: number | null = null;
  
  @ViewChild('berCanvas') berCanvas!: ElementRef<HTMLCanvasElement>;
  chartInstance: any = null;

  activeSNR: number | null = null;
  activeBER: number | null = null;

  constructor(
      private route: ActivatedRoute,
      private workspaceService: WorkspaceService
  ) {}

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }

  ngOnInit() {
      this.simulationId = Number(this.route.snapshot.paramMap.get('id'));
      this.loadData();
  }

  ngAfterViewInit() {
      setTimeout(() => this.renderChart(), 100);
  }

  loadData() {
      this.workspaceService.getSimulationComments(this.simulationId).pipe(takeUntil(this.destroy$)).subscribe(c => this.comments = c);
      this.workspaceService.getAnnotations(this.simulationId).pipe(takeUntil(this.destroy$)).subscribe(a => {
          this.annotations = a;
          this.renderChart(); // Re-render to inject pins natively securely securely
      });
      this.workspaceService.getVersionHistory(this.simulationId).pipe(takeUntil(this.destroy$)).subscribe(v => this.versions = v);
  }

  // Parses mock theoretical geometry entirely mirroring standard single-dashboard securely cleanly
  renderChart() {
      if(!this.berCanvas) return;
      if(this.chartInstance) this.chartInstance.destroy();

      const snr = [-10, -5, 0, 5, 10, 15, 20];
      const ber = [1.0, 0.5, 0.1, 0.01, 0.001, 0.0001, 0.00001];

      // Native Chart Plugin dynamically injecting mapped SVG points tracking user comments perfectly smoothly
      const annotationPlugin = {
          id: 'customAnnotations',
          afterDraw: (chart: any) => {
              const ctx = chart.ctx;
              this.annotations.forEach(a => {
                  const x = chart.scales.x.getPixelForValue(a.snrPoint);
                  const y = chart.scales.y.getPixelForValue(a.berPoint);
                  
                  ctx.beginPath();
                  ctx.arc(x, y, 12, 0, 2 * Math.PI);
                  ctx.fillStyle = '#ff5722';
                  ctx.fill();
                  ctx.strokeStyle = '#fff';
                  ctx.lineWidth = 2;
                  ctx.stroke();

                  ctx.fillStyle = '#fff';
                  ctx.font = 'bold 12px Arial';
                  ctx.textAlign = 'center';
                  ctx.textBaseline = 'middle';
                  ctx.fillText(a.pinNumber.toString(), x, y);
              });
          }
      };

      this.chartInstance = new Chart(this.berCanvas.nativeElement, {
          type: 'line',
          data: {
              labels: snr,
              datasets: [{
                  label: `Simulated BER`,
                  data: ber,
                  borderColor: '#3f51b5',
                  borderWidth: 2,
                  pointRadius: 6,
                  pointHoverRadius: 8
              }]
          },
          options: {
              responsive: true,
              maintainAspectRatio: false,
              onClick: (event: any, elements: any[]) => {
                  if (elements.length > 0) {
                      const idx = elements[0].index;
                      this.activeSNR = snr[idx];
                      this.activeBER = ber[idx];
                      const txt = prompt(`Add annotation to SNR: ${this.activeSNR}dB. Note:`);
                      if(txt) {
                          this.workspaceService.addAnnotation(this.simulationId, this.activeSNR, this.activeBER, txt)
                              .pipe(takeUntil(this.destroy$)).subscribe(() => this.loadData());
                      }
                  }
              },
              scales: { 
                  x: { title: { display: true, text: 'SNR (dB)' } },
                  y: { type: 'logarithmic', min: 1e-6, max: 1, title: { display: true, text: 'BER' } }
              }
          },
          plugins: [annotationPlugin]
      });
  }

  postComment() {
      if(!this.newCommentText.trim()) return;
      this.workspaceService.addComment(this.simulationId, this.newCommentText, this.replyingTo || undefined)
          .pipe(takeUntil(this.destroy$)).subscribe(() => {
             this.newCommentText = '';
             this.replyingTo = null;
             this.loadData();
          });
  }

  saveVersion() {
      this.workspaceService.saveVersion(this.simulationId).pipe(takeUntil(this.destroy$)).subscribe(() => this.loadData());
  }
}
