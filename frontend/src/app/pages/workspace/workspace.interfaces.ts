export interface Workspace {
  workspaceId: number;
  name: string;
  description: string;
  institution: string;
  memberCount: number;
  myRole: string;
  createdAt: string;
}

export interface WorkspaceMember {
  userId: number;
  name: string;
  initials: string;
  role: string;
  simulationCount: number;
  lastActiveAt: string;
}

export interface Comment {
  commentId: number;
  authorName: string;
  authorInitials: string;
  content: string;
  parentCommentId: number;
  replies: Comment[];
  createdAt: string;
}

export interface Annotation {
  annotationId: number;
  authorName: string;
  snrPoint: number;
  berPoint: number;
  text: string;
  pinNumber: number;
  createdAt: string;
}

export interface Version {
  versionId: number;
  versionNumber: number;
  parameters: any;
  changedFields: string[];
  createdAt: string;
  createdByName: string;
}

export interface WorkspaceFeed {
  simulationId: number;
  authorName: string;
  authorInitials: string;
  channelModel: string;
  modulation: string;
  frequencyGhz: number;
  berAt20db: number;
  antennas: number;
  commentCount: number;
  annotationCount: number;
  createdAt: string;
}

export interface CreateWorkspaceData {
  name: string;
  description: string;
  institution: string;
}
