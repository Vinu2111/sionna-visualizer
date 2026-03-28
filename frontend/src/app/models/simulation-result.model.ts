/**
 * Represents the simulation result returned from the Java backend,
 * which mirrors the Python bridge's AWGN BER-vs-SNR response structure.
 */
export interface PerformanceMetadata {
  duration_ms: number;
  compute_type: string;
  memory_mb: number;
  sionna_version: string;
}

export interface SimulationResult {
  snr_db: number[];
  ber_theoretical: number[];
  ber_simulated: number[];
  modulation: string;
  code_rate: number;
  simulation_time_ms: number;
  num_bits_simulated: number;
  performance?: PerformanceMetadata;
  colors?: string[];
  colormap_used?: string;
}

export interface ColormapOption {
  id: string;
  label: string;
  preview: string[];
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
  colormap?: string;
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
  
  // Channel Capacity fields
  capacityCurvesJson: string;
  spectralEfficiencyJson: string;
  insightsJson: string;
  
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
  colormapUsed?: string;
}

export interface BeamPatternRequest {
  num_antennas: number;
  steering_angle: number;
  frequency_ghz: number;
  array_spacing: number;
  colormap?: string;
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
  performance?: PerformanceMetadata;
  colors?: string[];
  colormap_used?: string;
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
  colormap?: string;
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
  performance?: PerformanceMetadata;
  colors?: string[];
  colormap_used?: string;
}

export interface ChannelCapacityCurve {
  bandwidth_mhz: number;
  label: string;
  capacity_gbps: number[];
  peak_capacity_gbps: number;
  color_hint: string;
}

export interface ChannelCapacityInsights {
  snr_for_1gbps_100mhz: number;
  snr_for_10gbps_1000mhz: number;
  spectral_efficiency_at_snr20: number;
  capacity_gain_10x_bandwidth: number;
}

export interface ChannelCapacityRequest {
  snr_min: number;
  snr_max: number;
  snr_steps: number;
  bandwidths_mhz: number[];
  colormap?: string;
}

export interface ChannelCapacityResult {
  snr_db: number[];
  spectral_efficiency: number[];
  capacity_curves: ChannelCapacityCurve[];
  snr_min: number;
  snr_max: number;
  insights: ChannelCapacityInsights;
  performance?: PerformanceMetadata;
  colors?: string[];
  colormap_used?: string;
}

export interface PathLossRequest {
  num_paths: number;
  frequency_ghz: number;
  environment: string;
  colormap?: string;
}

export interface PathDto {
  path_id: number;
  distance_m: number;
  path_loss_db: number;
  path_type: string;
  delay_ns: number;
}

export interface PathLossSummaryDto {
  los_path_loss_db: number;
  max_path_loss_db: number;
  path_loss_spread_db: number;
  mean_delay_ns: number;
}

export interface PathLossResult {
  paths: PathDto[];
  summary: PathLossSummaryDto;
  performance?: PerformanceMetadata;
  colors?: string[];
  colormap_used?: string;
}

export interface EstimateRange {
  min_ms: number;
  max_ms: number;
}

export interface SimulationEstimateResult {
  simulation_type: string;
  estimated_ms: number;
  estimated_range: EstimateRange;
  complexity_label: string;  // Fast | Medium | Slow | Heavy
  complexity_color: string;  // green | yellow | orange | red
  tips: string[];
  parameters_received: Record<string, any>;
}

export interface SimulationEstimateRequest {
  simulation_type: string;
  parameters: Record<string, any>;
}

export interface RayDirectionRequest {
  num_paths: number;
  frequency_ghz: number;
  environment: string;
  tx_position: number[];
  rx_position: number[];
  colormap?: string;
}

export interface RayDirectionPath {
  path_id: number;
  departure_azimuth_deg: number;
  departure_elevation_deg: number;
  arrival_azimuth_deg: number;
  arrival_elevation_deg: number;
  path_loss_db: number;
  delay_ns: number;
  path_type: string;
}

export interface RayDirectionSummary {
  angular_spread_deg: number;
  mean_departure_azimuth: number;
  mean_arrival_azimuth: number;
  num_los_paths: number;
  num_nlos_paths: number;
}

export interface RayDirectionResult {
  paths: RayDirectionPath[];
  tx_position: number[];
  rx_position: number[];
  los_distance_m: number;
  summary: RayDirectionSummary;
  performance?: PerformanceMetadata;
  colors?: string[];
  colormap_used?: string;
}

export interface UeTrajectoryRequest {
  num_waypoints: number;
  frequency_ghz: number;
  environment: string;
  tx_position: number[];
  speed_kmh: number;
  trajectory_type: string;
  colormap?: string;
}

export interface UeWaypoint {
  position: number[];
  distance_m: number;
  signal_dbm: number;
  handover_required: boolean;
  time_s: number;
  velocity_vector: number[];
}

export interface UeTrajectorySummary {
  total_distance_m: number;
  total_time_s: number;
  min_signal_dbm: number;
  max_signal_dbm: number;
  handover_count: number;
  coverage_percent: number;
}

export interface UeTrajectoryResult {
  waypoints: UeWaypoint[];
  tx_position: number[];
  trajectory_type: string;
  summary: UeTrajectorySummary;
  performance?: PerformanceMetadata;
  colors?: string[];
  colormap_used?: string;
}

// ─────────────────────────────────────────────────────────────────────────────
// Feature 1: Measurement Overlay / Calibration
// ─────────────────────────────────────────────────────────────────────────────

export interface MeasurementPointInput {
  snr_db: number;
  ber_measured: number;
  location?: string;
}

export interface MeasurementOverlayRequest {
  simulation_type: string;
  simulation_id?: number;
  measurements: MeasurementPointInput[];
  frequency_ghz: number;
  environment: string;
}

export interface ComparisonPoint {
  snr_db: number;
  ber_simulated: number;
  ber_measured: number;
  absolute_error: number;
  relative_error_percent: number;
  error_db: number | null;
  location?: string;
}

export interface CalibrationSummary {
  mean_absolute_error: number;
  rmse: number;
  calibration_quality: string;
  systematic_offset_db: number;
  max_error_point: number;
  num_measurement_points: number;
}

export interface MeasurementOverlayResult {
  comparison_points: ComparisonPoint[];
  calibration_summary: CalibrationSummary;
  simulation_type: string;
  performance?: PerformanceMetadata;
}

// ─────────────────────────────────────────────────────────────────────────────
// Feature 2: SINR Steering
// ─────────────────────────────────────────────────────────────────────────────

export interface SinrSteeringRequest {
  num_antennas: number;
  frequency_ghz: number;
  steering_angles: number[];
  interference_angle_deg: number;
  signal_power_dbm: number;
  interference_power_dbm: number;
}

export interface SteeringResult {
  steering_angle_deg: number;
  array_gain_db: number;
  interference_gain_db: number;
  sinr_db: number;
  efficiency_percent: number;
  is_optimal: boolean;
}

export interface OptimalSteering {
  angle_deg: number;
  sinr_db: number;
  array_gain_db: number;
}

export interface SinrSummary {
  max_sinr_db: number;
  min_sinr_db: number;
  sinr_range_db: number;
  num_angles_above_10db: number;
  interference_null_angle: number;
}

export interface SinrSteeringResult {
  steering_results: SteeringResult[];
  optimal_steering: OptimalSteering;
  summary: SinrSummary;
  performance?: PerformanceMetadata;
}
