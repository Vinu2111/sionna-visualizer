import { Component, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MatDialogModule } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { ExperimentService } from './experiment.service';

@Component({
  selector: 'app-create-experiment-dialog',
  standalone: true,
  imports: [
    CommonModule, FormsModule, ReactiveFormsModule, MatDialogModule,
    MatButtonModule, MatInputModule, MatIconModule
  ],
  template: `
    <h2 mat-dialog-title>Create New Experiment</h2>
    <mat-dialog-content>
       <form [formGroup]="form" class="exp-form">
          <mat-form-field appearance="outline" class="full-width">
             <mat-label>Experiment Name</mat-label>
             <input matInput formControlName="name" placeholder="e.g. MIMO 28GHz Urban Study">
          </mat-form-field>
          
          <mat-form-field appearance="outline" class="full-width">
             <mat-label>Description (Optional)</mat-label>
             <textarea matInput formControlName="description" rows="3"></textarea>
          </mat-form-field>

          <div class="color-picker">
             <label>Experiment Color Badge</label>
             <div class="colors">
                <div class="color-circle" *ngFor="let c of presetColors" 
                     [style.backgroundColor]="c"
                     [class.selected]="form.get('color')?.value === c"
                     (click)="form.patchValue({color: c})">
                     <mat-icon *ngIf="form.get('color')?.value === c">check</mat-icon>
                </div>
             </div>
          </div>
       </form>
    </mat-dialog-content>
    <mat-dialog-actions align="end">
       <button mat-button mat-dialog-close>Cancel</button>
       <button mat-flat-button color="primary" [disabled]="form.invalid || isSaving" (click)="save()">Create Experiment</button>
    </mat-dialog-actions>
  `,
  styles: [`
    .exp-form { display: flex; flex-direction: column; gap: 10px; margin-top: 10px; }
    .full-width { width: 100%; }
    .color-picker { 
        margin-top: 10px; 
        label { font-size: 13px; color: #666; margin-bottom: 10px; display: block; }
        .colors { display: flex; gap: 10px; flex-wrap: wrap; }
        .color-circle { 
            width: 36px; height: 36px; border-radius: 50%; cursor: pointer;
            display: flex; align-items: center; justify-content: center;
            color: white; transition: 0.2s transform;
            &:hover { transform: scale(1.1); }
            &.selected { box-shadow: 0 0 0 2px white, 0 0 0 4px #333; }
            mat-icon { font-size: 20px; width: 20px; height: 20px; }
        }
    }
  `]
})
export class CreateExperimentDialogComponent {
  
  form: FormGroup;
  isSaving = false;
  presetColors = ['#1976d2', '#d32f2f', '#388e3c', '#fbc02d', '#7b1fa2', '#e64a19', '#0097a7', '#455a64'];

  constructor(
      private fb: FormBuilder,
      private dialogRef: MatDialogRef<CreateExperimentDialogComponent>,
      private expService: ExperimentService
  ) {
      this.form = this.fb.group({
          name: ['', Validators.required],
          description: [''],
          color: ['#1976d2']
      });
  }

  save() {
      if (this.form.invalid) return;
      this.isSaving = true;
      this.expService.createExperiment(this.form.value).subscribe({
          next: (res) => this.dialogRef.close(res),
          error: () => { alert('Failed structurally.'); this.isSaving = false; }
      });
  }
}
