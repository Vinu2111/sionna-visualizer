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
import time
import traceback
import tracemalloc
import importlib.metadata
from fastapi import FastAPI, HTTPException, Request
from fastapi.responses import JSONResponse
from models import (
    SimulationRequest, SimulationResult, 
    BeamPatternRequest, BeamPatternResult,
    ModulationComparisonRequest, ModulationComparisonResult,
    ChannelCapacityRequest, ChannelCapacityResult,
    PathLossRequest, PathLossResult,
    SimulationEstimateRequest, SimulationEstimateResult,
    RayDirectionRequest, RayDirectionResult,
    UeTrajectoryRequest, UeTrajectoryResult,
    MeasurementOverlayRequest, MeasurementOverlayResult,
    SinrSteeringRequest, SinrSteeringResult
)
from sionna_runner import run_awgn_simulation
from beam_pattern import compute_ula_beam_pattern
from modulation_comparison import compute_modulation_comparison
from channel_capacity import compute_channel_capacity
from path_loss import compute_path_loss
from estimate import compute_estimate
from colormap import colormap_service
from ray_directions import compute_ray_directions
from ue_trajectory import simulate_ue_trajectory
from measurement_overlay import compute_measurement_overlay
from sinr_steering import compute_sinr_steering

app = FastAPI(
    title="Sionna Visualizer Bridge",
    description="CPU-only AWGN BER-vs-SNR simulation microservice",
    version="2.0.0",
)


# ---------------------------------------------------------------------------
# Global exception handler — catches ALL unhandled errors
# Returns structured JSON instead of a raw 500 stack trace
# ---------------------------------------------------------------------------

@app.exception_handler(Exception)
async def global_exception_handler(request: Request, exc: Exception):
    error_detail = traceback.format_exc()
    print(f"Unhandled error on {request.url}: {error_detail}")
    return JSONResponse(
        status_code=500,
        content={
            "error": str(exc),
            "path": str(request.url),
            "message": "Simulation engine error — check Railway logs"
        }
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
# Performance Tracking Helper
# ---------------------------------------------------------------------------

def get_compute_type():
    try:
        import torch
        return "GPU" if torch.cuda.is_available() else "CPU"
    except ImportError:
        return "CPU"

def get_sionna_version():
    try:
        return importlib.metadata.version("sionna")
    except Exception:
        return "unknown"

async def run_with_performance(func, *args):
    tracemalloc.start()
    start_time = time.time()
    
    result = await asyncio.wait_for(
        anyio.to_thread.run_sync(func, *args),
        timeout=30.0
    )
    
    end_time = time.time()
    _, peak_memory = tracemalloc.get_traced_memory()
    tracemalloc.stop()
    
    duration_ms = int((end_time - start_time) * 1000)
    memory_mb = round(float(peak_memory) / (1024 * 1024), 2)
    
    result["performance"] = {
        "duration_ms": duration_ms,
        "compute_type": get_compute_type(),
        "memory_mb": memory_mb,
        "sionna_version": get_sionna_version()
    }
    return result


# ---------------------------------------------------------------------------
# Core simulation endpoint
# ---------------------------------------------------------------------------

@app.get("/warmup")
async def warmup():
    """
    Runs a tiny BPSK simulation with 3 SNR points to keep the service warm.
    Always returns 200 even if the simulation fails — warmup failure must never crash.
    """
    try:
        request = SimulationRequest(
            modulation_order=2,
            code_rate=0.5,
            num_bits_per_symbol=1,
            snr_min=-5.0,
            snr_max=5.0,
            snr_steps=3
        )
        await simulate(request)
        return {"status": "warm", "message": "Bridge ready"}
    except Exception as e:
        print(f"Warmup failed (non-fatal): {e}")
        return {"status": "cold", "message": str(e)}

@app.post("/simulate", response_model=SimulationResult)
async def simulate(request: SimulationRequest):
    """
    Run an AWGN BER-vs-SNR simulation with the supplied parameters.
    """
    try:
        result = await run_with_performance(
            run_awgn_simulation,
            request.modulation_order,
            request.code_rate,
            request.num_bits_per_symbol,
            request.snr_min,
            request.snr_max,
            request.snr_steps,
        )
        # 2 data series: theoretical + simulated
        result["colors"] = colormap_service.get_colors(request.colormap, 2)
        result["colormap_used"] = request.colormap
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
        result = await run_with_performance(
            compute_ula_beam_pattern,
            request.num_antennas,
            request.steering_angle,
            request.frequency_ghz,
            request.array_spacing,
        )
        # 1 data series
        result["colors"] = colormap_service.get_colors(request.colormap, 1)
        result["colormap_used"] = request.colormap
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
        result = await run_with_performance(
            compute_modulation_comparison,
            request.snr_min,
            request.snr_max,
            request.snr_steps,
        )
        # 4 modulations
        result["colors"] = colormap_service.get_colors(request.colormap, 4)
        result["colormap_used"] = request.colormap
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
        result = await run_with_performance(
            compute_channel_capacity,
            request.snr_min,
            request.snr_max,
            request.snr_steps,
            request.bandwidths_mhz
        )
        num_bw = len(request.bandwidths_mhz)
        result["colors"] = colormap_service.get_colors(request.colormap, num_bw)
        result["colormap_used"] = request.colormap
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

# ---------------------------------------------------------------------------
# Path Loss endpoint
# ---------------------------------------------------------------------------

@app.post("/simulate/path-loss", response_model=PathLossResult)
async def simulate_path_loss(request: PathLossRequest):
    """
    Compute path loss for per-ray breakdown based on environment.
    """
    try:
        result = await run_with_performance(
            compute_path_loss,
            request.num_paths,
            request.frequency_ghz,
            request.environment
        )
        result["colors"] = colormap_service.get_colors(request.colormap, request.num_paths)
        result["colormap_used"] = request.colormap
        return PathLossResult(**result)
    except Exception as exc:
        raise HTTPException(
            status_code=500,
            detail=f"Path loss computation failed: {str(exc)}",
        )

# ---------------------------------------------------------------------------
# Ray Directions endpoint
# ---------------------------------------------------------------------------

@app.post("/simulate/ray-directions", response_model=RayDirectionResult)
async def simulate_ray_directions(request: RayDirectionRequest):
    """
    Compute path loss, angles, and delay for per-ray breakdown based on environment.
    """
    try:
        result = await run_with_performance(
            compute_ray_directions,
            request.num_paths,
            request.frequency_ghz,
            request.environment,
            request.tx_position,
            request.rx_position
        )
        result["colors"] = colormap_service.get_colors(request.colormap, request.num_paths)
        result["colormap_used"] = request.colormap
        return RayDirectionResult(**result)
    except Exception as exc:
        raise HTTPException(
            status_code=500,
            detail=f"Ray directions computation failed: {str(exc)}",
        )


# ---------------------------------------------------------------------------
# UE Trajectory endpoint
# ---------------------------------------------------------------------------

@app.post("/simulate/ue-trajectory", response_model=UeTrajectoryResult)
async def simulate_ue_trajectory_endpoint(request: UeTrajectoryRequest):
    """
    Compute moving UE path on a coverage map.
    """
    try:
        # It's a synchronous function, so normally we might want to run in thread
        # but for simplicity, we can do:
        result = await anyio.to_thread.run_sync(simulate_ue_trajectory, request)
        result.colors = colormap_service.get_colors(request.colormap, len(result.waypoints))
        result.colormap_used = request.colormap
        return result
    except Exception as exc:
        raise HTTPException(
            status_code=500,
            detail=f"UE trajectory computation failed: {str(exc)}",
        )


# ---------------------------------------------------------------------------
# Estimate compute time
# ---------------------------------------------------------------------------

@app.post("/simulate/estimate", response_model=SimulationEstimateResult)
async def simulate_estimate(request: SimulationEstimateRequest):
    """
    Returns an estimate of the compute time (in ms) before running a simulation.
    """
    try:
        est_ms, r_min, r_max, label, color, tips = compute_estimate(
            request.simulation_type, request.parameters
        )
        return SimulationEstimateResult(
            simulation_type=request.simulation_type,
            estimated_ms=est_ms,
            estimated_range={"min_ms": r_min, "max_ms": r_max},
            complexity_label=label,
            complexity_color=color,
            tips=tips,
            parameters_received=request.parameters
        )
    except Exception as exc:
        raise HTTPException(
            status_code=500,
            detail=f"Failed to generate estimate: {str(exc)}",
        )


# ---------------------------------------------------------------------------
# Measurement Overlay endpoint
# ---------------------------------------------------------------------------

@app.post("/simulate/measurement-overlay", response_model=MeasurementOverlayResult)
async def simulate_measurement_overlay(request: MeasurementOverlayRequest):
    """
    Compare real-world BER measurements against simulated AWGN curve.
    Returns calibration quality, error analysis, and comparison points.
    """
    try:
        measurements_raw = [m.model_dump() for m in request.measurements]
        result = await run_with_performance(
            compute_measurement_overlay,
            request.simulation_type,
            request.simulation_id,
            measurements_raw,
            request.frequency_ghz,
            request.environment,
        )
        return MeasurementOverlayResult(**result)
    except asyncio.TimeoutError:
        raise HTTPException(status_code=408, detail="Measurement overlay timed out.")
    except Exception as exc:
        raise HTTPException(status_code=500, detail=f"Measurement overlay failed: {str(exc)}")


# ---------------------------------------------------------------------------
# SINR Steering endpoint
# ---------------------------------------------------------------------------

@app.post("/simulate/sinr-steering", response_model=SinrSteeringResult)
async def simulate_sinr_steering(request: SinrSteeringRequest):
    """
    Compute SINR across steering angles for a ULA with a defined interferer.
    Returns per-angle array gain, interference suppression, SINR, and efficiency.
    """
    try:
        result = await run_with_performance(
            compute_sinr_steering,
            request.num_antennas,
            request.frequency_ghz,
            request.steering_angles,
            request.interference_angle_deg,
            request.signal_power_dbm,
            request.interference_power_dbm,
        )
        return SinrSteeringResult(**result)
    except asyncio.TimeoutError:
        raise HTTPException(status_code=408, detail="SINR steering computation timed out.")
    except Exception as exc:
        raise HTTPException(status_code=500, detail=f"SINR steering computation failed: {str(exc)}")


# ---------------------------------------------------------------------------
# GET /simulate/colormaps — list all available palettes
# ---------------------------------------------------------------------------

@app.get("/simulate/colormaps")
async def list_colormaps():
    """
    Returns all registered colormaps with a 5-colour preview strip each.
    No authentication required.
    """
    return {"colormaps": colormap_service.list_all()}
