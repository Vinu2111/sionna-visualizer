import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef, MatDialogModule } from '@angular/material/dialog';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';
import { LatexGeneratorService } from '../../services/latex-generator.service';
import { SimulationParameter } from '../../models/simulation-parameter.interface';

@Component({
  selector: 'app-latex-table-generator',
  standalone: true,
  imports: [
    CommonModule,
    MatDialogModule,
    MatButtonModule,
    MatIconModule,
    MatTableModule
  ],
  templateUrl: './latex-table-generator.component.html',
  styleUrls: ['./latex-table-generator.component.scss']
})
export class LatexTableGeneratorComponent implements OnInit {
  
  parameters: SimulationParameter[] = [];
  generatedLatex: string = '';
  displayedColumns: string[] = ['parameterName', 'value', 'unit'];

  constructor(
    public dialogRef: MatDialogRef<LatexTableGeneratorComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private latexService: LatexGeneratorService
  ) {}

  ngOnInit(): void {
    // Read the passed simulation result and extract mapped parameter values
    const simulationResult = this.data?.simulationResult;
    this.parameters = this.latexService.extractParameters(simulationResult);
    
    // Auto-generate the LaTeX string using the extracted parameters immediately on load
    this.generatedLatex = this.latexService.generateLatexTable(this.parameters);
  }

  // Executes the copy to clipboard action via the injected service
  copyLatex(): void {
    this.latexService.copyToClipboard(this.generatedLatex);
  }
}
