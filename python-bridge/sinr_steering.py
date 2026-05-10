"""
sinr_steering.py — SINR visualization across steering angles for ULA beamforming.

Uses standard ULA (Uniform Linear Array) beamforming formula to compute:
- Array factor gain at each steering angle
- Interference suppression
- SINR per steering angle
- Beam efficiency
"""

import math
import numpy as np


def compute_array_factor(num_antennas: int, steering_angle_deg: float, query_angle_deg: float) -> complex:
    """
    Compute the ULA array factor at query_angle when steered to steering_angle.

    AF(θ) = Σ exp(j*2π*n*d*sin(θ)/λ) for n=0..N-1, d=λ/2
    After applying steering (phase shift for steering_angle), we measure gain at query_angle.
    """
    N = num_antennas
    d = 0.5  # half-wavelength spacing (in units of λ)
    
    # Phase progression for steering
    steering_rad = math.radians(steering_angle_deg)
    query_rad = math.radians(query_angle_deg)
    
    # Each element n contributes exp(j * 2π * n * d * (sin(query) - sin(steering)))
    # This is the conventional phased-array expression with conjugate phase compensation
    psi = 2 * math.pi * d * (math.sin(query_rad) - math.sin(steering_rad))
    
    af = sum(math.e ** (1j * n * psi) for n in range(N))
    return af


def compute_sinr_steering(
    num_antennas: int,
    frequency_ghz: float,
    steering_angles: list,
    interference_angle_deg: float,
    signal_power_dbm: float,
    interference_power_dbm: float,
) -> dict:
    """
    Compute SINR at each steering angle for a ULA with interference.

    Args:
        num_antennas: Number of ULA antennas (8, 16, 32, 64)
        frequency_ghz: Carrier frequency in GHz
        steering_angles: List of steering angles to evaluate (degrees)
        interference_angle_deg: Angle of the interference source (degrees)
        signal_power_dbm: Desired signal power in dBm
        interference_power_dbm: Interference power in dBm

    Returns:
        dict with steering_results, optimal_steering, summary, performance placeholder
    """
    N = num_antennas
    noise_db = -100.0  # thermal noise floor in dBm

    # Signal is assumed to come from broadside (0°) direction unless otherwise computed.
    # We evaluate SINR when beam is steered to various angles; the desired signal arrives
    # from the direction of "max gain" in this simplified model, which is broadside (0°).
    # For a realistic CW scenario: signal arrives from 0°, interferer from interference_angle.
    signal_angle_deg = 0.0

    steering_results = []

    for angle in steering_angles:
        # ── Array gain at signal direction ────────────────────────────────────
        af_signal = compute_array_factor(N, angle, signal_angle_deg)
        array_gain_db = 20 * math.log10(abs(af_signal) / N + 1e-12)

        # ── Interference suppression ──────────────────────────────────────────
        af_interference = compute_array_factor(N, angle, interference_angle_deg)
        interference_gain_db = 20 * math.log10(abs(af_interference) / N + 1e-12)

        # ── SINR at this steering angle ───────────────────────────────────────
        signal_total_db = signal_power_dbm + array_gain_db
        interference_total_db = interference_power_dbm + interference_gain_db

        # Convert to linear, add noise, convert SINR to dB
        sig_lin = 10 ** (signal_total_db / 10)
        int_lin = 10 ** (interference_total_db / 10)
        noise_lin = 10 ** (noise_db / 10)

        sinr_db = 10 * math.log10(sig_lin / (int_lin + noise_lin + 1e-30))

        steering_results.append({
            "steering_angle_deg": float(angle),
            "array_gain_db": round(array_gain_db, 4),
            "interference_gain_db": round(interference_gain_db, 4),
            "sinr_db": round(sinr_db, 4),
            "efficiency_percent": None,  # filled in after finding max_sinr
            "is_optimal": False
        })

    # ── Optimal steering (max SINR) ───────────────────────────────────────────
    sinr_values = [r["sinr_db"] for r in steering_results]
    max_sinr_db = max(sinr_values)
    min_sinr_db = min(sinr_values)
    opt_idx = int(np.argmax(sinr_values))

    # Compute max possible SINR (steering directly at signal angle = 0°)
    af_max = compute_array_factor(N, signal_angle_deg, signal_angle_deg)
    max_ag = 20 * math.log10(abs(af_max) / N + 1e-12)
    af_int_max = compute_array_factor(N, signal_angle_deg, interference_angle_deg)
    max_ig = 20 * math.log10(abs(af_int_max) / N + 1e-12)
    sig_max = 10 ** ((signal_power_dbm + max_ag) / 10)
    int_max = 10 ** ((interference_power_dbm + max_ig) / 10)
    noise_lin_max = 10 ** (noise_db / 10)
    max_possible_sinr = 10 * math.log10(sig_max / (int_max + noise_lin_max + 1e-30))

    # Fill efficiency and mark optimal
    for i, r in enumerate(steering_results):
        eff = (r["sinr_db"] / max_possible_sinr * 100) if max_possible_sinr != 0 else 0
        r["efficiency_percent"] = round(eff, 2)
        r["is_optimal"] = (i == opt_idx)

    optimal_result = steering_results[opt_idx]
    optimal_steering = {
        "angle_deg": optimal_result["steering_angle_deg"],
        "sinr_db": optimal_result["sinr_db"],
        "array_gain_db": optimal_result["array_gain_db"]
    }

    # ── Null angle (minimum interference gain across all steering angles) ────
    int_gains = [r["interference_gain_db"] for r in steering_results]
    null_idx = int(np.argmin(int_gains))
    interference_null_angle = steering_results[null_idx]["steering_angle_deg"]

    summary = {
        "max_sinr_db": round(max_sinr_db, 4),
        "min_sinr_db": round(min_sinr_db, 4),
        "sinr_range_db": round(max_sinr_db - min_sinr_db, 4),
        "num_angles_above_10db": sum(1 for v in sinr_values if v > 10),
        "interference_null_angle": interference_null_angle
    }

    return {
        "steering_results": steering_results,
        "optimal_steering": optimal_steering,
        "summary": summary
    }
