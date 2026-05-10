import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatChipsModule } from '@angular/material/chips';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatExpansionModule } from '@angular/material/expansion';
import { NlSimulationService } from './nl-simulation.service';
import { ParsedSimulationParams, ParseHistory, EXAMPLE_QUERIES } from './nl-simulation.interfaces';

@Component({
  selector: 'app-nl-simulation',
  standalone: true,
  imports: [
    CommonModule, FormsModule, ReactiveFormsModule,
    MatCardModule, MatButtonModule, MatIconModule, MatInputModule,
    MatChipsModule, MatSelectModule, MatButtonToggleModule,
    MatProgressSpinnerModule, MatExpansionModule
  ],
  templateUrl: './nl-simulation.component.html',
  styleUrls: ['./nl-simulation.component.scss']
})
export class NlSimulationComponent implements OnInit {

  // Input state
  queryText = '';
  readonly MAX_CHARS = 500;
  exampleQueries = EXAMPLE_QUERIES;

  // Parse state
  isParsing = false;
  parseError = '';
  parsedParams: ParsedSimulationParams | null = null;

  // History
  history: ParseHistory[] = [];

  // Editable form built from parsed params
  paramForm!: FormGroup;

  // Dropdown options for the form
  frequencyOptions = [2.4, 5, 28, 39, 60, 77];
  antennaOptions = [1, 2, 4, 8, 16, 32];

  constructor(private service: NlSimulationService, private fb: FormBuilder) {}

  ngOnInit() {
    this.service.getParseHistory().subscribe(h => this.history = h);
    this.initForm();
  }

  initForm(params?: ParsedSimulationParams) {
    this.paramForm = this.fb.group({
      frequency:    [params?.frequency ?? null],
      channelModel: [params?.channelModel ?? null],
      modulation:   [params?.modulation ?? null],
      numAntennasTx:[params?.numAntennasTx ?? 1],
      numAntennasRx:[params?.numAntennasRx ?? 1],
      snrMin:       [params?.snrMin ?? -10],
      snrMax:       [params?.snrMax ?? 30],
      simulationType:[params?.simulationType ?? 'BER_SIMULATION'],
      environment:  [params?.environment ?? null]
    });
  }

  // Fill textarea from a quick-chip click
  fillExample(full: string) {
    this.queryText = full;
    this.parsedParams = null;
    this.parseError = '';
  }

  // Main parse action — sends query to backend → Claude AI
  parse() {
    if (!this.queryText.trim()) return;
    this.isParsing = true;
    this.parsedParams = null;
    this.parseError = '';

    this.service.parseNaturalLanguage(this.queryText).subscribe({
      next: (result) => {
        this.parsedParams = result;
        this.initForm(result);
        this.isParsing = false;
        // Reload history to show new entry
        this.service.getParseHistory().subscribe(h => this.history = h);
      },
      error: (err) => {
        this.parseError = err.error?.error || 'Parsing failed. Please try again.';
        this.isParsing = false;
      }
    });
  }

  // Reload a historical query into the textarea
  loadFromHistory(record: ParseHistory) {
    this.queryText = record.queryText;
    this.parsedParams = null;
    this.parseError = '';
  }

  isAiFilled(field: string): boolean {
    return this.parsedParams?.aiFilled?.includes(field) ?? false;
  }

  get charCount() { return this.queryText.length; }

  getConfidenceColor(): string {
    if (!this.parsedParams) return '';
    const map: Record<string, string> = { HIGH: '#4caf50', MEDIUM: '#ff9800', LOW: '#f44336' };
    return map[this.parsedParams.confidence] ?? '#999';
  }

  getConfidenceIcon(): string {
    const map: Record<string, string> = { HIGH: 'check_circle', MEDIUM: 'warning', LOW: 'error' };
    return map[this.parsedParams?.confidence ?? ''] ?? 'help';
  }

  runSimulation() {
    // Routing to existing simulation engine based on extracted type
    const params = this.paramForm.value;
    console.log('Running simulation with AI-extracted params:', params);
    // TODO: Route to /api/simulations with params mapped to SimulationRequest DTO
  }
}
