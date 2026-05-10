import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { GalleryItem, GalleryPage, GalleryDetail, ForkResult, Comment, GalleryFilters, PublishData } from './gallery.interfaces';

@Injectable({
  providedIn: 'root'
})
export class GalleryService {

  private apiUrl = '/api/gallery';

  constructor(private http: HttpClient) {}

  // Fetches public pages entirely unauthenticated securely via mapped GET layouts natively. 
  getGalleryItems(filters: GalleryFilters, page: number): Observable<GalleryPage> {
    let params = new HttpParams().set('page', page.toString());
    
    if (filters.channelModel && filters.channelModel !== 'All') params = params.set('channelModel', filters.channelModel);
    if (filters.modulation && filters.modulation !== 'All') params = params.set('modulation', filters.modulation);
    if (filters.frequency && filters.frequency !== 0) params = params.set('frequency', filters.frequency.toString());
    if (filters.sortBy) params = params.set('sortBy', filters.sortBy);
    if (filters.searchQuery) params = params.set('search', filters.searchQuery);

    return this.http.get<GalleryPage>(this.apiUrl, { params });
  }

  // Atomically executes unauthenticated structural metadata retrieval mapping heavy logic blocks. 
  getGalleryDetail(galleryId: number): Observable<GalleryDetail> {
    return this.http.get<GalleryDetail>(`${this.apiUrl}/${galleryId}`);
  }

  // Explicit authenticated executions locking bounds against internal mappings accurately. 
  publishSimulation(simulationId: number, publishData: PublishData): Observable<GalleryItem> {
    const params = new HttpParams().set('simulationId', simulationId.toString());
    return this.http.post<GalleryItem>(`${this.apiUrl}/publish`, publishData, { params });
  }

  // Generates massive independent exact-replicas mapped cleanly logically towards specific endpoints safely 
  forkSimulation(galleryId: number): Observable<ForkResult> {
    return this.http.post<ForkResult>(`${this.apiUrl}/${galleryId}/fork`, {});
  }

  // Generates threaded public comments cleanly safely
  addComment(galleryId: number, content: string): Observable<Comment> {
    const params = new HttpParams().set('content', content);
    return this.http.post<Comment>(`${this.apiUrl}/${galleryId}/comments`, null, { params });
  }
}
