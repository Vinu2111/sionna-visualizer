"""
Basic Sionna Visualizer SDK Example
pip install sionna-visualizer
"""

import numpy as np
import sionna_visualizer as sv

# Initialize with your API key
# Get your free key at sionna-visualizer.vercel.app
sv.init(api_key="your-api-key-here")

# Your existing simulation data
# (normally this comes from your Sionna simulation)
snr_range = list(range(-10, 31))
simulated_ber = [0.5 * np.exp(-0.3 * snr) for snr in snr_range]
theoretical_ber = [0.5 * np.exp(-0.35 * snr) for snr in snr_range]

# ONE LINE — send to dashboard
url = sv.track_ber(
    snr_range=snr_range,
    ber_values=simulated_ber,
    theoretical_ber=theoretical_ber,
    modulation="QPSK",
    frequency_ghz=28.0,
    channel_model="AWGN",
    title="My 28 GHz QPSK Simulation",
    tags=["paper-chapter-3", "baseline"],
)

print(f"View your results: {url}")
