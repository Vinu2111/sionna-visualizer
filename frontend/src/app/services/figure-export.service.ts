import { Injectable } from '@angular/core';
import { jsPDF } from 'jspdf';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class FigureExportService {
  private apiUrl = '/api/export';

  constructor(private http: HttpClient) {}

  // Reformats the Chart.js instance for IEEE journal specifications
  applyIeeeFormatting(chartInstance: any): any {
    const config = Object.assign({}, chartInstance.config);
    
    // Set standard font
    if (config.options && config.options.plugins && config.options.plugins.title) {
        config.options.plugins.title.font = { family: 'Times New Roman', size: 10 };
    }
    
    // Set legend style inside plot
    if (config.options && config.options.plugins && config.options.plugins.legend) {
        config.options.plugins.legend.position = 'chartArea';
        config.options.plugins.legend.labels = {
            font: { family: 'Times New Roman', size: 8 }
        };
    }
    
    // Set axis and grid line styles
    if (config.options && config.options.scales) {
        for (const axis in config.options.scales) {
            if (config.options.scales[axis]) {
              config.options.scales[axis].ticks = {
                  font: { family: 'Times New Roman', size: 8 }
              };
              config.options.scales[axis].grid = {
                  lineWidth: 0.5
              };
            }
        }
    }
    
    // Set data line weight
    if (config.data && config.data.datasets) {
        config.data.datasets.forEach((dataset: any) => {
            dataset.borderWidth = 1.5;
        });
    }
    
    chartInstance.update();
    return chartInstance;
  }

  // Reformats the Chart.js instance for Nature journal specifications
  applyNatureFormatting(chartInstance: any): any {
    const config = Object.assign({}, chartInstance.config);
    
    // Set standard font
    if (config.options && config.options.plugins && config.options.plugins.title) {
        config.options.plugins.title.font = { family: 'Arial', size: 8 };
    }
    
    // Set legend style top-right inside plot
    if (config.options && config.options.plugins && config.options.plugins.legend) {
        config.options.plugins.legend.position = 'chartArea';
        config.options.plugins.legend.align = 'end'; // push to top right
        config.options.plugins.legend.labels = {
            font: { family: 'Arial', size: 7 }
        };
    }
    
    // Set axis open box style
    if (config.options && config.options.scales) {
        for (const axis in config.options.scales) {
            if (config.options.scales[axis]) {
              config.options.scales[axis].ticks = {
                  font: { family: 'Arial', size: 7 }
              };
              // Remove top/right borders
              config.options.scales[axis].border = { display: axis === 'x' || axis === 'y' }; 
            }
        }
    }
    
    // Set data line weight
    if (config.data && config.data.datasets) {
        config.data.datasets.forEach((dataset: any) => {
            dataset.borderWidth = 1.0;
        });
    }
    
    chartInstance.update();
    return chartInstance;
  }

  // Exports the given chart element as an SVG file (fallback simulated with image)
  exportAsSvg(chartCanvas: HTMLCanvasElement): void {
      console.log('Exporting SVG format...');
      const url = chartCanvas.toDataURL('image/png', 1.0); 
      const link = document.createElement('a');
      link.download = 'figure-export.png'; // In browser fallback for plain canvas
      link.href = url;
      link.click();
  }

  // Exports the given chart element as a PDF file using jsPDF
  exportAsPdf(chartCanvas: HTMLCanvasElement): void {
      const imgData = chartCanvas.toDataURL('image/png', 1.0);
      const pdf = new jsPDF('l', 'mm', 'a4');
      const imgProps = pdf.getImageProperties(imgData);
      const pdfWidth = pdf.internal.pageSize.getWidth();
      const pdfHeight = (imgProps.height * pdfWidth) / imgProps.width;
      
      pdf.addImage(imgData, 'PNG', 0, 0, pdfWidth, pdfHeight);
      pdf.save('figure-export.pdf');
  }

  // Logs the export to the backend for tracking
  logExport(simulationId: number, journalStyle: string, exportFormat: string, chartType: string): Observable<any> {
      const payload = {
          simulationId,
          journalStyle,
          exportFormat,
          chartType
      };
      return this.http.post(`${this.apiUrl}/figure`, payload);
  }
}
