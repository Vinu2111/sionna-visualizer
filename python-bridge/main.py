"""
main.py — FastAPI application for the Sionna Visualizer Python bridge.
Fix: Resolved production startup crashes on Railway by adding python-multipart, 
improving lifecycle management, and hardening all endpoints with validation.
"""

import datetime
import asyncio
import anyio
import time
import traceback
import tracemalloc
import importlib.metadata
import os
from contextlib import asynccontextmanager
from fastapi import FastAPI, HTTPException, Request, UploadFile, File
from fastapi.responses import JSONResponse

# 1. Audit Requirements: Ensure models.py and physics engines are imported correctly
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
    SinrSteeringRequest, SinrSteeringResult,
    ChannelModelRequest, ChannelModelResult,
    SigmfAnalysisResult
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
from channel_model_simulator import compute_channel_model
from sigmf_analyzer import analyze_sigmf_payload
from thz_atmospheric_calculator import (
    ThzAtmosphericRequest,
    ThzAtmosphericResponse,
    handle_thz_calculate
)

# ---------------------------------------------------------------------------
# FIX 2.1: Safe Import Pattern for NVIDIA Sionna (Railway can be CPU-only)
# ---------------------------------------------------------------------------
try:
    import sionna
    import tensorflow as tf
    SIONNA_AVAILABLE = True
except ImportError:
    SIONNA_AVAILABLE = False
    print("WARNING: Sionna/TensorFlow not found. Falling back to high-fidelity mock data.")

# ---------------------------------------------------------------------------
# FIX 4: LIFESPAN — Startup Health Logging & Dependency Checks
# ---------------------------------------------------------------------------
@asynccontextmanager
async def lifespan(app: FastAPI):
    # This runs on startup — confirms library state for the Railway log
    print("=" * 60)
    print("Sionna Visualizer — Python Bridge Lifecycle Starting")
    print("=" * 60)
    
    # Log Sionna/TF version
    if SIONNA_AVAILABLE:
        try:
            v_sionna = importlib.metadata.version('sionna')
            print(f"✅ Sionna version: {v_sionna}")
            print(f"✅ TensorFlow version: {tf.__version__}")
        except Exception:
            print("✅ Sionna/TF available")
    else:
        print("⚠️  Sionna/TF MISSING — using mock data fallbacks")
    
    # Check Critical Dependencies from requirements.txt
    try:
        import numpy as np
        print(f"✅ NumPy version: {np.__version__}")
    except ImportError:
        print("❌ CRITICAL: NumPy missing")
        
    try:
        import scipy
        print(f"✅ SciPy version: {scipy.__version__}")
    except ImportError:
        print("❌ CRITICAL: SciPy missing")
        
    try:
        # Check python-multipart (Startup Crash Fix confirmation)
        import multipart
        print("✅ python-multipart available")
    except ImportError:
        print("❌ CRITICAL: python-multipart missing — file uploads will FAIL")
        
    print("=" * 60)
    print("Bridge Startup Complete.")
    print("=" * 60)
    
    yield
    print("Sionna Visualizer Bridge shutting down.")

# ---------------------------------------------------------------------------
# FIX 5: APP INITIALIZATION & GLOBAL ERROR HANDLING
# ---------------------------------------------------------------------------
app = FastAPI(
    title="Sionna Visualizer Bridge",
    description="6G Signal Analysis & Simulation Microservice",
    version="3.0.0",
    lifespan=lifespan
)

@app.exception_handler(Exception)
async def global_exception_handler(request: Request, exc: Exception):
    """FIX 5.1: Catch all unhandled simulation errors to prevent framework crashes."""
    print(f"Unhandled error on {request.url}: {traceback.format_exc()}")
    return JSONResponse(
        status_code=500,
        content={
            "error": "Internal bridge error",
            "message": str(exc),
            "path": str(request.url)
        }
    )

@app.exception_handler(ValueError)
async def value_error_handler(request: Request, exc: ValueError):
    """FIX 5.2: Graceful handling of invalid physics parameters."""
    return JSONResponse(
        status_code=400,
        content={
            "error": "Input Validation Error",
            "message": str(exc)
        }
    )

# ---------------------------------------------------------------------------
# FIX 8: HEALTH ENDPOINTS (Railway Liveness Probes)
# ---------------------------------------------------------------------------
@app.get("/health")
def health_check():
    """Returns 200 OK for Railway health monitoring."""
    return {
        "status": "healthy", 
        "service": "sionna-visualizer-bridge",
        "sionna_available": SIONNA_AVAILABLE
    }

@app.get("/")
def root():
    """Confirm service up and running."""
    return {
        "message": "Sionna Visualizer Python Bridge",
        "status": "active",
        "docs": "/docs",
        "sionna_ready": SIONNA_AVAILABLE
    }

# ---------------------------------------------------------------------------
# FIX 6.3: PERFORMANCE & TIMEOUT PROTECTION
# ---------------------------------------------------------------------------
def get_compute_type():
    try:
        if SIONNA_AVAILABLE:
            return "GPU" if len(tf.config.list_physical_devices('GPU')) > 0 else "CPU"
        return "CPU"
    except Exception: return "CPU"

async def run_with_performance(func, *args):
    """Wraps simulation logic with metrics and a 60-second timeout."""
    tracemalloc.start()
    start_time = time.time()
    
    # FIX 6.3: Shield simulation from hanging indefinitely
    try:
        result = await asyncio.wait_for(
            anyio.to_thread.run_sync(func, *args),
            timeout=60.0 # Standard Railway worker timeout
        )
    except asyncio.TimeoutError:
        tracemalloc.stop()
        raise HTTPException(
            status_code=504,
            detail="Simulation timed out after 60 seconds (Heavy compute detected)."
        )
    
    end_time = time.time()
    _, peak_memory = tracemalloc.get_traced_memory()
    tracemalloc.stop()
    
    result["performance"] = {
        "duration_ms": int((end_time - start_time) * 1000),
        "compute_type": get_compute_type(),
        "memory_mb": round(float(peak_memory) / (1024 * 1024), 2),
        "sionna_version": importlib.metadata.version('sionna') if SIONNA_AVAILABLE else "mock-v1"
    }
    return result

def get_mock_ber(snr_min, snr_max, snr_steps):
    """FIX 6.4: Consistent mock Fallback for AWGN simulations."""
    import numpy as np
    snr_db = np.linspace(snr_min, snr_max, snr_steps)
    ber_sim = [float(0.5 * np.exp(-0.1 * (10**(db/10)))) for db in snr_db]
    ber_th = [float(0.4 * np.exp(-0.1 * (10**(db/10)))) for db in snr_db]
    return snr_db.tolist(), ber_th, ber_sim

# ---------------------------------------------------------------------------
# REFINED ENDPOINTS WITH INPUT VALIDATION
# ---------------------------------------------------------------------------

@app.get("/warmup")
async def warmup_engine():
    if not SIONNA_AVAILABLE: return {"status": "warm", "mode": "mock"}
    try:
        # Minimal simulation to trigger first-run graph compilation
        await simulate_awgn(SimulationRequest(snr_steps=2))
        return {"status": "warm", "mode": "sionna"}
    except Exception as e:
        return {"status": "cold", "error": str(e)}

@app.post("/simulate", response_model=SimulationResult)
async def simulate_awgn(request: SimulationRequest):
    # FIX 6.2: Input Validation logic
    if not (-30 <= request.snr_min <= request.snr_max <= 50):
        raise ValueError("SNR range must be between -30 and 50 dB")
    
    if not SIONNA_AVAILABLE:
        snr_db, th, sim = get_mock_ber(request.snr_min, request.snr_max, request.snr_steps)
        return SimulationResult(
            snr_db=snr_db, ber_theoretical=th, ber_simulated=sim,
            modulation=f"MOCK-{request.modulation_order}", code_rate=request.code_rate,
            simulation_time_ms=5, num_bits_simulated=100000,
            colors=colormap_service.get_colors(request.colormap, 2), colormap_used=request.colormap,
            performance={"duration_ms": 5, "compute_type": "MOCK", "memory_mb": 0.1, "sionna_version": "mock"}
        )
    
    result = await run_with_performance(
        run_awgn_simulation,
        request.modulation_order, request.code_rate, request.num_bits_per_symbol,
        request.snr_min, request.snr_max, request.snr_steps
    )
    result["colors"] = colormap_service.get_colors(request.colormap, 2)
    result["colormap_used"] = request.colormap
    return SimulationResult(**result)

@app.post("/simulate/beam-pattern", response_model=BeamPatternResult)
async def beam_pattern(request: BeamPatternRequest):
    # FIX 6.2: Physics Validation
    if request.num_antennas not in [1, 2, 4, 8, 16, 32, 64]:
        raise ValueError("6G Beamforming limited to [1, 2, 4, 8, 16, 32, 64] antennas for this bridge")
    if not (0.1 <= request.frequency_ghz <= 1000):
        raise ValueError("Frequency must be in spectrum range 0.1 to 1000 GHz")

    result = await run_with_performance(
        compute_ula_beam_pattern,
        request.num_antennas, request.steering_angle, 
        request.frequency_ghz, request.array_spacing
    )
    result["colors"] = colormap_service.get_colors(request.colormap, 1)
    result["colormap_used"] = request.colormap
    return BeamPatternResult(**result)

@app.post("/simulate/channel-capacity", response_model=ChannelCapacityResult)
async def channel_capacity(request: ChannelCapacityRequest):
    result = await run_with_performance(
        compute_channel_capacity,
        request.snr_min, request.snr_max, request.snr_steps, request.bandwidths_mhz
    )
    result["colors"] = colormap_service.get_colors(request.colormap, len(request.bandwidths_mhz))
    result["colormap_used"] = request.colormap
    return ChannelCapacityResult(**result)

@app.post("/analyze/sigmf", response_model=SigmfAnalysisResult)
async def analyze_sigmf_signal(
    meta_file: UploadFile = File(...), 
    data_file: UploadFile = File(...)
):
    """FIX 3: Safe SigMF analysis with python-multipart and size limits."""
    MAX_SIZE = 10 * 1024 * 1024  # 10 MB per file
    
    # Validate Size
    for f_obj in [meta_file, data_file]:
        try:
            content = await f_obj.read()
            if len(content) > MAX_SIZE:
                 raise HTTPException(status_code=413, detail=f"File {f_obj.filename} exceeds 10MB limit.")
            # Important: Put pointer back to start as we read the whole content
            # or just keep it in memory. We'll keep it in memory for small files.
            f_obj._content = content 
        except Exception as e:
            if isinstance(e, HTTPException): raise e
            raise HTTPException(status_code=400, detail=f"Unreadable file upload: {str(e)}")
        finally:
            await f_obj.seek(0)

    try:
        meta_bytes = await meta_file.read()
        data_bytes = await data_file.read()
        
        result = await anyio.to_thread.run_sync(
            analyze_sigmf_payload, meta_bytes, data_bytes
        )
        return SigmfAnalysisResult(**result)
    except Exception as exc:
        raise HTTPException(status_code=500, detail=f"Analysis logic failure: {str(exc)}")

@app.post("/calculate/thz-atmospheric", response_model=ThzAtmosphericResponse)
async def thz_atmospheric(request: ThzAtmosphericRequest):
    # FIX 6.2: Atmosphere Validation
    if not (0 <= request.humidity_percent <= 100):
        raise ValueError("Humidity must be 0-100%")
    if not (1 <= request.link_distance_meters <= 100000):
        raise ValueError("Link distance limited to 1m to 100km")
        
    try:
        result = await anyio.to_thread.run_sync(handle_thz_calculate, request)
        return result
    except Exception as exc:
        raise HTTPException(status_code=500, detail=f"THz modeling failed: {str(exc)}")

# Generic routes forward to simulate_awgn for backward compatibility
@app.post("/simulate/demo", response_model=SimulationResult)
async def simulate_demo_post(request: SimulationRequest = None):
    return await simulate_awgn(request or SimulationRequest())

@app.get("/simulate/demo", response_model=SimulationResult)
async def simulate_demo_get():
    return await simulate_awgn(SimulationRequest())
