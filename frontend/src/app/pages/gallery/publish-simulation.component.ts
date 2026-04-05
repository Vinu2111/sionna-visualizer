import { Component, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef, MatDialogModule } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatChipsModule, MatChipInputEvent } from '@angular/material/chips';
import { MatIconModule } from '@angular/material/icon';
import { COMMA, ENTER } from '@angular/cdk/keycodes';
import { GalleryService } from './gallery.service';

@Component({
  selector: 'app-publish-simulation',
  standalone: true,
  imports: [
    CommonModule, FormsModule, ReactiveFormsModule, MatDialogModule,
    MatButtonModule, MatInputModule, MatSelectModule, MatChipsModule, MatIconModule
  ],
  templateUrl: './publish-simulation.component.html',
  styleUrls: ['./publish-simulation.component.scss']
})
export class PublishSimulationComponent {

  form: FormGroup;
  isPublishing = false;
  
  readonly separatorKeysCodes = [ENTER, COMMA] as const;
  customTags: string[] = [];

  constructor(
      private fb: FormBuilder,
      private dialogRef: MatDialogRef<PublishSimulationComponent>,
      @Inject(MAT_DIALOG_DATA) public data: { simulationId: number, defaultTitle: string },
      private galleryService: GalleryService
  ) {
      this.form = this.fb.group({
          title: [data.defaultTitle || 'My Simulation', [Validators.required, Validators.maxLength(200)]],
          description: ['', Validators.maxLength(500)],
          visibility: ['PUBLIC', Validators.required]
      });
  }

  addTag(event: MatChipInputEvent): void {
      const value = (event.value || '').trim();
      if (value) {
          this.customTags.push(value);
      }
      event.chipInput!.clear();
  }

  removeTag(tag: string): void {
      const index = this.customTags.indexOf(tag);
      if (index >= 0) {
          this.customTags.splice(index, 1);
      }
  }

  publish() {
      if (this.form.invalid) return;
      this.isPublishing = true;
      
      const payload = {
          ...this.form.value,
          customTags: this.customTags
      };

      this.galleryService.publishSimulation(this.data.simulationId, payload).subscribe({
          next: (res) => {
              this.isPublishing = false;
              this.dialogRef.close(res);
              alert("Published securely to the Global 6G Gallery!");
          },
          error: (err) => {
              console.error(err);
              this.isPublishing = false;
              alert("Failed to publish gracefully.");
          }
      });
  }
}
