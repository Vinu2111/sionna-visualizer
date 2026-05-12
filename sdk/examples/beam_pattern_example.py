"""
Beam Pattern tracking example with one-line SDK call.
"""

import numpy as np
import sionna_visualizer as sv

sv.init(api_key="your-api-key-here")

angles = np.linspace(-90, 90, 181).tolist()
pattern = (20 * np.log10(np.abs(np.sinc(np.linspace(-3, 3, 181))) + 1e-6)).tolist()

url = sv.track_beam_pattern(
    angles_deg=angles,
    pattern_db=pattern,
    num_antennas=16,
    frequency_ghz=28.0,
    title="16-element ULA beam pattern",
)

print("View beam pattern:", url)
