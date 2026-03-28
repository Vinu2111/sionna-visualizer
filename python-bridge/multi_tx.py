import time
import math
import numpy as np
from typing import List
from models import MultiTxRequest, MultiTxResult, Transmitter, CoverageGrid, PerTxStats, NetworkSummary, PerformanceMetadata

def compute_fspl(distance: float, frequency_ghz: float) -> float:
    # Free Space Path Loss: 20*log10(d) + 20*log10(f) + 32.44
    if distance < 1.0:
        distance = 1.0
    return 20 * math.log10(distance) + 20 * math.log10(frequency_ghz) + 32.44

def get_shadowing_margin(environment: str) -> float:
    if environment == "urban": return 8.0
    if environment == "suburban": return 5.0
    return 3.0

def run_multi_tx_simulation(req: MultiTxRequest) -> MultiTxResult:
    start_time = time.time()

    grid_size = req.grid_size
    # Assume 400x400 area
    area_size = 400.0
    step = area_size / grid_size
    
    env_margin = get_shadowing_margin(req.environment)
    noise_power_dbm = -100.0

    best_signal = np.zeros((grid_size, grid_size))
    serving_tx = np.zeros((grid_size, grid_size), dtype=int)
    sinr = np.zeros((grid_size, grid_size))
    classification_grid = []

    # Store signals for interference computation
    # shape: (num_tx, grid_size, grid_size)
    tx_signals = np.zeros((len(req.transmitters), grid_size, grid_size))
    
    cells_served = {tx.tx_id: 0 for tx in req.transmitters}
    overlap_cells = {tx.tx_id: 0 for tx in req.transmitters}
    sig_sums = {tx.tx_id: 0.0 for tx in req.transmitters}

    total_cells = grid_size * grid_size

    for row in range(grid_size):
        class_row = []
        for col in range(grid_size):
            x = col * step + step/2
            y = row * step + step/2

            best_sig = -999.0
            best_tx_id = -1
            best_idx = -1

            for idx, tx in enumerate(req.transmitters):
                dx = tx.position[0] - x
                dy = tx.position[1] - y
                # Add default Z if not provided or size < 3
                tz = tx.position[2] if len(tx.position) > 2 else 25.0
                dz = tz - 1.5 # UE height
                dist = math.sqrt(dx*dx + dy*dy + dz*dz)
                
                loss = compute_fspl(dist, req.frequency_ghz) + env_margin
                sig = tx.tx_power_dbm - loss
                tx_signals[idx, row, col] = sig

                if sig > best_sig:
                    best_sig = sig
                    best_tx_id = tx.tx_id
                    best_idx = idx

            best_signal[row, col] = best_sig
            serving_tx[row, col] = best_tx_id
            
            cells_served[best_tx_id] += 1
            sig_sums[best_tx_id] += best_sig

            if req.show_interference:
                # compute interference power
                interf_pwr_linear = 0.0
                overlap_count = 0
                for idx, tx in enumerate(req.transmitters):
                    if idx != best_idx:
                        s = tx_signals[idx, row, col]
                        interf_pwr_linear += math.pow(10, s/10.0)
                        if (best_sig - s) < 5.0:
                            overlap_count += 1
                
                if overlap_count > 0:
                    overlap_cells[best_tx_id] += 1

                noise_linear = math.pow(10, noise_power_dbm/10.0)
                tot_interf_linear = interf_pwr_linear + noise_linear
                sinr_val = best_sig - 10 * math.log10(tot_interf_linear)
                sinr[row, col] = sinr_val
            else:
                # no interference, just SNR
                snr_val = best_sig - noise_power_dbm
                sinr[row, col] = snr_val

            # Classification
            if best_sig > -70: cls = "strong"
            elif best_sig > -80: cls = "good"
            elif best_sig > -90: cls = "fair"
            elif best_sig > -100: cls = "weak"
            else: cls = "no_coverage"
            
            class_row.append(cls)
        
        classification_grid.append(class_row)
        
    per_tx_stats = []
    for tx in req.transmitters:
        c = cells_served[tx.tx_id]
        p = (c / total_cells) * 100 if total_cells > 0 else 0
        m = (sig_sums[tx.tx_id] / c) if c > 0 else -100
        per_tx_stats.append(PerTxStats(
            tx_id=tx.tx_id,
            label=tx.label,
            cells_served=c,
            coverage_percent=round(p, 2),
            mean_signal_dbm=round(m, 2),
            overlap_cells=overlap_cells[tx.tx_id]
        ))
        
    # network summary
    strong_cells = sum(1 for row in classification_grid for c in row if c == "strong")
    covered_cells = sum(1 for row in classification_grid for c in row if c != "no_coverage")
    interf_cells = np.sum(sinr < 10.0) if req.show_interference else 0
    holes = total_cells - covered_cells

    net_summary = NetworkSummary(
        total_coverage_percent=round((covered_cells / total_cells) * 100, 2),
        strong_coverage_percent=round((strong_cells / total_cells) * 100, 2),
        interference_zones_percent=round((int(interf_cells) / total_cells) * 100, 2),
        mean_sinr_db=round(float(np.mean(sinr)), 2),
        coverage_holes_percent=round((holes / total_cells) * 100, 2)
    )

    perf = PerformanceMetadata(
        duration_ms=int((time.time() - start_time) * 1000),
        compute_type="CPU",
        memory_mb=0.0,
        sionna_version="0.17"
    )

    return MultiTxResult(
        grid_size=req.grid_size,
        transmitters=req.transmitters,
        coverage_grid=CoverageGrid(
            best_signal=best_signal.tolist(),
            serving_tx=serving_tx.tolist(),
            sinr=sinr.tolist(),
            classification=classification_grid
        ),
        per_tx_stats=per_tx_stats,
        network_summary=net_summary,
        performance=perf,
        colormap_used=req.colormap
    )
