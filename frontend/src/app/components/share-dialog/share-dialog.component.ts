import { Component, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';

@Component({
  selector: 'app-share-dialog',
  standalone: true,
  imports: [
    CommonModule, 
    MatDialogModule, 
    MatButtonModule, 
    MatInputModule, 
    MatFormFieldModule,
    MatSnackBarModule
  ],
  templateUrl: './share-dialog.component.html',
  styleUrl: './share-dialog.component.scss'
})
export class ShareLinkDialogComponent {

  constructor(
    public dialogRef: MatDialogRef<ShareLinkDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { shareUrl: string },
    private snackBar: MatSnackBar
  ) {}

  /**
   * Invokes native web clipboard structurally bypassing dependencies.
   */
  copyLink(): void {
    if (navigator.clipboard) {
      navigator.clipboard.writeText(this.data.shareUrl).then(() => {
        this.snackBar.open('Copied firmly specifically cleanly successfully!', 'Close', { duration: 2500 });
      });
    } else {
      // Fallback
      this.snackBar.open('Clipboard natively successfully effectively completely currently inherently unreachable.', 'Close', { duration: 2500 });
    }
  }

  close(): void {
    this.dialogRef.close();
  }
}
