"""
main.py — FastAPI application for the Sionna Visualizer Python bridge.

Exposes:
  GET  /health              — liveness probe
  POST /simulate            — full simulation with user-supplied parameters
  POST /simulate/demo       — same, but with sensible defaults (for Angular dev)
"""

import datetime
from fastapi import FastAPI, HTTPException
from models import SimulationRequest, SimulationResult, BeamPatternRequest, BeamPatternResult
from sionna_runner import run_awgn_simulation
from beam_pattern import compute_ula_beam_pattern

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
    return {"status": "ok", "service": "sionna-bridge", "version": "2.0.0"}


# ---------------------------------------------------------------------------
# Core simulation endpoint
# ---------------------------------------------------------------------------

@app.post("/simulate", response_model=SimulationResult)
def simulate(request: SimulationRequest):
    """
    Run an AWGN BER-vs-SNR simulation with the supplied parameters.

    Returns both a theoretical BER curve (closed-form) and a Monte-Carlo
    simulated BER curve.  Raises HTTP 500 on any internal error so callers
    always get a structured failure message instead of a silent empty body.
    """
    try:
        result = run_awgn_simulation(
            modulation_order=request.modulation_order,
            code_rate=request.code_rate,
            num_bits_per_symbol=request.num_bits_per_symbol,
            snr_min=request.snr_min,
            snr_max=request.snr_max,
            snr_steps=request.snr_steps,
        )
        return SimulationResult(**result)

    except Exception as exc:
        raise HTTPException(
            status_code=500,
            detail=f"Simulation failed: {str(exc)}",
        )


# ---------------------------------------------------------------------------
# Demo endpoint — POST with default QPSK parameters
# ---------------------------------------------------------------------------

@app.post("/simulate/demo", response_model=SimulationResult)
def simulate_demo(request: SimulationRequest = None):
    """
    Runs a simulation with default QPSK rate-1/2 params.
    If a body is provided, it uses those params.
    """
    if request is None:
        request = SimulationRequest()
    return simulate(request)


# ---------------------------------------------------------------------------
# Beam Pattern endpoint
# ---------------------------------------------------------------------------

@app.post("/simulate/beam-pattern", response_model=BeamPatternResult)
def simulate_beam_pattern(request: BeamPatternRequest):
    """
    Run a ULA beam pattern generation with the supplied parameters.
    """
    try:
        result = compute_ula_beam_pattern(
            num_antennas=request.num_antennas,
            steering_angle=request.steering_angle,
            frequency_ghz=request.frequency_ghz,
            array_spacing=request.array_spacing,
        )
        return BeamPatternResult(**result)

    except Exception as exc:
        raise HTTPException(
            status_code=500,
            detail=f"Beam pattern computation failed: {str(exc)}",
        )


# ---------------------------------------------------------------------------
# Keep a GET /simulate/demo alias so the old Java GET call still works
# during the transition period before the Java service is updated.
# ---------------------------------------------------------------------------

@app.get("/simulate/demo", response_model=SimulationResult)
def simulate_demo_get():
    """Legacy GET alias — kept for backward compatibility."""
    return simulate(SimulationRequest())
