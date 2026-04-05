export interface Experiment {
  experimentId: number;
  name: string;
  description: string;
  color: string;
  simulationCount: number;
  createdAt: string;
}

export interface SimulationHistoryItem {
  simulationId: number;
  channelModel: string;
  modulation: string;
  frequencyGhz: number;
  berAt20db: number;
  simulationTimeSeconds: number;
  tags: string[];
  note: string;
  starred: boolean;
  experimentId: number;
  experimentName: string;
  experimentColor: string;
  createdAt: string;
}

export interface TagCount {
  tag: string;
  count: number;
}

export interface SimulationFilters {
  experimentId?: number;
  tags?: string[];
  searchQuery?: string;
  starred?: boolean;
  page?: number;
  size?: number;
}

export interface CreateExperimentData {
  name: string;
  description: string;
  color: string;
}
