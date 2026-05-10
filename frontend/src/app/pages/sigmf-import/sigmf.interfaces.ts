export interface SigmfMetadata {
  sampleRate: number;
  centerFrequency: number;
  dataType: string;
  description: string;
  hardware: string;
  author: string;
  numSamples: number;
}

export interface IqSample {
  i: number;
  q: number;
}

export interface SigmfAnalysisResult {
  metadata: SigmfMetadata;
  estimatedSnrDb: number;
  berEstimate: number[];
  snrRange: number[];
  iqSamplesI: number[];
  iqSamplesQ: number[];
  berMatchPercentage: number;
  simulatedBer: number[];
}
