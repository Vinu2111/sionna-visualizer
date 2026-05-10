import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef, MatDialogModule } from '@angular/material/dialog';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { ReproducibilityService } from '../../services/reproducibility.service';

@Component({
  selector: 'app-reproducibility-package',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatButtonModule,
    MatCheckboxModule,
    MatIconModule,
    MatProgressSpinnerModule
  ],
  templateUrl: './reproducibility-package.component.html',
  styleUrls: ['./reproducibility-package.component.scss']
})
export class ReproducibilityPackageComponent implements OnInit {
  
  optionsForm: FormGroup;
  isGenerating = false;

  constructor(
    public dialogRef: MatDialogRef<ReproducibilityPackageComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private fb: FormBuilder,
    private reproService: ReproducibilityService
  ) {
    // Setup defaults exactly as requested for quick baseline adoption
    this.optionsForm = this.fb.group({
      includeRawBerData: [true],
      includeBeamPatternData: [true],
      anonymizeForBlindReview: [false]
    });
  }

  ngOnInit(): void {}

  // Triggers the zip generation process
  generateAndDownload(): void {
    const currentSimId = this.data?.simulationId || 1; 
    this.isGenerating = true;

    this.reproService.generatePackage(currentSimId, this.optionsForm.value).subscribe({
      next: (blob: Blob) => {
        // Feed blob to download service wrapper and close modal smoothly
        this.reproService.downloadZip(blob, 'reproducibility-package.zip');
        this.isGenerating = false;
        this.dialogRef.close();
      },
      error: (err) => {
        console.error('Failed to generate reproducibility package', err);
        this.isGenerating = false;
      }
    });
  }
}
