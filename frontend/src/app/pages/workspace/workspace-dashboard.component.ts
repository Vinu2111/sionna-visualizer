import { Component, OnInit, OnDestroy } from '@angular/core';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { MatMenuModule } from '@angular/material/menu';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { WorkspaceService } from './workspace.service';
import { Workspace, WorkspaceMember, WorkspaceFeed } from './workspace.interfaces';
import { CreateWorkspaceDialogComponent } from './create-workspace-dialog.component';
import { InviteMemberDialogComponent } from './invite-member-dialog.component';

@Component({
  selector: 'app-workspace-dashboard',
  standalone: true,
  imports: [
    CommonModule, RouterModule, MatCardModule, MatButtonModule,
    MatIconModule, MatChipsModule, MatMenuModule, MatDialogModule
  ],
  templateUrl: './workspace-dashboard.component.html',
  styleUrls: ['./workspace-dashboard.component.scss']
})
export class WorkspaceDashboardComponent implements OnInit, OnDestroy {
  destroy$ = new Subject<void>();

  workspaces: Workspace[] = [];
  activeWorkspace: Workspace | null = null;
  members: WorkspaceMember[] = [];
  feed: WorkspaceFeed[] = [];

  constructor(
      private workspaceService: WorkspaceService,
      private dialog: MatDialog
  ) {}

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }

  ngOnInit() {
      this.loadWorkspaces();
  }

  loadWorkspaces() {
      this.workspaceService.getUserWorkspaces().pipe(takeUntil(this.destroy$)).subscribe(ws => {
          this.workspaces = ws;
          if (ws.length > 0) {
              this.selectWorkspace(ws[0]);
          }
      });
  }

  selectWorkspace(ws: Workspace) {
      this.activeWorkspace = ws;
      this.loadMembers();
      this.loadFeed();
  }

  loadMembers() {
      if(!this.activeWorkspace) return;
      this.workspaceService.getWorkspaceMembers(this.activeWorkspace.workspaceId).pipe(takeUntil(this.destroy$)).subscribe(
          data => this.members = data
      );
  }

  loadFeed() {
      if(!this.activeWorkspace) return;
      this.workspaceService.getWorkspaceFeed(this.activeWorkspace.workspaceId).pipe(takeUntil(this.destroy$)).subscribe(
          data => this.feed = data
      );
  }

  openCreateWorkspace() {
      const db = this.dialog.open(CreateWorkspaceDialogComponent, { width: '500px' });
      db.afterClosed().pipe(takeUntil(this.destroy$)).subscribe(res => { if(res) this.loadWorkspaces(); });
  }

  openInviteMember() {
      if(!this.activeWorkspace) return;
      const db = this.dialog.open(InviteMemberDialogComponent, { 
          width: '500px', 
          data: { workspaceId: this.activeWorkspace.workspaceId }
      });
      db.afterClosed().pipe(takeUntil(this.destroy$)).subscribe(res => { if(res) this.loadMembers(); });
  }

  canInvite(): boolean {
      return this.activeWorkspace?.myRole === 'OWNER' || this.activeWorkspace?.myRole === 'ADMIN';
  }

  getRoleColor(role: string): string {
      switch(role) {
          case 'OWNER': return '#d32f2f';
          case 'ADMIN': return '#f57c00';
          case 'VIEWER': return '#7b1fa2';
          default: return '#1976d2';
      }
  }
}
