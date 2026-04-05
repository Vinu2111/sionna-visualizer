export interface GalleryFilters {
  channelModel?: string;
  modulation?: string;
  frequency?: number;
  sortBy?: string;
  searchQuery?: string;
}

export interface PublishData {
  title: string;
  description: string;
  visibility: string;
  customTags: string[];
}

export interface GalleryItem {
  galleryId: number;
  simulationId: number;
  title: string;
  description: string;
  authorName: string;
  authorInitials: string;
  channelModel: string;
  modulation: string;
  frequencyGhz: number;
  berValues: number[];
  snrRange: number[];
  viewCount: number;
  forkCount: number;
  downloadCount: number;
  tags: string[];
  publishedAt: string;
  visibility: string;
}

export interface GalleryPage {
  content: GalleryItem[];
  pageNumber: number;
  pageSize: number;
  totalElements: number;
  totalPages: number;
}

export interface Comment {
  id: number;
  authorName: string;
  content: string;
  createdAt: string;
}

export interface GalleryDetail extends GalleryItem {
  simulationParameters: any;
  comments: Comment[];
  fullBerData: number[];
}

export interface ForkResult {
  newSimulationId: number;
  redirectUrl: string;
  message: string;
}
