import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SimulationService } from '../../services/simulation.service';
import { ColormapOption } from '../../models/simulation-result.model';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-colormap-selector',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './colormap-selector.component.html',
  styleUrls: ['./colormap-selector.component.css']
})
export class ColormapSelectorComponent implements OnInit {
  @Input() selectedId: string = 'default';
  @Output() selectionChange = new EventEmitter<string>();

  colormaps: ColormapOption[] = [];
  loading = true;

  constructor(private simulationService: SimulationService) {}

  ngOnInit(): void {
    this.simulationService.getColormaps().subscribe({
      next: (data) => {
        this.colormaps = data;
        this.loading = false;
      },
      error: (err) => {
        console.error('Failed to load colormaps', err);
        this.loading = false;
      }
    });
  }

  onSelect(id: string): void {
    this.selectedId = id;
    this.selectionChange.emit(id);
  }
}
