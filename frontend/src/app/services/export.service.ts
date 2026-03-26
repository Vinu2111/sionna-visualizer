import { Injectable } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class ExportService {

  private today(): string {
    return new Date().toISOString().slice(0, 10);
  }

  // ─── Low-level file download trigger ────────────────────────────────────────

  downloadCSV(filename: string, csvContent: string): void {
    const bom = '\uFEFF'; // Excel-compatible UTF-8 BOM
    const blob = new Blob([bom + csvContent], { type: 'text/csv;charset=utf-8;' });
    this.triggerDownload(URL.createObjectURL(blob), filename);
  }

  downloadJSON(filename: string, data: object): void {
    const json = JSON.stringify(data, null, 2);
    const blob = new Blob([json], { type: 'application/json' });
    this.triggerDownload(URL.createObjectURL(blob), filename);
  }

  private triggerDownload(url: string, filename: string): void {
    const a = document.createElement('a');
    a.href = url;
    a.download = filename;
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    URL.revokeObjectURL(url);
  }

  // ─── PNG from Chart.js canvas ────────────────────────────────────────────────

  downloadChartPng(canvasId: string, filename: string): void {
    const canvas = document.querySelector(`canvas`) as HTMLCanvasElement;
    if (!canvas) return;
    const url = canvas.toDataURL('image/png', 1.0);
    this.triggerDownload(url, filename);
  }

  downloadCanvasPng(canvas: HTMLCanvasElement, filename: string): void {
    const url = canvas.toDataURL('image/png', 1.0);
    this.triggerDownload(url, filename);
  }

  // ─── CSV generators ──────────────────────────────────────────────────────────

  generateBerCsv(result: any): string {
    const lines: string[] = [
      '# Sionna Visualizer — BER vs SNR',
      `# Modulation: ${result.modulation ?? ''}, Code Rate: ${result.code_rate ?? ''}`,
      'snr_db,ber_theoretical,ber_simulated'
    ];
    const snr: number[] = result.snr_db ?? [];
    const th: number[]  = result.ber_theoretical ?? [];
    const sim: number[] = result.ber_simulated ?? [];
    for (let i = 0; i < snr.length; i++) {
      lines.push(`${snr[i]},${th[i] ?? ''},${sim[i] ?? ''}`);
    }
    return lines.join('\n');
  }

  generateBeamCsv(result: any): string {
    const lines: string[] = [
      '# Sionna Visualizer — Beam Pattern',
      `# Antennas: ${result.num_antennas ?? ''}, Steering: ${result.steering_angle ?? ''}deg, Freq: ${result.frequency_ghz ?? ''}GHz`,
      'angle_degrees,pattern_db'
    ];
    const angles: number[]  = result.angles ?? [];
    const pattern: number[] = result.pattern_db ?? [];
    for (let i = 0; i < angles.length; i++) {
      lines.push(`${angles[i]},${pattern[i] ?? ''}`);
    }
    return lines.join('\n');
  }

  generateModComparisonCsv(result: any): string {
    const lines: string[] = [
      '# Sionna Visualizer — Modulation Comparison',
      `# SNR Range: ${result.snr_min ?? ''} to ${result.snr_max ?? ''} dB`,
      'snr_db,bpsk_ber,qpsk_ber,qam16_ber,qam64_ber'
    ];
    const snr: number[]  = result.snr_db ?? [];
    const bpsk: number[] = result.bpsk ?? [];
    const qpsk: number[] = result.qpsk ?? [];
    const q16: number[]  = result.qam16 ?? [];
    const q64: number[]  = result.qam64 ?? [];
    for (let i = 0; i < snr.length; i++) {
      lines.push(`${snr[i]},${bpsk[i] ?? ''},${qpsk[i] ?? ''},${q16[i] ?? ''},${q64[i] ?? ''}`);
    }
    return lines.join('\n');
  }

  generateCapacityCsv(result: any): string {
    // Build column headers from whatever bandwidths came back
    const curves: any[] = result.capacity_curves ?? [];
    const bwHeaders = curves.map((c: any) => `capacity_${c.bandwidth_mhz}mhz_gbps`).join(',');
    const lines: string[] = [
      '# Sionna Visualizer — Shannon Channel Capacity',
      `# SNR Range: ${result.snr_min ?? ''} to ${result.snr_max ?? ''} dB`,
      `snr_db,spectral_efficiency_bits_s_hz,${bwHeaders}`
    ];
    const snr: number[] = result.snr_db ?? [];
    const se: number[]  = result.spectral_efficiency ?? [];
    for (let i = 0; i < snr.length; i++) {
      const capVals = curves.map((c: any) => c.capacity_gbps[i] ?? '').join(',');
      lines.push(`${snr[i]},${se[i] ?? ''},${capVals}`);
    }
    return lines.join('\n');
  }

  generatePathLossCsv(result: any): string {
    const lines: string[] = [
      '# Sionna Visualizer — Path Loss',
      `# Environment: ${result.summary?.environment ?? 'urban'}, Frequency: ${result.summary?.frequency_ghz ?? 28} GHz`,
      'path_id,distance_m,path_loss_db,path_type,delay_ns'
    ];
    const paths: any[] = result.paths ?? [];
    for (const p of paths) {
      lines.push(`${p.path_id},${p.distance_m ?? ''},${p.path_loss_db ?? ''},${p.path_type ?? ''},${p.delay_ns ?? ''}`);
    }
    return lines.join('\n');
  }

  generateRayDirectionsCsv(result: any): string {
    const lines: string[] = [
      '# Sionna Visualizer — Ray Directions',
      `# Environment: ${result?.environment ?? 'urban'}, Frequency: ${result?.frequency_ghz ?? 28} GHz`,
      'path_id,path_type,path_loss_db,delay_ns,departure_azimuth_deg,departure_elevation_deg,arrival_azimuth_deg,arrival_elevation_deg'
    ];
    const paths: any[] = result?.paths ?? [];
    for (const p of paths) {
      lines.push(`${p.path_id},${p.path_type ?? ''},${p.path_loss_db ?? ''},${p.delay_ns ?? ''},${p.departure_azimuth_deg ?? ''},${p.departure_elevation_deg ?? ''},${p.arrival_azimuth_deg ?? ''},${p.arrival_elevation_deg ?? ''}`);
    }
    return lines.join('\n');
  }

  // ─── JSON export with _metadata wrapper ─────────────────────────────────────

  wrapWithMetadata(simulationType: string, data: any): object {
    return {
      _metadata: {
        exported_from: 'Sionna Visualizer',
        export_date: new Date().toISOString(),
        live_url: 'https://sionna-visualizer.vercel.app',
        github: 'https://github.com/Vinu2111/sionna-visualizer',
        simulation_type: simulationType
      },
      ...data
    };
  }

  // ─── Clipboard ───────────────────────────────────────────────────────────────

  copyToClipboard(data: object): Promise<void> {
    return navigator.clipboard.writeText(JSON.stringify(data, null, 2));
  }

  // ─── Bulk download with stagger delay ────────────────────────────────────────

  async bulkDownload(items: { filename: string; content: string; type: 'csv' | 'json' }[], delayMs = 200): Promise<void> {
    for (const item of items) {
      if (item.type === 'csv') {
        this.downloadCSV(item.filename, item.content);
      } else {
        this.downloadJSON(item.filename, JSON.parse(item.content));
      }
      await new Promise(r => setTimeout(r, delayMs));
    }
  }

  // ─── Filename helpers ────────────────────────────────────────────────────────

  berFilename(modulation: string, ext: string): string {
    return `sionna-ber-snr-${(modulation || 'qpsk').toLowerCase()}-${this.today()}.${ext}`;
  }
  beamFilename(numAntennas: number, ext: string): string {
    return `sionna-beam-pattern-${numAntennas}ant-${this.today()}.${ext}`;
  }
  modFilename(ext: string): string {
    return `sionna-mod-comparison-${this.today()}.${ext}`;
  }
  capFilename(ext: string): string {
    return `sionna-channel-capacity-${this.today()}.${ext}`;
  }
  comparisonFilename(type1: string, type2: string, ext: string): string {
    return `sionna-comparison-${type1.toLowerCase()}-vs-${type2.toLowerCase()}-${this.today()}.${ext}`;
  }
  allSimsFilename(): string {
    return `sionna-all-simulations-${this.today()}.json`;
  }
}
