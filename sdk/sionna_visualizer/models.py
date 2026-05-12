from dataclasses import dataclass, field, asdict
from typing import List, Optional
from datetime import datetime


@dataclass
class BerData:
    # BER simulation result data
    snr_range_db: List[float]
    simulated_ber: List[float]
    theoretical_ber: Optional[List[float]] = None
    modulation: str = "QPSK"
    channel_model: str = "AWGN"
    frequency_ghz: float = 28.0
    num_antennas_tx: int = 1
    num_antennas_rx: int = 1
    monte_carlo_trials: int = 10000


@dataclass
class BeamData:
    # Beam pattern result data
    angles_deg: List[float]
    pattern_db: List[float]
    num_antennas: int
    frequency_ghz: float
    array_type: str = "ULA"


@dataclass
class SimulationResult:
    # Universal simulation result container
    simulation_type: str
    title: Optional[str] = None
    tags: List[str] = field(default_factory=list)
    ber_data: Optional[BerData] = None
    beam_data: Optional[BeamData] = None
    raw_data: Optional[dict] = None
    created_at: str = field(default_factory=lambda: datetime.now().isoformat())

    def to_dict(self) -> dict:
        # Convert to JSON-serializable dict for sending to API.
        payload = {
            "simulationType": self.simulation_type,
            "title": self.title,
            "tags": self.tags,
            "createdAt": self.created_at,
        }
        if self.ber_data is not None:
            payload["berData"] = {
                "snrRangeDb": self.ber_data.snr_range_db,
                "simulatedBer": self.ber_data.simulated_ber,
                "theoreticalBer": self.ber_data.theoretical_ber,
                "modulation": self.ber_data.modulation,
                "channelModel": self.ber_data.channel_model,
                "frequencyGhz": self.ber_data.frequency_ghz,
                "numAntennasTx": self.ber_data.num_antennas_tx,
                "numAntennasRx": self.ber_data.num_antennas_rx,
            }
        if self.beam_data is not None:
            payload["beamData"] = asdict(self.beam_data)
        if self.raw_data is not None:
            payload["rawData"] = self.raw_data
        return payload


@dataclass
class TrackingResponse:
    # Response from Sionna Visualizer API
    success: bool
    shareable_url: str
    simulation_id: int
    message: str
