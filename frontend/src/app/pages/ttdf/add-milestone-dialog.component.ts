import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, FormArray, Validators } from '@angular/forms';
import { MatDialogRef, MatDialogModule } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatIconModule } from '@angular/material/icon';
import { TtdfService } from './ttdf.service';

@Component({
  selector: 'app-add-milestone-dialog',
  standalone: true,
  imports: [
      CommonModule, FormsModule, ReactiveFormsModule, MatDialogModule, 
      MatButtonModule, MatInputModule, MatSelectModule, MatDatepickerModule, 
      MatNativeDateModule, MatIconModule
  ],
  template: `
    <h2 mat-dialog-title>Add Project Milestone</h2>
    <mat-dialog-content>
       <form [formGroup]="form" class="m-form">
          <mat-form-field appearance="outline" class="full-width">
             <mat-label>Milestone Title</mat-label>
             <input matInput formControlName="title" placeholder="e.g. Demonstration of Phase 1 Component">
          </mat-form-field>
          
          <div class="row">
              <mat-form-field appearance="outline">
                 <mat-label>Month Number</mat-label>
                 <input matInput type="number" formControlName="monthNumber" placeholder="e.g. 6">
              </mat-form-field>
              
              <mat-form-field appearance="outline">
                 <mat-label>Due Date</mat-label>
                 <input matInput [matDatepicker]="picker" formControlName="dueDate">
                 <mat-datepicker-toggle matIconSuffix [for]="picker"></mat-datepicker-toggle>
                 <mat-datepicker #picker></mat-datepicker>
              </mat-form-field>
          </div>

          <mat-form-field appearance="outline" class="full-width">
             <mat-label>Description</mat-label>
             <textarea matInput formControlName="description" rows="2"></textarea>
          </mat-form-field>

          <div class="kpis-section">
              <div class="kpis-header">
                  <h3>KPI Targets</h3>
                  <button mat-button type="button" color="primary" (click)="addKpi()"><mat-icon>add</mat-icon> Add KPI</button>
              </div>

              <div formArrayName="kpis">
                  <div class="kpi-row" *ngFor="let kpi of kpis.controls; let i=index" [formGroupName]="i">
                      <mat-form-field appearance="outline" class="flex-2">
                          <mat-label>KPI Name</mat-label>
                          <input matInput formControlName="kpiName" placeholder="e.g. BER at 20dB SNR">
                      </mat-form-field>
                      <mat-form-field appearance="outline" class="flex-1">
                          <mat-label>Target</mat-label>
                          <input matInput type="number" formControlName="targetValue">
                      </mat-form-field>
                      <mat-form-field appearance="outline" class="flex-1">
                          <mat-label>Unit</mat-label>
                          <input matInput formControlName="unit" placeholder="e.g. bps/Hz">
                      </mat-form-field>
                      <mat-form-field appearance="outline" class="flex-1">
                          <mat-label>Type</mat-label>
                          <mat-select formControlName="metricType">
                              <mat-option value="BER">BER</mat-option>
                              <mat-option value="THROUGHPUT">Throughput</mat-option>
                              <mat-option value="COVERAGE">Coverage</mat-option>
                              <mat-option value="CUSTOM">Custom</mat-option>
                          </mat-select>
                      </mat-form-field>
                      <button mat-icon-button type="button" color="warn" (click)="removeKpi(i)"><mat-icon>delete</mat-icon></button>
                  </div>
              </div>
          </div>
       </form>
    </mat-dialog-content>
    <mat-dialog-actions align="end">
       <button mat-button mat-dialog-close>Cancel</button>
       <button mat-flat-button color="primary" [disabled]="form.invalid || isSaving" (click)="save()">Save Milestone</button>
    </mat-dialog-actions>
  `,
  styles: [`
    .m-form { display: flex; flex-direction: column; gap: 5px; margin-top: 10px; }
    .full-width { width: 100%; }
    .row { display: flex; gap: 15px; }
    .kpis-section { margin-top: 10px; border-top: 1px solid #eee; padding-top: 15px; }
    .kpis-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px; h3 { margin: 0; font-size: 14px; font-weight: 500; color: #555; } }
    .kpi-row { display: flex; gap: 10px; align-items: baseline; }
    .flex-2 { flex: 2; } .flex-1 { flex: 1; }
  `]
})
export class AddMilestoneDialogComponent {
  
  form: FormGroup;
  isSaving = false;

  constructor(
      private fb: FormBuilder,
      private dialogRef: MatDialogRef<AddMilestoneDialogComponent>,
      private ttdfService: TtdfService
  ) {
      this.form = this.fb.group({
          title: ['', Validators.required],
          monthNumber: [null, Validators.required],
          dueDate: [null, Validators.required],
          description: [''],
          kpis: this.fb.array([])
      });
  }

  get kpis() {
      return this.form.get('kpis') as FormArray;
  }

  addKpi() {
      this.kpis.push(this.fb.group({
          kpiName: ['', Validators.required],
          targetValue: [null, Validators.required],
          unit: [''],
          metricType: ['CUSTOM']
      }));
  }

  removeKpi(index: number) {
      this.kpis.removeAt(index);
  }

  save() {
      if (this.form.invalid) return;
      this.isSaving = true;
      this.ttdfService.addMilestone(this.form.value).subscribe({
          next: () => this.dialogRef.close(true),
          error: () => this.isSaving = false
      });
  }
}
