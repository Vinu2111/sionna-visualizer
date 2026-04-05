import { Component, OnInit, ViewChildren, QueryList, ElementRef, AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { FormsModule } from '@angular/forms';
import { Subject } from 'rxjs';
import { debounceTime } from 'rxjs/operators';
import Chart from 'chart.js/auto';
import { GalleryService } from './gallery.service';
import { GalleryItem, GalleryFilters, GalleryPage } from './gallery.interfaces';

@Component({
  selector: 'app-public-gallery',
  standalone: true,
  imports: [
    CommonModule, RouterModule, MatCardModule, MatButtonModule,
    MatIconModule, MatChipsModule, MatPaginatorModule, FormsModule
  ],
  templateUrl: './public-gallery.component.html',
  styleUrls: ['./public-gallery.component.scss']
})
export class PublicGalleryComponent implements OnInit, AfterViewInit {

  items: GalleryItem[] = [];
  totalItems = 0;
  pageSize = 10;
  currentPage = 0;

  filters: GalleryFilters = {
     channelModel: 'All', modulation: 'All', frequency: 0, 
     sortBy: 'newest', searchQuery: ''
  };

  searchSubject = new Subject<string>();

  @ViewChildren('miniChart') miniCharts!: QueryList<ElementRef<HTMLCanvasElement>>;
  chartInstances: any[] = [];

  constructor(private galleryService: GalleryService, private router: Router) {
      // Debounce logic preventing aggressive API spam dynamically securely
      this.searchSubject.pipe(debounceTime(300)).subscribe(query => {
          this.filters.searchQuery = query;
          this.currentPage = 0;
          this.loadGallery();
      });
  }

  ngOnInit(): void {
      this.loadGallery();
  }

  ngAfterViewInit(): void {
      this.miniCharts.changes.subscribe(() => {
          this.renderMiniCharts();
      });
  }

  loadGallery(): void {
      this.galleryService.getGalleryItems(this.filters, this.currentPage).subscribe(page => {
          this.items = page.content;
          this.totalItems = page.totalElements;
          // Mini charts update via AfterViewInit natively smoothly 
      });
  }

  onSearchChange(val: string) {
      this.searchSubject.next(val);
  }

  onFilterChange() {
      this.currentPage = 0;
      this.loadGallery();
  }

  onPageChange(event: PageEvent) {
      this.currentPage = event.pageIndex;
      this.pageSize = event.pageSize;
      this.loadGallery();
  }

  viewDetails(id: number) {
      this.router.navigate(['/gallery', id]);
  }

  // Heavy dynamic generation mapped cleanly without overhead securely 
  renderMiniCharts() {
      // Clean garbage collections mapped before mapping new configurations
      this.chartInstances.forEach(c => c.destroy());
      this.chartInstances = [];

      this.miniCharts.forEach((canvasRef, index) => {
          const item = this.items[index];
          if(item && item.berValues && item.snrRange) {
              const ctx = canvasRef.nativeElement;
              const chart = new Chart(ctx, {
                  type: 'line',
                  data: {
                      labels: item.snrRange,
                      datasets: [{
                          data: item.berValues,
                          borderColor: this.getColorForModel(item.channelModel),
                          borderWidth: 2,
                          pointRadius: 0,
                          tension: 0.1
                      }]
                  },
                  options: {
                      responsive: true,
                      maintainAspectRatio: false,
                      scales: { x: { display: false }, y: { display: false, type: 'logarithmic' } },
                      plugins: { legend: { display: false }, tooltip: { enabled: false } },
                      layout: { padding: 0 }
                  }
              });
              this.chartInstances.push(chart);
          }
      });
  }

  // Generates physical logic bounds smoothly
  getColorForModel(model: string): string {
      if(model.includes('CDL')) return '#ff5722';
      if(model.includes('TDL')) return '#4caf50';
      return '#3f51b5'; // AWGN standard default logically securely natively
  }
}
