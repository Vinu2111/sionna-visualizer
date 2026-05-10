from typing import Dict, Any, List, Tuple
import math

def compute_estimate(simulation_type: str, params: Dict[str, Any]) -> Tuple[int, int, int, str, str, List[str]]:
    tips: List[str] = ["Sionna provides high accuracy for multi-path distributions and channel capacity limits."]
    estimated_ms = 0
    
    sim_type = simulation_type.upper()
    
    if sim_type == "AWGN":
        base_ms = 50
        snr_steps = int(params.get("snr_steps", 25))
        snr_factor = snr_steps / 10.0
        
        mod = str(params.get("modulation", "QPSK")).upper()
        mod_factor = 1.0
        if "BPSK" in mod: mod_factor = 1.0
        elif "QPSK" in mod: mod_factor = 1.2
        elif "16QAM" in mod: mod_factor = 1.5
        elif "64QAM" in mod: mod_factor = 2.0
            
        estimated_ms = int(base_ms * snr_factor * mod_factor)
        
        if "64QAM" in mod and snr_steps > 20:
            tips.append("Reduce SNR steps to speed up simulation for high order modulations like 64QAM.")
        if snr_steps > 30:
            tips.append("Consider reducing SNR steps to 20 for a faster overview.")
            
    elif sim_type == "BEAM_PATTERN":
        base_ms = 80
        num_antennas = int(params.get("num_antennas", 16))
        freq_ghz = float(params.get("frequency_ghz", 28.0))
        
        estimated_ms = int(base_ms * (num_antennas / 8.0) * (freq_ghz / 28.0))
        
        if num_antennas > 32:
            tips.append("Warning: Large antenna arrays (>32) significantly increase ray tracing compute time.")
            
    elif sim_type == "MODULATION_COMPARISON":
        # AWGN estimate * 4 (always 4 modulations)
        base_ms = 50
        snr_steps = int(params.get("snr_steps", 50))
        # average mod factor for 4 modulations is ~1.425, or we can just use BPSK=1 assuming we run all 4. 
        # Instructions: formula: AWGN estimate * 4
        # Assuming AWGN estimate is for BPSK=1.0 ? Let's use 1.0.
        awgn_est = base_ms * (snr_steps / 10.0) * 1.0
        estimated_ms = int(awgn_est * 4)
        
    elif sim_type == "CHANNEL_CAPACITY":
        base_ms = 60
        snr_steps = int(params.get("snr_steps", 50))
        estimated_ms = int(base_ms * (snr_steps / 10.0))
        
    elif sim_type == "PATH_LOSS":
        base_ms = 40
        num_paths = int(params.get("num_paths", 8))
        estimated_ms = int(base_ms * (num_paths / 8.0))
        
        if num_paths > 16:
            tips.append("Consider starting with 8 paths for a quicker initial path loss visualization.")
            
    else:
        estimated_ms = 100

    r_min = math.floor(estimated_ms * 0.8)
    r_max = math.ceil(estimated_ms * 1.2)
    
    if estimated_ms < 200:
        label = "Fast"
        color = "green"
    elif estimated_ms <= 500:
        label = "Medium"
        color = "yellow"
    elif estimated_ms <= 2000:
        label = "Slow"
        color = "orange"
    else:
        label = "Heavy"
        color = "red"
        
    return estimated_ms, r_min, r_max, label, color, tips
