import math
import random

def compute_ray_directions(num_paths: int, frequency_ghz: float, environment: str, tx_pos: list, rx_pos: list):
    dx = rx_pos[0] - tx_pos[0]
    dy = rx_pos[1] - tx_pos[1]
    dz = rx_pos[2] - tx_pos[2]
    
    los_dist = math.sqrt(dx**2 + dy**2 + dz**2)
    dist_2d = math.sqrt(dx**2 + dy**2)
    
    los_dep_az = math.degrees(math.atan2(dy, dx)) % 360.0
    los_dep_el = math.degrees(math.atan2(dz, dist_2d))
    
    los_arr_az = (los_dep_az + 180) % 360.0
    los_arr_el = -los_dep_el
    
    c = 299792458.0
    def calc_fspl(d, f_ghz):
        if d <= 0: return 0.0
        return 20 * math.log10(d) + 20 * math.log10(f_ghz * 1e9) + 20 * math.log10(4 * math.pi / c)
    
    paths = []
    
    # LOS path
    paths.append({
        "path_id": 1,
        "departure_azimuth_deg": los_dep_az,
        "departure_elevation_deg": los_dep_el,
        "arrival_azimuth_deg": los_arr_az,
        "arrival_elevation_deg": los_arr_el,
        "path_loss_db": calc_fspl(los_dist, frequency_ghz),
        "delay_ns": (los_dist / c) * 1e9,
        "path_type": "LOS"
    })
    
    # NLOS paths
    for i in range(2, num_paths + 1):
        if environment == "urban":
            d_multiplier = random.uniform(1.1, 2.5)
        elif environment == "suburban":
            d_multiplier = random.uniform(1.2, 3.0)
        else: # rural
            d_multiplier = random.uniform(1.05, 1.5)
            
        nlos_dist = los_dist * d_multiplier
        nlos_dep_az = (los_dep_az + random.uniform(-30, 30)) % 360.0
        nlos_dep_el = max(-90.0, min(90.0, los_dep_el + random.uniform(-15, 15)))
        nlos_arr_az = (los_arr_az + random.uniform(-30, 30)) % 360.0
        nlos_arr_el = max(-90.0, min(90.0, los_arr_el + random.uniform(-15, 15)))
        
        # Adding environment specific extra loss
        env_loss = 0
        if environment == "urban": env_loss = random.uniform(10, 25)
        elif environment == "suburban": env_loss = random.uniform(5, 15)
        else: env_loss = random.uniform(2, 8)
        
        paths.append({
            "path_id": i,
            "departure_azimuth_deg": nlos_dep_az,
            "departure_elevation_deg": nlos_dep_el,
            "arrival_azimuth_deg": nlos_arr_az,
            "arrival_elevation_deg": nlos_arr_el,
            "path_loss_db": calc_fspl(nlos_dist, frequency_ghz) + env_loss,
            "delay_ns": (nlos_dist / c) * 1e9,
            "path_type": "NLOS"
        })
        
    arr_azs = [p["arrival_azimuth_deg"] for p in paths]
    mean_arr_az = sum(arr_azs) / len(arr_azs)
    
    # Handle circular variance for angles accurately or just use simple standard deviation
    # We will use simple standard deviation as approximation
    variance = sum((x - mean_arr_az)**2 for x in arr_azs) / len(arr_azs)
    angular_spread = math.sqrt(variance)
    
    dep_azs = [p["departure_azimuth_deg"] for p in paths]
    mean_dep_az = sum(dep_azs) / len(dep_azs)
    
    return {
        "paths": paths,
        "tx_position": tx_pos,
        "rx_position": rx_pos,
        "los_distance_m": los_dist,
        "summary": {
            "angular_spread_deg": angular_spread,
            "mean_departure_azimuth": mean_dep_az,
            "mean_arrival_azimuth": mean_arr_az,
            "num_los_paths": 1,
            "num_nlos_paths": num_paths - 1
        }
    }
