from pydantic import BaseModel, Field
from typing import List, Dict, Any, Optional

class SimulationRequest(BaseModel):
    """
    Parameters for the OFDM Simulation.
    If not provided, these will default to standard values.
    """
    num_ofdm_symbols: Optional[int] = Field(default=14, description="Number of OFDM symbols per slot")
    fft_size: Optional[int] = Field(default=76, description="FFT size (number of subcarriers)")
    snr_min: Optional[int] = Field(default=0, description="Minimum Signal-to-Noise Ratio (SNR) in dB")
    snr_max: Optional[int] = Field(default=20, description="Maximum Signal-to-Noise Ratio (SNR) in dB")

class SimulationResult(BaseModel):
    """
    Results from the OFDM Simulation execution.
    Contains data points that the frontend will map to charts.
    """
    snr_db: List[float] = Field(description="List of SNR values tested in this run")
    ber: List[float] = Field(description="List of Bit Error Rate (BER) values matching the SNR points")
    metadata: Dict[str, Any] = Field(description="Simulation configuration parameters used")
    timestamp: str = Field(description="ISO format timestamp indicating when the simulation completed")
