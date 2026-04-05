import { Component, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MatDialogModule } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { WorkspaceService } from './workspace.service';

@Component({
  selector: 'app-create-workspace-dialog',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, MatDialogModule, MatButtonModule, MatInputModule],
  template: `
    <h2 mat-dialog-title>Create Team Workspace</h2>
    <mat-dialog-content>
       <form [formGroup]="form" style="display: flex; flex-direction: column; gap: 10px; margin-top: 10px;">
          <mat-form-field appearance="outline" style="width: 100%;">
             <mat-label>Workspace Name</mat-label>
             <input matInput formControlName="name" placeholder="e.g. Prof. Sharma — 6G MIMO Lab">
          </mat-form-field>
          <mat-form-field appearance="outline" style="width: 100%;">
             <mat-label>Description (Optional)</mat-label>
             <textarea matInput formControlName="description" rows="2"></textarea>
          </mat-form-field>
          <mat-form-field appearance="outline" style="width: 100%;">
             <mat-label>Institution (Optional)</mat-label>
             <input matInput formControlName="institution" placeholder="e.g. IIT Bombay">
          </mat-form-field>
       </form>
    </mat-dialog-content>
    <mat-dialog-actions align="end">
       <button mat-button mat-dialog-close>Cancel</button>
       <button mat-flat-button color="primary" [disabled]="form.invalid || isSaving" (click)="save()">Create Workspace</button>
    </mat-dialog-actions>
  `
})
export class CreateWorkspaceDialogComponent {
  form: FormGroup;
  isSaving = false;

  constructor(
      private fb: FormBuilder,
      private dialogRef: MatDialogRef<CreateWorkspaceDialogComponent>,
      private workspaceService: WorkspaceService
  ) {
      this.form = this.fb.group({
          name: ['', Validators.required],
          description: [''],
          institution: ['']
      });
  }

  save() {
      if (this.form.invalid) return;
      this.isSaving = true;
      this.workspaceService.createWorkspace(this.form.value).subscribe({
          next: Object => this.dialogRef.close(true),
          error: () => this.isSaving = false
      });
  }
}
