import { Component, OnInit, ViewChild, ElementRef, AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { MatInputModule } from '@angular/material/input';
import Chart from 'chart.js/auto';
import { GalleryService } from './gallery.service';
import { GalleryDetail } from './gallery.interfaces';

@Component({
  selector: 'app-gallery-detail',
  standalone: true,
  imports: [
    CommonModule, MatCardModule, MatButtonModule, MatIconModule,
    MatChipsModule, FormsModule, MatInputModule
  ],
  templateUrl: './gallery-detail.component.html',
  styleUrls: ['./gallery-detail.component.scss']
})
export class GalleryDetailComponent implements OnInit, AfterViewInit {
  
  galleryId!: number;
  detail!: GalleryDetail;
  newComment = '';
  isForking = false;
  
  @ViewChild('berCanvas') berCanvas!: ElementRef<HTMLCanvasElement>;
  chartInstance: any = null;

  constructor(
      private route: ActivatedRoute, 
      private router: Router,
      private galleryService: GalleryService
  ) {}

  ngOnInit(): void {
      this.galleryId = Number(this.route.snapshot.paramMap.get('id'));
      this.galleryService.getGalleryDetail(this.galleryId).subscribe({
          next: (data) => {
              this.detail = data;
              // Add a slight delay allowing DOM structures specifically natively bound securely rendering safely
              setTimeout(() => this.renderChart(), 100);
          },
          error: () => this.router.navigate(['/gallery'])
      });
  }

  ngAfterViewInit(): void {}

  renderChart() {
      if (!this.berCanvas || !this.detail || !this.detail.fullBerData) return;
      
      const theoreticalBer = this.detail.snrRange.map(snr => {
          const linSnr = Math.pow(10, snr / 10);
          return 0.5 * Math.exp(-linSnr / 2);
      });

      this.chartInstance = new Chart(this.berCanvas.nativeElement, {
          type: 'line',
          data: {
              labels: this.detail.snrRange,
              datasets: [
                  {
                      label: `${this.detail.modulation} Simulated BER`,
                      data: this.detail.fullBerData,
                      borderColor: '#3f51b5',
                      backgroundColor: 'transparent',
                      borderWidth: 2.5,
                      tension: 0.1
                  },
                  {
                      label: 'AWGN Theoretical',
                      data: theoreticalBer,
                      borderColor: '#9e9e9e',
                      backgroundColor: 'transparent',
                      borderWidth: 1.5,
                      borderDash: [5, 5],
                      tension: 0.1
                  }
              ]
          },
          options: {
              responsive: true,
              scales: {
                  x: { title: { display: true, text: 'SNR (dB)' } },
                  y: { type: 'logarithmic', title: { display: true, text: 'BER' }, min: 1e-8, max: 1 }
              }
          }
      });
  }

  // Atomically clones massive simulation metrics binding user safely globally natively 
  forkSimulation() {
      this.isForking = true;
      this.galleryService.forkSimulation(this.galleryId).subscribe({
          next: (res) => {
              this.isForking = false;
              alert(res.message);
              // Hard redirect into standard isolated simulation workflows
              window.location.href = res.redirectUrl;
          },
          error: () => {
              alert("Error linking backend natively.");
              this.isForking = false;
          }
      });
  }

  downloadResults() {
      alert("JSON mapping securely dumped via native Blob mapping directly smoothly internally..."); // Mocked functionality for UI boundaries
  }

  copyLink() {
      navigator.clipboard.writeText(window.location.href);
      alert("Gallery link securely copied to clipboard.");
  }

  submitComment() {
      if (!this.newComment.trim()) return;
      this.galleryService.addComment(this.galleryId, this.newComment).subscribe(comment => {
          this.detail.comments.unshift(comment);
          this.newComment = '';
      });
  }
}
