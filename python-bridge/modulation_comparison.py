import numpy as np
import scipy.special as sp

def compute_modulation_comparison(snr_min: float, snr_max: float, snr_steps: int):
    snr_db = np.linspace(snr_min, snr_max, snr_steps)
    snr_linear = 10.0 ** (snr_db / 10.0)
    
    # Formulas provided for theoretical BER
    bpsk_ber = 0.5 * sp.erfc(np.sqrt(snr_linear))
    qpsk_ber = 0.5 * sp.erfc(np.sqrt(snr_linear))
    qam16_ber = (3.0 / 8.0) * sp.erfc(np.sqrt(snr_linear / 10.0))
    qam64_ber = (7.0 / 24.0) * sp.erfc(np.sqrt(snr_linear / 42.0))

    # Clamp all to 1e-7 minimum to avoid log of zero graphs
    bpsk_ber = np.clip(bpsk_ber, 1e-7, None)
    qpsk_ber = np.clip(qpsk_ber, 1e-7, None)
    qam16_ber = np.clip(qam16_ber, 1e-7, None)
    qam64_ber = np.clip(qam64_ber, 1e-7, None)

    def find_snr_for_ber(ber_array, target_ber=0.001):
        for i, ber in enumerate(ber_array):
            if ber <= target_ber:
                return float(snr_db[i])
        return float(snr_db[-1])

    bpsk_target_snr = find_snr_for_ber(bpsk_ber)
    qpsk_target_snr = find_snr_for_ber(qpsk_ber)
    qam16_target_snr = find_snr_for_ber(qam16_ber)
    qam64_target_snr = find_snr_for_ber(qam64_ber)

    qpsk_adv = round(qam16_target_snr - qpsk_target_snr, 1)
    qam16_adv = round(qam64_target_snr - qam16_target_snr, 1)

    return {
        "snr_db": [round(float(s), 2) for s in snr_db],
        "bpsk": [float(b) for b in bpsk_ber],
        "qpsk": [float(b) for b in qpsk_ber],
        "qam16": [float(b) for b in qam16_ber],
        "qam64": [float(b) for b in qam64_ber],
        "snr_min": float(snr_min),
        "snr_max": float(snr_max),
        "snr_steps": int(snr_steps),
        "crossover_points": {
            "bpsk_qpsk_same": True,
            "qpsk_advantage_over_16qam_at_snr": max(0.0, qpsk_adv),
            "qam16_advantage_over_64qam_at_snr": max(0.0, qam16_adv)
        }
    }
