from pydantic import BaseModel, Field
from typing import List, Optional, Dict, Any

class EstimateRange(BaseModel):
    min_ms: int
    max_ms: int

class SimulationEstimateResult(BaseModel):
    simulation_type: str
    estimated_ms: int
    estimated_range: EstimateRange
    complexity_label: str
    complexity_color: str
    tips: List[str]
    parameters_received: Dict[str, Any]

class SimulationEstimateRequest(BaseModel):
    simulation_type: str
    parameters: Dict[str, Any]


class PerformanceMetadata(BaseModel):
    duration_ms: int
    compute_type: str
    memory_mb: float
    sionna_version: str

class SimulationRequest(BaseModel):
    """
    Parameters for the AWGN BER vs SNR simulation.
    Defaults correspond to a standard QPSK rate-1/2 scenario.
    """
    modulation_order: int = Field(
        default=4,
        description="Modulation order: 2=BPSK, 4=QPSK, 16=16QAM, 64=64QAM"
    )
    code_rate: float = Field(
        default=0.5,
        description="Code rate (fraction of bits that are data, e.g. 0.5 = rate-1/2)"
    )
    num_bits_per_symbol: int = Field(
        default=2,
        description="Bits per symbol: 1=BPSK, 2=QPSK, 4=16QAM, 6=64QAM"
    )
    snr_min: float = Field(
        default=-5.0,
        description="Minimum SNR in dB"
    )
    snr_max: float = Field(
        default=20.0,
        description="Maximum SNR in dB"
    )
    snr_steps: int = Field(
        default=25,
        description="Number of SNR points on the BER curve"
    )
    colormap: str = Field(default="default", description="Colormap for chart rendering")


class SimulationResult(BaseModel):
    """
    Results from the AWGN BER vs SNR simulation.
    Contains both theoretically computed and Monte-Carlo simulated BER curves.
    """
    snr_db: List[float] = Field(description="SNR values tested (dB)")
    ber_theoretical: List[float] = Field(description="Theoretical BER at each SNR point")
    ber_simulated: List[float] = Field(description="Monte-Carlo simulated BER at each SNR point")
    modulation: str = Field(description="Modulation scheme name (e.g. QPSK)")
    code_rate: float = Field(description="Code rate used in this run")
    simulation_time_ms: int = Field(description="Wall-clock time for the simulation in milliseconds")
    num_bits_simulated: int = Field(description="Total bits processed in the Monte-Carlo run")
    performance: Optional[PerformanceMetadata] = None
    colors: Optional[List[str]] = None
    colormap_used: Optional[str] = None

class BeamPatternRequest(BaseModel):
    num_antennas: int = 16
    steering_angle: float = 0.0
    frequency_ghz: float = 28.0
    array_spacing: float = 0.5
    colormap: str = "default"

class BeamPatternResult(BaseModel):
    angles: List[float]
    pattern_db: List[float]
    steering_angle: float
    num_antennas: int
    frequency_ghz: float
    main_lobe_width: float
    side_lobe_level: float
    array_gain_db: float
    performance: Optional[PerformanceMetadata] = None
    colors: Optional[List[str]] = None
    colormap_used: Optional[str] = None

class ModulationComparisonRequest(BaseModel):
    snr_min: float = -5.0
    snr_max: float = 25.0
    snr_steps: int = 50
    colormap: str = "default"

class CrossoverPoints(BaseModel):
    bpsk_qpsk_same: bool
    qpsk_advantage_over_16qam_at_snr: float
    qam16_advantage_over_64qam_at_snr: float

class ModulationComparisonResult(BaseModel):
    snr_db: List[float]
    bpsk: List[float]
    qpsk: List[float]
    qam16: List[float]
    qam64: List[float]
    snr_min: float
    snr_max: float
    snr_steps: int
    crossover_points: CrossoverPoints
    performance: Optional[PerformanceMetadata] = None
    colors: Optional[List[str]] = None
    colormap_used: Optional[str] = None

class ChannelCapacityRequest(BaseModel):
    snr_min: float = -10.0
    snr_max: float = 30.0
    snr_steps: int = 50
    bandwidths_mhz: list = [10.0, 100.0, 400.0, 1000.0]
    colormap: str = "default"

class ChannelCapacityCurve(BaseModel):
    bandwidth_mhz: float
    label: str
    capacity_gbps: List[float]
    peak_capacity_gbps: float
    color_hint: str

class ChannelCapacityInsights(BaseModel):
    snr_for_1gbps_100mhz: float
    snr_for_10gbps_1000mhz: float
    spectral_efficiency_at_snr20: float
    capacity_gain_10x_bandwidth: float

class ChannelCapacityResult(BaseModel):
    snr_db: List[float]
    spectral_efficiency: List[float]
    capacity_curves: List[ChannelCapacityCurve]
    snr_min: float
    snr_max: float
    insights: ChannelCapacityInsights
    performance: Optional[PerformanceMetadata] = None
    colors: Optional[List[str]] = None
    colormap_used: Optional[str] = None

class PathLossRequest(BaseModel):
    num_paths: int
    frequency_ghz: float
    environment: str
    colormap: str = "default"

class PathDto(BaseModel):
    path_id: int
    distance_m: float
    path_loss_db: float
    path_type: str
    delay_ns: float

class PathLossSummary(BaseModel):
    los_path_loss_db: float
    max_path_loss_db: float
    path_loss_spread_db: float
    mean_delay_ns: float

class PathLossResult(BaseModel):
    paths: list[PathDto]
    summary: PathLossSummary
    performance: Optional[PerformanceMetadata] = None
    colors: Optional[List[str]] = None
    colormap_used: Optional[str] = None

class RayDirectionRequest(BaseModel):
    num_paths: int = 8
    frequency_ghz: float = 28.0
    environment: str = "urban"
    tx_position: List[float] = [0.0, 0.0, 10.0]
    rx_position: List[float] = [100.0, 50.0, 1.5]
    colormap: str = "default"

class RayDirectionPath(BaseModel):
    path_id: int
    departure_azimuth_deg: float
    departure_elevation_deg: float
    arrival_azimuth_deg: float
    arrival_elevation_deg: float
    path_loss_db: float
    delay_ns: float
    path_type: str

class RayDirectionSummary(BaseModel):
    angular_spread_deg: float
    mean_departure_azimuth: float
    mean_arrival_azimuth: float
    num_los_paths: int
    num_nlos_paths: int

class RayDirectionResult(BaseModel):
    paths: List[RayDirectionPath]
    tx_position: List[float]
    rx_position: List[float]
    los_distance_m: float
    summary: RayDirectionSummary
    performance: Optional[PerformanceMetadata] = None
    colors: Optional[List[str]] = None
    colormap_used: Optional[str] = None

class UeTrajectoryRequest(BaseModel):
    num_waypoints: int = 6
    frequency_ghz: float = 28.0
    environment: str = "urban"
    tx_position: List[float] = [0.0, 0.0, 25.0]
    speed_kmh: float = 30.0
    trajectory_type: str = "random"
    colormap: str = "default"

class Waypoint(BaseModel):
    position: List[float]
    distance_m: float
    signal_dbm: float
    handover_required: bool
    time_s: float
    velocity_vector: List[float]

class UeTrajectorySummary(BaseModel):
    total_distance_m: float
    total_time_s: float
    min_signal_dbm: float
    max_signal_dbm: float
    handover_count: int
    coverage_percent: float

class UeTrajectoryResult(BaseModel):
    waypoints: List[Waypoint]
    tx_position: List[float]
    trajectory_type: str
    summary: UeTrajectorySummary
    performance: Optional[PerformanceMetadata] = None
    colors: Optional[List[str]] = None
    colormap_used: Optional[str] = None

class Transmitter(BaseModel):
    tx_id: int
    position: List[float]
    tx_power_dbm: float
    label: str

class MultiTxRequest(BaseModel):
    transmitters: List[Transmitter]
    grid_size: int = 20
    frequency_ghz: float = 28.0
    environment: str = "urban"
    show_interference: bool = True
    colormap: str = "default"

class CoverageGrid(BaseModel):
    best_signal: List[List[float]]
    serving_tx: List[List[int]]
    sinr: List[List[float]]
    classification: List[List[str]]

class PerTxStats(BaseModel):
    tx_id: int
    label: str
    cells_served: int
    coverage_percent: float
    mean_signal_dbm: float
    overlap_cells: int

class NetworkSummary(BaseModel):
    total_coverage_percent: float
    strong_coverage_percent: float
    interference_zones_percent: float
    mean_sinr_db: float
    coverage_holes_percent: float

class MultiTxResult(BaseModel):
    grid_size: int
    transmitters: List[Transmitter]
    coverage_grid: CoverageGrid
    per_tx_stats: List[PerTxStats]
    network_summary: NetworkSummary
    performance: Optional[PerformanceMetadata] = None
    colors: Optional[List[str]] = None
    colormap_used: Optional[str] = None


# ─────────────────────────────────────────────────────────────────────────────
# Feature 1: Measurement Overlay / Calibration
# ─────────────────────────────────────────────────────────────────────────────

class MeasurementPoint(BaseModel):
    snr_db: float
    ber_measured: float
    location: Optional[str] = ""

class MeasurementOverlayRequest(BaseModel):
    simulation_type: str = "AWGN"
    simulation_id: Optional[int] = None
    measurements: List[MeasurementPoint]
    frequency_ghz: float = 28.0
    environment: str = "urban"

class ComparisonPoint(BaseModel):
    snr_db: float
    ber_simulated: float
    ber_measured: float
    absolute_error: float
    relative_error_percent: float
    error_db: Optional[float] = None
    location: Optional[str] = ""

class CalibrationSummary(BaseModel):
    mean_absolute_error: float
    rmse: float
    calibration_quality: str
    systematic_offset_db: float
    max_error_point: float
    num_measurement_points: int

class MeasurementOverlayResult(BaseModel):
    comparison_points: List[ComparisonPoint]
    calibration_summary: CalibrationSummary
    simulation_type: str
    performance: Optional[PerformanceMetadata] = None


# ─────────────────────────────────────────────────────────────────────────────
# Feature 2: SINR Steering
# ─────────────────────────────────────────────────────────────────────────────

class SinrSteeringRequest(BaseModel):
    num_antennas: int = 16
    frequency_ghz: float = 28.0
    steering_angles: List[float] = [-60, -45, -30, -15, 0, 15, 30, 45, 60]
    interference_angle_deg: float = 45.0
    signal_power_dbm: float = 0.0
    interference_power_dbm: float = -10.0

class SteeringResult(BaseModel):
    steering_angle_deg: float
    array_gain_db: float
    interference_gain_db: float
    sinr_db: float
    efficiency_percent: float
    is_optimal: bool

class OptimalSteering(BaseModel):
    angle_deg: float
    sinr_db: float
    array_gain_db: float

class SinrSummary(BaseModel):
    max_sinr_db: float
    min_sinr_db: float
    sinr_range_db: float
    num_angles_above_10db: int
    interference_null_angle: float

class SinrSteeringResult(BaseModel):
    steering_results: List[SteeringResult]
    optimal_steering: OptimalSteering
    summary: SinrSummary
    performance: Optional[PerformanceMetadata] = None
