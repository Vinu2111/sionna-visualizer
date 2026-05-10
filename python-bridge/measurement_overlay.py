"""
measurement_overlay.py — Real measurement data import and calibration analysis.

Computes calibration quality by comparing real-world BER measurements against
saved AWGN simulation results.
"""

import math
import numpy as np
from scipy.special import erfc


def compute_awgn_ber_at_snr(snr_db: float, modulation: str = "QPSK") -> float:
    """Compute theoretical BER for a given SNR using AWGN formulas."""
    snr_linear = 10 ** (snr_db / 10)
    mod = modulation.upper()
    if mod in ("BPSK", "QPSK"):
        return 0.5 * erfc(math.sqrt(snr_linear))
    elif mod == "16QAM":
        return 0.375 * erfc(math.sqrt(snr_linear * 0.1))
    elif mod == "64QAM":
        return (7 / 12) * erfc(math.sqrt(snr_linear * 2 / 42))
    else:
        return 0.5 * erfc(math.sqrt(snr_linear))


def compute_measurement_overlay(
    simulation_type: str,
    simulation_id: int,
    measurements: list,
    frequency_ghz: float,
    environment: str,
) -> dict:
    """
    Compare real measurement BER points against theoretical AWGN simulation.

    Args:
        simulation_type: e.g. "AWGN"
        simulation_id: ID of saved simulation (we regenerate the curve analytically)
        measurements: list of dicts with snr_db, ber_measured, and optional location
        frequency_ghz: carrier frequency in GHz
        environment: deployment scenario label

    Returns:
        dict with comparison_points, calibration_summary, simulation_type, performance
    """
    comparison_points = []
    error_db_values = []

    for m in measurements:
        snr_db = float(m["snr_db"])
        ber_measured = float(m["ber_measured"])

        # Compute simulated BER at this SNR (theoretical QPSK as baseline)
        ber_simulated = compute_awgn_ber_at_snr(snr_db, "QPSK")

        absolute_error = abs(ber_simulated - ber_measured)
        relative_error_pct = (
            (absolute_error / ber_simulated * 100) if ber_simulated > 0 else 0.0
        )

        # error_db = 10*log10(ber_measured / ber_simulated) if both > 0
        err_db = None
        if ber_measured > 0 and ber_simulated > 0:
            err_db = 10 * math.log10(ber_measured / ber_simulated)
            error_db_values.append(err_db)

        comparison_points.append(
            {
                "snr_db": round(snr_db, 3),
                "ber_simulated": ber_simulated,
                "ber_measured": ber_measured,
                "absolute_error": absolute_error,
                "relative_error_percent": round(relative_error_pct, 4),
                "error_db": round(err_db, 4) if err_db is not None else None,
                "location": m.get("location", ""),
            }
        )

    # ── Calibration summary ──────────────────────────────────────────────────
    abs_errors = [p["absolute_error"] for p in comparison_points]
    rel_errors = [p["relative_error_percent"] for p in comparison_points]

    mean_abs_error = float(np.mean(abs_errors)) if abs_errors else 0.0
    mean_rel_error = float(np.mean(rel_errors)) if rel_errors else 0.0
    rmse = float(np.sqrt(np.mean(np.array(abs_errors) ** 2))) if abs_errors else 0.0

    systematic_offset_db = float(np.mean(error_db_values)) if error_db_values else 0.0

    # Max error point — SNR with highest absolute error
    max_idx = int(np.argmax(abs_errors)) if abs_errors else 0
    max_error_point = comparison_points[max_idx]["snr_db"] if comparison_points else 0.0

    # Calibration quality
    if mean_rel_error < 5:
        quality = "Excellent"
    elif mean_rel_error < 15:
        quality = "Good"
    elif mean_rel_error < 30:
        quality = "Fair"
    else:
        quality = "Poor"

    calibration_summary = {
        "mean_absolute_error": mean_abs_error,
        "rmse": rmse,
        "calibration_quality": quality,
        "systematic_offset_db": round(systematic_offset_db, 4),
        "max_error_point": max_error_point,
        "num_measurement_points": len(comparison_points),
    }

    return {
        "comparison_points": comparison_points,
        "calibration_summary": calibration_summary,
        "simulation_type": simulation_type,
    }
