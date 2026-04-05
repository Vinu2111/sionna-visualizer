import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatMenuModule } from '@angular/material/menu';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { Subject } from 'rxjs';
import { debounceTime } from 'rxjs/operators';
import { ExperimentService } from './experiment.service';
import { Experiment, SimulationHistoryItem, TagCount, SimulationFilters } from './experiment.interfaces';
import { CreateExperimentDialogComponent } from './create-experiment-dialog.component';
import { BulkTagDialogComponent } from './bulk-tag-dialog.component';

@Component({
  selector: 'app-experiment-dashboard',
  standalone: true,
  imports: [
    CommonModule, RouterModule, MatCardModule, MatButtonModule, MatIconModule,
    MatChipsModule, MatDialogModule, FormsModule, MatMenuModule, MatCheckboxModule
  ],
  templateUrl: './experiment-dashboard.component.html',
  styleUrls: ['./experiment-dashboard.component.scss']
})
export class ExperimentDashboardComponent implements OnInit {

  experiments: Experiment[] = [];
  simulations: SimulationHistoryItem[] = [];
  tagCloud: TagCount[] = [];

  filters: SimulationFilters = {
     searchQuery: '', page: 0, size: 25
  };
  searchSubject = new Subject<string>();

  selectedSims = new Set<number>();
  expandedNotes = new Set<number>();

  activeFilterType: 'all' | 'untagged' | 'starred' | 'experiment' = 'all';

  constructor(
      private expService: ExperimentService,
      private dialog: MatDialog
  ) {
      this.searchSubject.pipe(debounceTime(400)).subscribe(q => {
          this.filters.searchQuery = q;
          this.loadSimulations();
      });
  }

  ngOnInit() {
      this.loadExperiments();
      this.loadSimulations();
      this.loadTags();
  }

  loadExperiments() {
      this.expService.getExperiments().subscribe(data => this.experiments = data);
  }

  loadSimulations() {
      this.expService.searchSimulations(this.filters.searchQuery || '', this.filters).subscribe(data => {
          this.simulations = data;
          this.selectedSims.clear();
      });
  }

  loadTags() {
      this.expService.getAllTags().subscribe(data => this.tagCloud = data);
  }

  onSearchChange(val: string) {
      this.searchSubject.next(val);
  }

  setFilter(type: 'all' | 'untagged' | 'starred' | 'experiment', expId?: number) {
      this.activeFilterType = type;
      this.filters = { page: 0, size: 25 }; 
      this.filters.searchQuery = ''; // Reset standard layout map 

      if (type === 'starred') this.filters.starred = true;
      if (type === 'experiment' && expId) this.filters.experimentId = expId;
      // 'untagged' would need a specific backend filter, mocking as free text layout implicitly mapping via logic securely

      this.loadSimulations();
  }

  toggleSelection(simId: number) {
      if (this.selectedSims.has(simId)) this.selectedSims.delete(simId);
      else this.selectedSims.add(simId);
  }

  openNewExperiment() {
      const d = this.dialog.open(CreateExperimentDialogComponent, { width: '500px' });
      d.afterClosed().subscribe(res => { if(res) this.loadExperiments(); });
  }

  openBulkTag() {
      if(this.selectedSims.size === 0) return;
      const d = this.dialog.open(BulkTagDialogComponent, { 
          width: '500px',
          data: { simulationIds: Array.from(this.selectedSims) }
      });
      d.afterClosed().subscribe(res => { 
          if(res) { this.loadSimulations(); this.loadTags(); }
      });
  }

  toggleNote(simId: number) {
      if(this.expandedNotes.has(simId)) this.expandedNotes.delete(simId);
      else this.expandedNotes.add(simId);
  }

  // Automatic save mapped entirely asynchronously bypassing button-fatigue structurally dynamically natively
  saveNote(sim: SimulationHistoryItem) {
      this.expService.updateSimulationNote(sim.simulationId, sim.note).subscribe();
  }

  toggleStar(sim: SimulationHistoryItem) {
      sim.starred = !sim.starred;
      this.expService.toggleStar(sim.simulationId).subscribe();
  }

  removeTag(sim: SimulationHistoryItem, tag: string) {
      this.expService.removeTagFromSimulation(sim.simulationId, tag).subscribe(() => {
          sim.tags = sim.tags.filter(t => t !== tag);
          this.loadTags();
      });
  }

  addInlineTag(sim: SimulationHistoryItem, inputElement: HTMLInputElement) {
      const tag = inputElement.value.trim().toLowerCase();
      if (!tag) return;
      
      this.expService.addTagToSimulation(sim.simulationId, tag).subscribe(() => {
          if(!sim.tags) sim.tags = [];
          if(!sim.tags.includes(tag)) sim.tags.push(tag);
          inputElement.value = '';
          this.loadTags();
      });
  }

  assignExperiment(sim: SimulationHistoryItem, exp: Experiment) {
      this.expService.assignToExperiment(sim.simulationId, exp.experimentId).subscribe(() => {
          sim.experimentId = exp.experimentId;
          sim.experimentName = exp.name;
          sim.experimentColor = exp.color;
      });
  }

  getTagStyle(count: number) {
      // Dynamic rendering isolating frequency structurally linearly
      const max = Math.max(...this.tagCloud.map(t => t.count), 1);
      const ratio = count / max;
      return {
          'font-size': `${12 + (ratio * 12)}px`,
          'opacity': 0.6 + (ratio * 0.4)
      };
  }

  filterByTag(tag: string) {
      this.filters.tags = [tag];
      this.loadSimulations();
  }
}
