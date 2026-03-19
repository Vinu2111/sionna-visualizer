import datetime
from fastapi import FastAPI
from models import SimulationRequest, SimulationResult
from sionna_runner import run_ofdm_simulation

app = FastAPI(title="Sionna Visualizer Bridge")

@app.get("/health")
def health_check():
    """
    Health check endpoint for the bridge service.
    This remains unchanged as requested.
    """
    return {"status": "ok", "service": "sionna-bridge"}


@app.post("/simulate", response_model=SimulationResult)
def simulate(request: SimulationRequest):
    """
    Run an OFDM simulation using parameters sent in the HTTP Request Body.
    If NVIDIA Sionna is unavailable, it gracefully returns a mock curve.
    """
    
    # 1. Unpack user request values, falling back to Pydantic defaults if missing
    snr_db, ber, metadata = run_ofdm_simulation(
        num_ofdm_symbols=request.num_ofdm_symbols,
        fft_size=request.fft_size,
        snr_min=request.snr_min,
        snr_max=request.snr_max
    )
    
    # 2. Structure the data into our Pydantic response entity
    # Generating an ISO timestamp so the frontend knows exactly when this ran
    timestamp = datetime.datetime.utcnow().isoformat()
    
    return SimulationResult(
        snr_db=snr_db,
        ber=ber,
        metadata=metadata,
        timestamp=timestamp
    )


@app.get("/simulate/demo", response_model=SimulationResult)
def simulate_demo():
    """
    Run a fast simulation using standard default parameters.
    Very useful when developing the Angular frontend layout since we don't 
    have to construct a complex payload object yet.
    """
    
    # Generate an empty request object which uses all the Pydantic defaults
    default_request = SimulationRequest()
    
    # We call the POST logic directly to adhere to DRY (Don't Repeat Yourself)
    return simulate(default_request)
