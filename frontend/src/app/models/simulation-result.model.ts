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
  snrDb: string;           // JSON array string stored in DB
  berTheoretical: string;  // JSON array string stored in DB
  berSimulated: string;    // JSON array string stored in DB
  modulationType: string;
  codeRate: number;
  snrMin: number;
  snrMax: number;
  simulationTimeMs: number;
  hardwareUsed: string;
  timestamp: string;
  createdAt: string;
}
