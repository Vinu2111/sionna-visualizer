/**
 * Represents the simulation result returned from the Java backend,
 * which mirrors the Python bridge's AWGN BER-vs-SNR response structure.
 */
export interface SimulationResult {
  snr_db: number[];
  ber_theoretical: number[];
  ber_simulated: number[];
  modulation: string;
  code_rate: number;
  simulation_time_ms: number;
  num_bits_simulated: number;
}

/**
 * Parameters sent to POST /api/simulate
 */
export interface SimulationRequest {
  modulation_order: number;      // 2=BPSK, 4=QPSK, 16=16QAM, 64=64QAM
  code_rate: number;             // 0.1 – 1.0
  num_bits_per_symbol: number;   // 1/2/4/6
  snr_min: number;
  snr_max: number;
  snr_steps: number;
}

/**
 * Represents a saved simulation as returned from GET /api/simulations
 * (PostgreSQL history).
 */
export interface SimulationHistoryItem {
  id: number;
  simulationType: string;
  snrDb: string;           // JSON array string stored in DB
  berTheoretical: string;  // JSON array string stored in DB
  berSimulated: string;    // JSON array string stored in DB
  beamAngles: string;      // JSON array string stored in DB
  beamPatternDb: string;   // JSON array string stored in DB
  
  // Modulation Comparison fields
  bpskBer: string;
  qpskBer: string;
  qam16Ber: string;
  qam64Ber: string;
  comparisonSnrMin: number;
  comparisonSnrMax: number;
  crossoverPoints: string;
  
  modulationType: string;
  codeRate: number;
  snrMin: number;
  snrMax: number;
  steeringAngle: number;
  numAntennas: number;
  frequencyGhz: number;
  mainLobeWidth: number;
  sideLobeLevel: number;
  simulationTimeMs: number;
  hardwareUsed: string;
  timestamp: string;
  createdAt: string;
}

export interface BeamPatternRequest {
  num_antennas: number;
  steering_angle: number;
  frequency_ghz: number;
  array_spacing: number;
}

export interface BeamPatternResult {
  angles: number[];
  pattern_db: number[];
  steering_angle: number;
  num_antennas: number;
  frequency_ghz: number;
  main_lobe_width: number;
  side_lobe_level: number;
  array_gain_db: number;
}

export interface CrossoverPoints {
  bpsk_qpsk_same: boolean;
  qpsk_advantage_over_16qam_at_snr: number;
  qam16_advantage_over_64qam_at_snr: number;
}

export interface ModulationComparisonRequest {
  snr_min: number;
  snr_max: number;
  snr_steps: number;
}

export interface ModulationComparisonResult {
  snr_db: number[];
  bpsk: number[];
  qpsk: number[];
  qam16: number[];
  qam64: number[];
  snr_min: number;
  snr_max: number;
  snr_steps: number;
  crossover_points: CrossoverPoints;
}
