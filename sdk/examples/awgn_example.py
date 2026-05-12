"""
Sionna + Sionna Visualizer Integration Example
Shows how to add ONE line to existing Sionna code
"""

import numpy as np
import sionna_visualizer as sv

# Initialize SDK
sv.init(api_key="your-api-key-here")


def mock_sionna_awgn_simulation(modulation, snr_range):
    # Simulates what Sionna AWGN returns.
    ber_values = []
    for snr in snr_range:
        snr_linear = 10 ** (snr / 10)
        if modulation == "BPSK":
            ber = 0.5 * np.erfc(np.sqrt(snr_linear))
        elif modulation == "QPSK":
            ber = 0.5 * np.erfc(np.sqrt(snr_linear / 2))
        else:
            ber = 0.5 * np.erfc(np.sqrt(snr_linear / 4))
        ber_values.append(float(ber))
    return ber_values


# Run simulation
snr_range = list(range(-10, 31, 2))
ber_values = mock_sionna_awgn_simulation("QPSK", snr_range)

# Track with ONE line — this is the magic
url = sv.track_ber(
    snr_range=snr_range,
    ber_values=ber_values,
    modulation="QPSK",
    frequency_ghz=28.0,
    channel_model="AWGN",
    title="AWGN QPSK 28 GHz",
    tags=["awgn", "qpsk", "28ghz"],
)

print(f"\n✅ Visualization ready: {url}\n")
