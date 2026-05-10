export interface SimulationSummary {
  id: number;
  modulation: string;
  channelModel: string;
  frequency: number;
  createdAt: string;
}

export interface ExternalSimData {
  simulatorType: string;
  snrValues: number[];
  berValues: number[];
  throughputValues: number[];
  detectedColumns: string[];
}

export interface ComparisonResult {
  comparisonId: number;
  sionnaSimulationId: number;
  simulatorType: string;
  snrPoints: number[];
  sionnaBer: number[];
  externalBer: number[];
  sionnaThroughput: number[];
  externalThroughput: number[];
  berCrossoverSnr: number;
  averageBerDifference: number;
  betterPerformerAt20db: string;
  matchedDataPoints: number;
  createdAt: string;
}
