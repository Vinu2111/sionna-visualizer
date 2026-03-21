import numpy as np
import math

def compute_channel_capacity(snr_min=-10.0, snr_max=30.0, snr_steps=50, bandwidths_mhz=None):
    if bandwidths_mhz is None:
        bandwidths_mhz = [10.0, 100.0, 400.0, 1000.0]
        
    snr_db = np.linspace(snr_min, snr_max, snr_steps)
    snr_linear = 10 ** (snr_db / 10.0)
    
    spectral_efficiency = np.log2(1 + snr_linear)
    
    colors = {
        10.0: "#ffffff",
        100.0: "#64ffda",
        400.0: "#f7b731",
        1000.0: "#ff6b6b"
    }

    labels = {
        10.0: "10 MHz (Sub-6GHz basic)",
        100.0: "100 MHz (Sub-6GHz 6G)",
        400.0: "400 MHz (mmWave 6G)",
        1000.0: "1000 MHz (mmWave wide)"
    }
    
    capacity_curves = []
    
    for bw in bandwidths_mhz:
        bw_hz = bw * 1e6
        capacity_bps = bw_hz * np.array(spectral_efficiency)
        capacity_gbps = capacity_bps / 1e9
        
        capacity_curves.append({
            "bandwidth_mhz": float(bw),
            "label": labels.get(bw, f"{bw} MHz"),
            # pyre-ignore[16, 9]
            "capacity_gbps": [float(x) for x in capacity_gbps],
            "peak_capacity_gbps": float(np.max(capacity_gbps)),
            "color_hint": colors.get(bw, "#cccccc")
        })
        
    def get_snr_for_capacity(target_gbps, bw_mhz):
        target_bps = target_gbps * 1e9
        bw_hz = bw_mhz * 1e6
        power_term = target_bps / bw_hz
        snr_linear_req = (2 ** power_term) - 1
        if snr_linear_req <= 0:
            return 0.0
        return float(10 * math.log10(snr_linear_req))
        
    snr_for_1gbps_100mhz = get_snr_for_capacity(1.0, 100.0)
    snr_for_10gbps_1000mhz = get_snr_for_capacity(10.0, 1000.0)
    
    se_at_snr20 = math.log2(1 + (10 ** (20.0 / 10.0)))
    
    capacity_gain_10x_bandwidth = 10.0
    
    return {
        "snr_db": snr_db.tolist(),
        "spectral_efficiency": spectral_efficiency.tolist(),
        "capacity_curves": capacity_curves,
        "snr_min": float(snr_min),
        "snr_max": float(snr_max),
        "insights": {
            "snr_for_1gbps_100mhz": snr_for_1gbps_100mhz,
            "snr_for_10gbps_1000mhz": snr_for_10gbps_1000mhz,
            "spectral_efficiency_at_snr20": float(se_at_snr20),
            "capacity_gain_10x_bandwidth": capacity_gain_10x_bandwidth
        }
    }
