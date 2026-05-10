export interface ParsedSimulationParams {
  frequency: number | null;
  channelModel: string | null;
  modulation: string | null;
  modulationList: string[];
  numAntennasTx: number;
  numAntennasRx: number;
  snrMin: number;
  snrMax: number;
  simulationType: string;
  environment: string | null;
  runComparison: boolean;
  confidence: 'HIGH' | 'MEDIUM' | 'LOW';
  missingParams: string[];
  naturalLanguageSummary: string;
  aiFilled: string[];    // Field names the AI populated — used to show "AI filled" badges
}

export interface ParseHistory {
  id: number;
  queryText: string;
  confidence: string;
  simulationType: string;
  parsedAt: string;
}

// Quick-fill example chips shown below the textarea
export const EXAMPLE_QUERIES: { chip: string; full: string }[] = [
  {
    chip: '28 GHz MIMO 4x4 CDL-A QPSK',
    full: 'Simulate 28 GHz MIMO 4x4 urban CDL-A channel with QPSK modulation'
  },
  {
    chip: '64QAM AWGN SNR -5 to 25 dB',
    full: 'Run AWGN BER test for 64QAM at 5 GHz with SNR range from -5 to 25 dB'
  },
  {
    chip: 'BPSK vs QPSK rural TDL-B',
    full: 'Compare BPSK and QPSK performance in a rural TDL-B channel scenario'
  },
  {
    chip: '60 GHz beamforming 32 antennas',
    full: '60 GHz mmWave beamforming simulation with 32 transmit antennas at 39 GHz'
  }
];
