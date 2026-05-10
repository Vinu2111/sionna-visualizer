import { Component, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef, MatDialogModule } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { WorkspaceService } from './workspace.service';

@Component({
  selector: 'app-invite-member-dialog',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, MatDialogModule, MatButtonModule, MatInputModule, MatSelectModule],
  template: `
    <h2 mat-dialog-title>Invite Team Member</h2>
    <mat-dialog-content>
       <form [formGroup]="form" style="display: flex; flex-direction: column; gap: 10px; margin-top: 10px;">
          <mat-form-field appearance="outline" style="width: 100%;">
             <mat-label>Email Address</mat-label>
             <input matInput formControlName="email" type="email" placeholder="colleague@university.edu">
          </mat-form-field>
          <mat-form-field appearance="outline" style="width: 100%;">
             <mat-label>Role</mat-label>
             <mat-select formControlName="role">
                 <mat-option value="ADMIN"><strong>ADMIN</strong> — Can invite members and manage workspace</mat-option>
                 <mat-option value="MEMBER"><strong>MEMBER</strong> — Can run simulations and view all results</mat-option>
                 <mat-option value="VIEWER"><strong>VIEWER</strong> — Can only view and comment (for supervisors)</mat-option>
             </mat-select>
          </mat-form-field>
       </form>
    </mat-dialog-content>
    <mat-dialog-actions align="end">
       <button mat-button mat-dialog-close>Cancel</button>
       <button mat-flat-button color="primary" [disabled]="form.invalid || isSaving" (click)="save()">Send Invitation</button>
    </mat-dialog-actions>
  `
})
export class InviteMemberDialogComponent {
  form: FormGroup;
  isSaving = false;

  constructor(
      private fb: FormBuilder,
      private dialogRef: MatDialogRef<InviteMemberDialogComponent>,
      @Inject(MAT_DIALOG_DATA) public data: { workspaceId: number },
      private workspaceService: WorkspaceService
  ) {
      this.form = this.fb.group({
          email: ['', [Validators.required, Validators.email]],
          role: ['MEMBER', Validators.required]
      });
  }

  save() {
      if (this.form.invalid) return;
      this.isSaving = true;
      this.workspaceService.inviteMember(this.data.workspaceId, this.form.value.email, this.form.value.role).subscribe({
          next: () => this.dialogRef.close(true),
          error: () => this.isSaving = false
      });
  }
}
