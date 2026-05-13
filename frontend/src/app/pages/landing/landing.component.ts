import { Component, OnInit, AfterViewInit, OnDestroy, Inject, PLATFORM_ID } from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { Router, RouterModule } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { AuthService } from '../../services/auth.service';
import { environment } from '../../../environments/environment';
import Chart from 'chart.js/auto';

/** Particle data for the animated hero background */
interface Particle {
  x: number;
  size: number;
  speed: number;
  delay: number;
  color: string;
}

@Component({
  selector: 'app-landing',
  standalone: true,
  imports: [CommonModule, FormsModule, MatButtonModule, MatCardModule, MatIconModule, RouterModule],
  templateUrl: './landing.component.html',
  styleUrl: './landing.component.scss'
})
export class LandingComponent implements OnInit, AfterViewInit, OnDestroy {

  // ── Particle animation data ───────────────────────────────────────────────
  particles: Particle[] = [];

  // ── Demo section state ────────────────────────────────────────────────────
  modulationOptions = ['BPSK', 'QPSK', '16QAM', '64QAM'];
  selectedModulation = 'QPSK';
  snrMin = -10;
  snrMax = 30;
  isLoading = false;
  private previewChart: Chart | null = null;

  // ── Curl example (stored here to avoid {{ }} parsing issues in template) ──
  curlExample = `curl -X POST \\
  https://sionna-backend.onrender.com/v1/api/track \\
  -H "X-API-Key: YOUR_KEY" \\
  -d '{"modulation":"QPSK","snr":[0,5,10]}'`;

  // ── Feature cards data ────────────────────────────────────────────────────
  features = [
    {
      icon: '📊',
      title: 'Interactive Charts',
      description: 'BER curves, beam patterns, channel capacity, path loss — all interactive and exportable.'
    },
    {
      icon: '🔗',
      title: 'Shareable URLs',
      description: 'Every simulation gets a public URL. Share results with supervisors and collaborators.'
    },
    {
      icon: '📄',
      title: 'Publication Ready',
      description: 'IEEE and Nature figure export, LaTeX parameter tables, reproducibility packages in one click.'
    },
    {
      icon: '🐍',
      title: 'Python SDK',
      description: 'pip install sionna-visualizer — One line to visualize any simulation result.'
    },
    {
      icon: '🤝',
      title: 'Team Workspace',
      description: 'Collaborate with your lab. Comments, annotations, and version history on every simulation.'
    },
    {
      icon: '🇮🇳',
      title: 'Indian Institutional',
      description: 'TTDF milestone tracking and Bharat 6G Alliance reporting built specifically for Indian researchers.'
    }
  ];

  // ── Intersection observer for scroll animations ───────────────────────────
  private observer: IntersectionObserver | null = null;
  private isBrowser: boolean;

  constructor(
    private router: Router,
    private authService: AuthService,
    private http: HttpClient,
    @Inject(PLATFORM_ID) platformId: Object
  ) {
    this.isBrowser = isPlatformBrowser(platformId);
  }

  ngOnInit(): void {
    // Redirect authenticated users to dashboard
    if (this.authService.isLoggedIn()) {
      this.router.navigate(['/dashboard']);
      return;
    }

    // Generate 50 floating particles with random properties
    this.generateParticles();
  }

  ngAfterViewInit(): void {
    if (!this.isBrowser) return;

    // Set up Intersection Observer for scroll-triggered fade-in animations
    this.setupScrollAnimations();

    // Set up counter animation for stats
    this.setupCounterAnimation();

    // Initialize the preview chart with default QPSK data
    setTimeout(() => this.runPreview(), 300);
  }

  ngOnDestroy(): void {
    this.observer?.disconnect();
    this.previewChart?.destroy();
  }

  // ── Generate particles for hero background ────────────────────────────────
  private generateParticles(): void {
    const teal = '#00C2CB';
    const purple = '#7C3AED';
    this.particles = [];

    for (let i = 0; i < 50; i++) {
      this.particles.push({
        x: Math.random() * 100,
        size: 2 + Math.random() * 4,
        speed: 8 + Math.random() * 16,
        delay: Math.random() * 12,
        color: Math.random() > 0.5 ? teal : purple
      });
    }
  }

  // ── Animation 2: Scroll-triggered fade-in ─────────────────────────────────
  private setupScrollAnimations(): void {
    this.observer = new IntersectionObserver(
      (entries) => {
        entries.forEach((entry) => {
          if (entry.isIntersecting) {
            entry.target.classList.add('visible');
          }
        });
      },
      { threshold: 0.1 }
    );

    // Observe all sections with .fade-section class
    document.querySelectorAll('.fade-section').forEach((section) => {
      this.observer!.observe(section);
    });
  }

  // ── Animation 6: Counter animation for stats ─────────────────────────────
  private setupCounterAnimation(): void {
    const counterObserver = new IntersectionObserver(
      (entries) => {
        entries.forEach((entry) => {
          if (entry.isIntersecting) {
            const counters = entry.target.querySelectorAll('.stat-number');
            counters.forEach((counter: Element) => {
              const el = counter as HTMLElement;
              const target = parseInt(el.getAttribute('data-target') || '0', 10);
              if (target === 0) return;

              let current = 0;
              const increment = Math.ceil(target / 40);
              const timer = setInterval(() => {
                current += increment;
                if (current >= target) {
                  current = target;
                  clearInterval(timer);
                }
                el.textContent = String(current);
              }, 30);
            });
            counterObserver.unobserve(entry.target);
          }
        });
      },
      { threshold: 0.3 }
    );

    const statsRow = document.querySelector('.stats-row');
    if (statsRow) {
      counterObserver.observe(statsRow);
    }
  }

  // ── Demo: Select modulation ───────────────────────────────────────────────
  selectModulation(mod: string): void {
    this.selectedModulation = mod;
  }

  // ── Demo: Slider change ───────────────────────────────────────────────────
  onSliderChange(): void {
    // Slider values are bound via ngModel, no extra logic needed
  }

  // ── Demo: Run preview simulation ──────────────────────────────────────────
  runPreview(): void {
    this.isLoading = true;

    const url = `${environment.apiUrl}/api/public/simulate/preview?modulation=${this.selectedModulation}&snrMin=${this.snrMin}&snrMax=${this.snrMax}`;

    this.http.get<any>(url).subscribe({
      next: (data) => {
        this.renderChart(data);
        this.isLoading = false;
      },
      error: () => {
        // Fallback: generate local mock data if API is unreachable
        this.renderChartWithFallback();
        this.isLoading = false;
      }
    });
  }

  // ── Render Chart.js line chart ────────────────────────────────────────────
  private renderChart(data: any): void {
    if (!this.isBrowser) return;

    const canvas = document.getElementById('previewChart') as HTMLCanvasElement;
    if (!canvas) return;

    if (this.previewChart) {
      this.previewChart.destroy();
    }

    const snr: number[] = data.snr_db;
    const berTheory: number[] = data.ber_theoretical;
    const berSim: number[] = data.ber_simulated;

    // Animation 7: Chart draw animation
    this.previewChart = new Chart(canvas, {
      type: 'line',
      data: {
        labels: snr.map((s: number) => s.toFixed(1)),
        datasets: [
          {
            label: 'Theoretical BER',
            data: berTheory,
            borderColor: '#00C2CB',
            backgroundColor: 'rgba(0, 194, 203, 0.1)',
            borderWidth: 2.5,
            pointRadius: 0,
            tension: 0.3,
            fill: false
          },
          {
            label: 'Simulated BER',
            data: berSim,
            borderColor: '#7C3AED',
            backgroundColor: 'rgba(124, 58, 237, 0.1)',
            borderWidth: 2,
            borderDash: [6, 4],
            pointRadius: 0,
            tension: 0.3,
            fill: false
          }
        ]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        animation: {
          duration: 1000,
          easing: 'easeInOutQuart'
        },
        plugins: {
          legend: {
            labels: {
              color: '#ffffff',
              font: { family: 'Inter', size: 12 }
            }
          }
        },
        scales: {
          x: {
            title: {
              display: true,
              text: 'SNR (dB)',
              color: '#999',
              font: { family: 'Inter', size: 12 }
            },
            ticks: { color: '#666', maxTicksLimit: 10 },
            grid: { color: 'rgba(255,255,255,0.05)' }
          },
          y: {
            type: 'logarithmic',
            title: {
              display: true,
              text: 'Bit Error Rate',
              color: '#999',
              font: { family: 'Inter', size: 12 }
            },
            ticks: {
              color: '#666',
              callback: function(value: any) {
                const v = Number(value);
                if ([1, 0.1, 0.01, 0.001, 0.0001, 0.00001, 0.000001].includes(v)) {
                  return v.toExponential(0);
                }
                return '';
              }
            },
            grid: { color: 'rgba(255,255,255,0.05)' },
            min: 1e-7
          }
        }
      }
    });
  }

  // ── Fallback chart if API is unreachable ───────────────────────────────────
  private renderChartWithFallback(): void {
    // Generate local theoretical BER for QPSK as fallback
    const snr: number[] = [];
    const berTheory: number[] = [];
    const berSim: number[] = [];

    for (let s = this.snrMin; s <= this.snrMax; s += 1) {
      snr.push(s);
      const snrLin = Math.pow(10, s / 10);
      const ber = 0.5 * this.erfc(Math.sqrt(snrLin / 2));
      berTheory.push(ber);
      berSim.push(ber * (1 + (Math.random() - 0.5) * 0.16));
    }

    this.renderChart({
      snr_db: snr,
      ber_theoretical: berTheory,
      ber_simulated: berSim
    });
  }

  // Simple erfc approximation for fallback
  private erfc(x: number): number {
    const a1 = 0.254829592;
    const a2 = -0.284496736;
    const a3 = 1.421413741;
    const a4 = -1.453152027;
    const a5 = 1.061405429;
    const p = 0.3275911;
    const t = 1.0 / (1.0 + p * Math.abs(x));
    const y = 1.0 - (((((a5 * t + a4) * t) + a3) * t + a2) * t + a1) * t * Math.exp(-x * x);
    return x >= 0 ? (1 - y) : (1 + y);
  }
}
