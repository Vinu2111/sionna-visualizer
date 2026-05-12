# Sionna Visualizer Python SDK

> Visualize your NVIDIA Sionna 6G simulation results with one line of code.

## Install

```bash
pip install sionna-visualizer
```

## Quick Start

```python
import sionna_visualizer as sv

sv.init(api_key="your-key")

url = sv.track_ber(
    snr_range=[-10, -5, 0, 5, 10, 15, 20],
    ber_values=[0.45, 0.32, 0.18, 0.08, 0.02, 0.004, 0.0005],
    modulation="QPSK",
    frequency_ghz=28.0,
    title="My Simulation"
)
print(url)
# https://sionna-visualizer.vercel.app/share/abc123
```

## Get Your Free API Key

Visit sionna-visualizer.vercel.app/api-docs

## Full Documentation

Visit sionna-visualizer.vercel.app
