import time
import math
import random
import importlib.metadata
import numpy as np
from models import (
    UeTrajectoryRequest,
    UeTrajectoryResult,
    Waypoint,
    UeTrajectorySummary,
    PerformanceMetadata
)

def _get_coverage_radius(env: str) -> float:
    if env == "urban":
        return 400.0
    elif env == "suburban":
        return 800.0
    else:
        return 2000.0

def _get_shadowing_std(env: str) -> float:
    if env == "urban":
        return 8.0
    elif env == "suburban":
        return 5.0
    else:
        return 3.0

def _calculate_fspl(distance: float, frequency_ghz: float) -> float:
    """Free Space Path Loss."""
    if distance < 1.0:
        distance = 1.0
    # FSPL = 20*log10(d) + 20*log10(f) + 32.44
    return 20 * math.log10(distance) + 20 * math.log10(frequency_ghz * 1000.0) + 32.44

def simulate_ue_trajectory(request: UeTrajectoryRequest) -> UeTrajectoryResult:
    start_time = time.time()
    
    tx_x, tx_y, tx_h = request.tx_position
    
    # 1. Generate Waypoints based on trajectory_type
    coverage_radius = _get_coverage_radius(request.environment)
    points = []
    
    if request.trajectory_type == "linear":
        # Random start angle, end at opposite side
        angle = random.uniform(0, 2 * math.pi)
        start_x = tx_x + coverage_radius * math.cos(angle)
        start_y = tx_y + coverage_radius * math.sin(angle)
        end_x = tx_x - coverage_radius * math.cos(angle)
        end_y = tx_y - coverage_radius * math.sin(angle)
        
        for i in range(request.num_waypoints):
            t = i / max(1, request.num_waypoints - 1)
            x = start_x + t * (end_x - start_x)
            y = start_y + t * (end_y - start_y)
            # Add small random deviation for intermediate points (±10m)
            if 0 < i < request.num_waypoints - 1:
                x += random.uniform(-10, 10)
                y += random.uniform(-10, 10)
            points.append([x, y])
            
    elif request.trajectory_type == "circular":
        # Radius depends on environment: urban=150, suburban=200, rural=300
        radius = 150.0 if request.environment == "urban" else (200.0 if request.environment == "suburban" else 300.0)
        start_angle = random.uniform(0, 2 * math.pi)
        for i in range(request.num_waypoints):
            # Distributed evenly around a circle
            angle = start_angle + (i * 2 * math.pi) / request.num_waypoints
            x = tx_x + radius * math.cos(angle)
            y = tx_y + radius * math.sin(angle)
            points.append([x, y])
            
    else: # "random"
        for _ in range(request.num_waypoints):
            min_dist = 50.0 if request.environment == "urban" else (100.0 if request.environment == "suburban" else 200.0)
            d = random.uniform(min_dist, coverage_radius)
            angle = random.uniform(0, 2 * math.pi)
            x = tx_x + d * math.cos(angle)
            y = tx_y + d * math.sin(angle)
            points.append([x, y])
            
    # 2. Compute metrics for each waypoint
    waypoints = []
    cumulative_time = 0.0
    cumulative_distance = 0.0
    shadowing_std = _get_shadowing_std(request.environment)
    speed_ms = request.speed_kmh / 3.6
    tx_power_dbm = 30.0
    
    for i in range(len(points)):
        x, y = points[i]
        
        # Distance to TX
        dist_3d = math.sqrt((x - tx_x)**2 + (y - tx_y)**2 + tx_h**2)
        
        # Path Loss + Shadowing
        fspl = _calculate_fspl(dist_3d, request.frequency_ghz)
        shadowing = random.gauss(0, shadowing_std)
        signal_dbm = tx_power_dbm - fspl + shadowing
        
        handover_required = signal_dbm < -85.0
        
        velocity_vector = [0.0, 0.0]
        if i < len(points) - 1:
            nx, ny = points[i+1]
            seg_dist = math.sqrt((nx - x)**2 + (ny - y)**2)
            if seg_dist > 0:
                velocity_vector = [(nx - x)/seg_dist, (ny - y)/seg_dist]
                cumulative_distance += seg_dist
                cumulative_time += seg_dist / speed_ms
        else:
            velocity_vector = [0.0, 0.0]
            
        waypoints.append(Waypoint(
            position=[x, y],
            distance_m=dist_3d,
            signal_dbm=signal_dbm,
            handover_required=handover_required,
            time_s=cumulative_time if i < len(points) - 1 else cumulative_time,
            velocity_vector=velocity_vector
        ))
        
    # Re-adjust time since the time computed in loop was for *end* of segment. Actually, time at point 0 should be 0.
    # Let's fix loop logic for time:
    time_s = 0.0
    total_distance_m = 0.0
    for i in range(len(points)):
        waypoints[i].time_s = time_s
        if i < len(points) - 1:
            dist = math.sqrt((points[i+1][0] - points[i][0])**2 + (points[i+1][1] - points[i][1])**2)
            total_distance_m += dist
            time_s += dist / speed_ms

    # 3. Trajectory Summary
    min_signal = min(w.signal_dbm for w in waypoints)
    max_signal = max(w.signal_dbm for w in waypoints)
    handovers = sum(1 for w in waypoints if w.handover_required)
    coverage_above_90 = sum(1 for w in waypoints if w.signal_dbm >= -90.0)
    coverage_percent = (coverage_above_90 / len(waypoints)) * 100.0
    
    summary = UeTrajectorySummary(
        total_distance_m=total_distance_m,
        total_time_s=total_distance_m / speed_ms if speed_ms > 0 else 0.0,
        min_signal_dbm=min_signal,
        max_signal_dbm=max_signal,
        handover_count=handovers,
        coverage_percent=coverage_percent
    )
    
    duration_ms = int((time.time() - start_time) * 1000)
    performance = PerformanceMetadata(
        duration_ms=duration_ms,
        compute_type="CPU",
        memory_mb=1.2,
        sionna_version=importlib.metadata.version("sionna") if importlib.metadata.packages_distributions().get("sionna") else "N/A"
    )

    return UeTrajectoryResult(
        waypoints=waypoints,
        tx_position=request.tx_position,
        trajectory_type=request.trajectory_type,
        summary=summary,
        performance=performance
    )
