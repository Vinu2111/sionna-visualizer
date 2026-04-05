import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Workspace, WorkspaceMember, Comment, Annotation, Version, WorkspaceFeed, CreateWorkspaceData } from './workspace.interfaces';

@Injectable({
  providedIn: 'root'
})
export class WorkspaceService {

  private apiUrl = '/api';

  constructor(private http: HttpClient) {}

  getUserWorkspaces(): Observable<Workspace[]> {
    return this.http.get<Workspace[]>(`${this.apiUrl}/workspaces`);
  }

  createWorkspace(data: CreateWorkspaceData): Observable<Workspace> {
    return this.http.post<Workspace>(`${this.apiUrl}/workspaces`, data);
  }

  getWorkspaceMembers(workspaceId: number): Observable<WorkspaceMember[]> {
    return this.http.get<WorkspaceMember[]>(`${this.apiUrl}/workspaces/${workspaceId}/members`);
  }

  inviteMember(workspaceId: number, email: string, role: string): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/workspaces/${workspaceId}/invite`, { email, role });
  }

  getWorkspaceFeed(workspaceId: number): Observable<WorkspaceFeed[]> {
    return this.http.get<WorkspaceFeed[]>(`${this.apiUrl}/workspaces/${workspaceId}/feed`);
  }

  getSimulationComments(simulationId: number): Observable<Comment[]> {
    return this.http.get<Comment[]>(`${this.apiUrl}/simulations/${simulationId}/comments`);
  }

  addComment(simulationId: number, content: string, parentCommentId?: number): Observable<Comment> {
    return this.http.post<Comment>(`${this.apiUrl}/simulations/${simulationId}/comments`, { content, parentCommentId });
  }

  getAnnotations(simulationId: number): Observable<Annotation[]> {
    return this.http.get<Annotation[]>(`${this.apiUrl}/simulations/${simulationId}/annotations`);
  }

  addAnnotation(simulationId: number, snrPoint: number, berPoint: number, text: string): Observable<Annotation> {
    return this.http.post<Annotation>(`${this.apiUrl}/simulations/${simulationId}/annotations`, { snrPoint, berPoint, text });
  }

  getVersionHistory(simulationId: number): Observable<Version[]> {
    return this.http.get<Version[]>(`${this.apiUrl}/simulations/${simulationId}/versions`);
  }

  saveVersion(simulationId: number): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/simulations/${simulationId}/versions`, {});
  }
}
