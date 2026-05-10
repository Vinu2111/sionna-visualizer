import { Injectable } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { SimulationParameter } from '../models/simulation-parameter.interface';

@Injectable({
  providedIn: 'root'
})
export class LatexGeneratorService {

  constructor(private snackBar: MatSnackBar) {}

  // Reads simulation result object and returns array of parameters
  extractParameters(simulationResult: any): SimulationParameter[] {
    const params: SimulationParameter[] = [];
    
    if (!simulationResult) return params;

    // Safely extract core parameters with simple fallbacks
    params.push({ parameterName: 'Carrier Frequency', value: simulationResult.frequency || '28', unit: 'GHz' });
    params.push({ parameterName: 'Modulation', value: simulationResult.modulation || 'QPSK', unit: '--' });
    params.push({ parameterName: 'Number of Antennas', value: simulationResult.antennas || '16', unit: '--' });
    params.push({ parameterName: 'Bandwidth', value: simulationResult.bandwidth || '100', unit: 'MHz' });
    
    // Format SNR range cleanly
    const minSnr = simulationResult.minSnr != null ? simulationResult.minSnr : '-10';
    const maxSnr = simulationResult.maxSnr != null ? simulationResult.maxSnr : '30';
    params.push({ parameterName: 'SNR Range', value: `${minSnr} to ${maxSnr}`, unit: 'dB' });
    
    // Add additional critical details
    params.push({ parameterName: 'Channel Model', value: simulationResult.channelModel || 'AWGN', unit: '--' });
    params.push({ parameterName: 'Monte Carlo Trials', value: simulationResult.trials || '10000', unit: '--' });
    params.push({ parameterName: 'Simulation Duration', value: simulationResult.duration || '4.2', unit: 'seconds' });

    // Include optional fields only if they exist in the result object
    if (simulationResult.txPower) {
        params.push({ parameterName: 'TX Power', value: simulationResult.txPower, unit: 'dBm' });
    }
    if (simulationResult.noiseFigure) {
        params.push({ parameterName: 'Noise Figure', value: simulationResult.noiseFigure, unit: 'dB' });
    }

    return params;
  }

  // Takes parameter array and returns full LaTeX string using standard booktabs format
  generateLatexTable(parameters: SimulationParameter[]): string {
    let latex = '\\begin{table}[h]\n';
    latex += '\\centering\n';
    latex += '\\caption{Simulation Parameters}\n';
    latex += '\\label{tab:sim-params}\n';
    latex += '\\begin{tabular}{lll}\n';
    
    // Include top table rule
    latex += '\\toprule\n';
    latex += '\\textbf{Parameter} & \\textbf{Value} & \\textbf{Unit} \\\\\n';
    latex += '\\midrule\n';

    // Loop through each dynamic parameter and add it to the table string
    parameters.forEach(param => {
      latex += `${param.parameterName} & ${param.value} & ${param.unit} \\\\\n`;
    });

    // Close the table with a bottom rule
    latex += '\\bottomrule\n';
    latex += '\\end{tabular}\n';
    latex += '\\end{table}';

    return latex;
  }

  // Copies generated LaTeX to clipboard and shows snackbar confirmation to the researcher
  copyToClipboard(latexString: string): void {
    navigator.clipboard.writeText(latexString).then(() => {
      // Notify the user of successful copy
      this.snackBar.open('LaTeX copied successfully! Paste directly into Overleaf.', 'Dismiss', {
        duration: 4000,
        horizontalPosition: 'end',
        verticalPosition: 'bottom'
      });
    }).catch(err => {
      // Handle rare copy failure errors gracefully
      console.error('Could not copy text: ', err);
      this.snackBar.open('Failed to copy to clipboard', 'Close', { duration: 3000 });
    });
  }
}
