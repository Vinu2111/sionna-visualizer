import { Component, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MAT_DIALOG_DATA, MatDialogRef, MatDialogModule } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatChipsModule, MatChipInputEvent } from '@angular/material/chips';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { COMMA, ENTER } from '@angular/cdk/keycodes';
import { ExperimentService } from './experiment.service';

@Component({
  selector: 'app-bulk-tag-dialog',
  standalone: true,
  imports: [
    CommonModule, MatDialogModule, MatButtonModule, MatChipsModule,
    MatIconModule, MatFormFieldModule
  ],
  template: `
    <h2 mat-dialog-title>Bulk Tagging</h2>
    <mat-dialog-content>
       <p style="color: #666; margin-bottom: 20px;">Tagging {{ data.simulationIds.length }} simulations simultaneously.</p>
       
       <mat-form-field appearance="outline" style="width: 100%;">
           <mat-label>Add Tags</mat-label>
           <mat-chip-grid #chipGrid aria-label="Tag selection">
               <mat-chip-row *ngFor="let tag of tags" (removed)="removeTag(tag)">
                   {{ tag }}
                   <button matChipRemove><mat-icon>cancel</mat-icon></button>
               </mat-chip-row>
               <input placeholder="New tag..." 
                      [matChipInputFor]="chipGrid"
                      [matChipInputSeparatorKeyCodes]="separatorKeysCodes"
                      (matChipInputTokenEnd)="addTag($event)">
           </mat-chip-grid>
       </mat-form-field>
    </mat-dialog-content>
    <mat-dialog-actions align="end">
       <button mat-button mat-dialog-close>Cancel</button>
       <button mat-flat-button color="primary" [disabled]="tags.length === 0 || isSaving" (click)="save()">Apply Tags</button>
    </mat-dialog-actions>
  `
})
export class BulkTagDialogComponent {
  
  tags: string[] = [];
  readonly separatorKeysCodes = [ENTER, COMMA] as const;
  isSaving = false;

  constructor(
      private dialogRef: MatDialogRef<BulkTagDialogComponent>,
      @Inject(MAT_DIALOG_DATA) public data: { simulationIds: number[] },
      private expService: ExperimentService
  ) {}

  addTag(event: MatChipInputEvent): void {
      const value = (event.value || '').trim().toLowerCase();
      if (value && !this.tags.includes(value)) this.tags.push(value);
      event.chipInput!.clear();
  }

  removeTag(tag: string): void {
      const index = this.tags.indexOf(tag);
      if (index >= 0) this.tags.splice(index, 1);
  }

  save() {
      this.isSaving = true;
      this.expService.bulkAddTags(this.data.simulationIds, this.tags).subscribe({
          next: () => this.dialogRef.close(true),
          error: () => { alert('Failed bulk tagging structurally.'); this.isSaving = false; }
      });
  }
}
