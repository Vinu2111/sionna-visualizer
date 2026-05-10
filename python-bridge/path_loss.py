import math
import random

def compute_path_loss(num_paths: int, frequency_ghz: float, environment: str):
    if environment.lower() == "urban":
        min_d, max_d = 50, 500
    elif environment.lower() == "suburban":
        min_d, max_d = 100, 1000
    else:  # rural
        min_d, max_d = 200, 5000

    distances = sorted([random.uniform(min_d, max_d) for _ in range(num_paths)])
    
    c = 3e8
    f_hz = frequency_ghz * 1e9
    
    paths = []
    for i, d in enumerate(distances):
        fspl = 20 * math.log10(d) + 20 * math.log10(f_hz) + 20 * math.log10(4 * math.pi / c)
        delay = (d / c) * 1e9
        
        paths.append({
            "path_id": int(i + 1),
            "distance_m": float(round(d, 2)),
            "path_loss_db": float(round(fspl, 2)),
            "path_type": "LOS" if i == 0 else "NLOS",
            "delay_ns": float(round(delay, 2))
        })
    
    los_pl = float(paths[0]["path_loss_db"])
    max_pl = float(max(p["path_loss_db"] for p in paths))
    spread = float(max_pl - los_pl)
    mean_delay = float(sum(p["delay_ns"] for p in paths)) / len(paths)
    
    return {
        "paths": paths,
        "summary": {
            "los_path_loss_db": round(los_pl, 2),
            "max_path_loss_db": round(max_pl, 2),
            "path_loss_spread_db": round(spread, 2),
            "mean_delay_ns": round(mean_delay, 2)
        }
    }
