/**
 * Represents the structured data returned from the Python Sionna bridge,
 * encapsulated within standard DTOs.
 */
export interface SimulationResult {
  snr_db: number[];
  ber: number[];
  metadata: {
    num_ofdm_symbols?: number;
    fft_size?: number;
    subcarrier_spacing?: number;
    num_tx?: number;
    num_rx?: number;
    sionna_used?: boolean;
    mock_generated?: boolean;
    [key: string]: any; // Allow for other flexible metadata fields
  };
  timestamp: string;
}

/**
 * Represents a saved simulation exactly as it is returned directly
 * from the Java Spring Boot PostgreSQL database endpoints.
 */
export interface SimulationHistoryItem {
  id: number;
  snrDb: string; // Stored as a JSON string in DB
  ber: string;   // Stored as a JSON string in DB
  numOfdmSymbols: number;
  fftSize: number;
  hardwareUsed: string;
  timestamp: string;
  createdAt: string;
}
