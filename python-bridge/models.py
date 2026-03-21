from pydantic import BaseModel, Field
from typing import List


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

class BeamPatternRequest(BaseModel):
    num_antennas: int = 16
    steering_angle: float = 0.0
    frequency_ghz: float = 28.0
    array_spacing: float = 0.5

class BeamPatternResult(BaseModel):
    angles: List[float]
    pattern_db: List[float]
    steering_angle: float
    num_antennas: int
    frequency_ghz: float
    main_lobe_width: float
    side_lobe_level: float
    array_gain_db: float
