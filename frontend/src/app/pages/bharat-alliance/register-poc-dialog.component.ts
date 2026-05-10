import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, FormArray, Validators } from '@angular/forms';
import { MatDialogRef, MatDialogModule } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatStepperModule } from '@angular/material/stepper';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatIconModule } from '@angular/material/icon';
import { BharatAllianceService } from './bharat-alliance.service';
import { KpiTemplate, ALLIANCE_TRACKS } from './bharat-alliance.interfaces';

@Component({
  selector: 'app-register-poc-dialog',
  standalone: true,
  imports: [
    CommonModule, FormsModule, ReactiveFormsModule, MatDialogModule, MatButtonModule,
    MatInputModule, MatSelectModule, MatStepperModule, MatDatepickerModule, MatNativeDateModule, MatIconModule
  ],
  templateUrl: './register-poc-dialog.component.html',
  styleUrls: ['./register-poc-dialog.component.scss']
})
export class RegisterPocDialogComponent implements OnInit {

  basicForm: FormGroup;
  classificationForm: FormGroup;
  kpiForm: FormGroup;

  allianceTracks = ALLIANCE_TRACKS;
  trlOptions = [1, 2, 3, 4, 5];
  kpiTemplates: KpiTemplate[] = [];
  isSaving = false;

  constructor(
    private fb: FormBuilder,
    private dialogRef: MatDialogRef<RegisterPocDialogComponent>,
    private service: BharatAllianceService
  ) {
    this.basicForm = this.fb.group({
      title: ['', [Validators.required, Validators.minLength(10)]],
      description: ['', [Validators.required, Validators.maxLength(1000)]],
      targetUseCase: ['']
    });

    this.classificationForm = this.fb.group({
      allianceTrack: ['', Validators.required],
      currentTrl: [1, Validators.required],
      expectedCompletionTrl: [null, Validators.required],
      targetCompletionDate: [null, Validators.required]
    });

    this.kpiForm = this.fb.group({
      kpis: this.fb.array([])
    });
  }

  ngOnInit() {}

  get kpis() { return this.kpiForm.get('kpis') as FormArray; }

  onTrackChange(track: string) {
    this.service.getKpiTemplates(track).subscribe(templates => {
      this.kpiTemplates = templates;
      this.kpis.clear();
      templates.forEach(t => {
        this.kpis.push(this.fb.group({
          kpiName: [t.kpiName, Validators.required],
          targetValue: [t.suggestedTarget, Validators.required],
          unit: [t.unit]
        }));
      });
    });
  }

  addCustomKpi() {
    this.kpis.push(this.fb.group({
      kpiName: ['', Validators.required],
      targetValue: [null, Validators.required],
      unit: ['']
    }));
  }

  removeKpi(i: number) { this.kpis.removeAt(i); }

  save() {
    if (this.basicForm.invalid || this.classificationForm.invalid) return;
    this.isSaving = true;

    const payload = {
      ...this.basicForm.value,
      ...this.classificationForm.value,
      kpis: this.kpis.value
    };

    this.service.registerPoc(payload).subscribe({
      next: () => this.dialogRef.close(true),
      error: () => this.isSaving = false
    });
  }
}
