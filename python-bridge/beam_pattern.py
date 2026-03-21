import math
import numpy as np

def compute_ula_beam_pattern(
    num_antennas: int,
    steering_angle: float,
    frequency_ghz: float,
    array_spacing: float
) -> dict:
    """
    Computes a realistic antenna beam pattern using the Uniform Linear Array (ULA) model.
    """
    
    # 1. Generate angles from -90 to 90 degrees (181 points)
    angles_deg = np.linspace(-90, 90, 181)
    angles_rad = np.radians(angles_deg)
    
    steering_rad = np.radians(steering_angle)
    
    # Array positions n = 0, 1, ..., N-1
    n = np.arange(num_antennas)
    
    # 2. Compute Array Factor (AF)
    # Phase shift psi incorporates both beam steering and physical angle
    psi = 2 * math.pi * array_spacing * (np.sin(angles_rad) - np.sin(steering_rad))
    phase_matrix = np.exp(1j * n[:, np.newaxis] * psi[np.newaxis, :])
    AF = np.abs(np.sum(phase_matrix, axis=0))
    
    # Normalize AF
    AF_max = np.max(AF)
    if AF_max > 0:
        AF_norm = AF / AF_max
    else:
        AF_norm = AF
    
    # 3. Convert to dB scale
    pattern_db = 20 * np.log10(AF_norm + 1e-10)
    
    # 4. Extract metrics
    array_gain_db = 10 * math.log10(num_antennas)
    
    # Find Main Lobe Width (3dB beamwidth)
    peak_idx = np.argmax(pattern_db)
    
    right_idx = peak_idx
    while right_idx < len(pattern_db) and pattern_db[right_idx] >= -3.0:
        right_idx += 1
    
    left_idx = peak_idx
    while left_idx >= 0 and pattern_db[left_idx] >= -3.0:
        left_idx -= 1
        
    left_idx = max(0, left_idx)
    right_idx = min(len(pattern_db)-1, right_idx)
    
    main_lobe_width = float(angles_deg[right_idx] - angles_deg[left_idx])
    
    # Side Lobe Level (SLL)
    # Find first nulls to mask out the main lobe completely
    db_masked = pattern_db.copy()
    
    null_right = peak_idx
    while null_right < len(pattern_db)-1 and pattern_db[null_right+1] < pattern_db[null_right]:
        null_right += 1
        
    null_left = peak_idx
    while null_left > 0 and pattern_db[null_left-1] < pattern_db[null_left]:
        null_left -= 1
        
    db_masked[null_left:null_right+1] = -100
    side_lobe_level = float(np.max(db_masked))
    if side_lobe_level <= -99.0:
        side_lobe_level = -100.0 # No side lobes found
        
    # Return formatted JSON
    return {
        "angles": [round(float(a), 1) for a in angles_deg],
        "pattern_db": [round(float(p), 2) for p in pattern_db],
        "steering_angle": steering_angle,
        "num_antennas": num_antennas,
        "frequency_ghz": frequency_ghz,
        "main_lobe_width": round(main_lobe_width, 2),
        "side_lobe_level": round(side_lobe_level, 2),
        "array_gain_db": round(array_gain_db, 2)
    }
