"""
main.py — FastAPI application for the Sionna Visualizer Python bridge.

Exposes:
  GET  /health              — liveness probe
  POST /simulate            — full simulation with user-supplied parameters
  POST /simulate/demo       — same, but with sensible defaults (for Angular dev)
"""

import datetime
import asyncio
import anyio
from fastapi import FastAPI, HTTPException
from models import (
    SimulationRequest, SimulationResult, 
    BeamPatternRequest, BeamPatternResult,
    ModulationComparisonRequest, ModulationComparisonResult,
    ChannelCapacityRequest, ChannelCapacityResult
)
from sionna_runner import run_awgn_simulation
from beam_pattern import compute_ula_beam_pattern
from modulation_comparison import compute_modulation_comparison
from channel_capacity import compute_channel_capacity

app = FastAPI(
    title="Sionna Visualizer Bridge",
    description="CPU-only AWGN BER-vs-SNR simulation microservice",
    version="2.0.0",
)


# ---------------------------------------------------------------------------
# Health probe — Railway / k8s liveness check
# ---------------------------------------------------------------------------

@app.get("/health")
def health_check():
    """
    Returns 200 OK to signal the service is up.
    Used by Railway health checks and the Java backend's connectivity test.
    """
    return {"status": "healthy", "service": "sionna-python-bridge", "version": "1.0.0"}


# ---------------------------------------------------------------------------
# Core simulation endpoint
# ---------------------------------------------------------------------------

@app.post("/simulate", response_model=SimulationResult)
async def simulate(request: SimulationRequest):
    """
    Run an AWGN BER-vs-SNR simulation with the supplied parameters.
    """
    try:
        result = await asyncio.wait_for(
            anyio.to_thread.run_sync(
                run_awgn_simulation,
                request.modulation_order,
                request.code_rate,
                request.num_bits_per_symbol,
                request.snr_min,
                request.snr_max,
                request.snr_steps,
            ),
            timeout=30.0
        )
        return SimulationResult(**result)
    except asyncio.TimeoutError:
        raise HTTPException(
            status_code=408,
            detail="Simulation timed out. Try reducing the number of SNR steps."
        )
    except Exception as exc:
        raise HTTPException(
            status_code=500,
            detail=f"Simulation failed: {str(exc)}",
        )


# ---------------------------------------------------------------------------
# Demo endpoint — POST with default QPSK parameters
# ---------------------------------------------------------------------------

@app.post("/simulate/demo", response_model=SimulationResult)
async def simulate_demo(request: SimulationRequest = None):
    """
    Runs a simulation with default QPSK rate-1/2 params.
    If a body is provided, it uses those params.
    """
    if request is None:
        request = SimulationRequest()
    return await simulate(request)


# ---------------------------------------------------------------------------
# Beam Pattern endpoint
# ---------------------------------------------------------------------------

@app.post("/simulate/beam-pattern", response_model=BeamPatternResult)
async def simulate_beam_pattern(request: BeamPatternRequest):
    """
    Run a ULA beam pattern generation with the supplied parameters.
    """
    try:
        result = await asyncio.wait_for(
            anyio.to_thread.run_sync(
                compute_ula_beam_pattern,
                request.num_antennas,
                request.steering_angle,
                request.frequency_ghz,
                request.array_spacing,
            ),
            timeout=30.0
        )
        return BeamPatternResult(**result)
    except asyncio.TimeoutError:
        raise HTTPException(
            status_code=408,
            detail="Simulation timed out. Try reducing the number of SNR steps."
        )
    except Exception as exc:
        raise HTTPException(
            status_code=500,
            detail=f"Beam pattern computation failed: {str(exc)}",
        )

# ---------------------------------------------------------------------------
# Modulation Comparison endpoint
# ---------------------------------------------------------------------------

@app.post("/simulate/modulation-comparison", response_model=ModulationComparisonResult)
async def simulate_modulation_comparison(request: ModulationComparisonRequest):
    """
    Run theoretical generation across BPSK, QPSK, 16QAM, 64QAM.
    """
    try:
        result = await asyncio.wait_for(
            anyio.to_thread.run_sync(
                compute_modulation_comparison,
                request.snr_min,
                request.snr_max,
                request.snr_steps,
            ),
            timeout=30.0
        )
        return ModulationComparisonResult(**result)
    except asyncio.TimeoutError:
        raise HTTPException(
            status_code=408,
            detail="Simulation timed out. Try reducing the number of SNR steps."
        )
    except Exception as exc:
        raise HTTPException(
            status_code=500,
            detail=f"Modulation comparison failed: {str(exc)}",
        )


# ---------------------------------------------------------------------------
# Channel Capacity endpoint
# ---------------------------------------------------------------------------

@app.post("/simulate/channel-capacity", response_model=ChannelCapacityResult)
async def simulate_channel_capacity(request: ChannelCapacityRequest):
    """
    Compute Shannon channel capacity across standard 6G bandwidths.
    """
    try:
        result = await asyncio.wait_for(
            anyio.to_thread.run_sync(
                compute_channel_capacity,
                request.snr_min,
                request.snr_max,
                request.snr_steps,
                request.bandwidths_mhz
            ),
            timeout=30.0
        )
        return ChannelCapacityResult(**result)
    except asyncio.TimeoutError:
        raise HTTPException(
            status_code=408,
            detail="Simulation timed out. Try reducing the number of SNR steps."
        )
    except Exception as exc:
        raise HTTPException(
            status_code=500,
            detail=f"Channel capacity computation failed: {str(exc)}",
        )


# ---------------------------------------------------------------------------
# Keep a GET /simulate/demo alias so the old Java GET call still works
# during the transition period before the Java service is updated.
# ---------------------------------------------------------------------------

@app.get("/simulate/demo", response_model=SimulationResult)
async def simulate_demo_get():
    """Legacy GET alias — kept for backward compatibility."""
    return await simulate(SimulationRequest())
