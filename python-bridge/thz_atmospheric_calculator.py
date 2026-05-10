from pydantic import BaseModel
import numpy as np
from scipy.special import erfc
from typing import List, Dict

# The core models for atmospheric simulation are based on ITU-R P.676 and ITU-R P.838.
# ITU-R P.676 defines attenuation by atmospheric gases (oxygen and water vapor). 
# ITU-R P.838 defines specific attenuation model for rain. 
# 
# In THz systems these factors drastically reduce viable ranges and create "absorption windows."
# Frequencies like 300 GHz, 350 GHz, and 410 GHz are greatly preferred for THz 6G links 
# because they sit perfectly between the aggressive water vapor and oxygen absorption peaks. 
# Operating exactly on a peak (like 60 GHz O2 or 380 GHz H2O) causes catastrophic signal death.

class ThzAtmosphericRequest(BaseModel):
    frequency_ghz: float
    humidity_percent: float
    temperature_celsius: float
    pressure_hpa: float
    rain_rate_mm_per_hr: float
    link_distance_meters: float
    tx_power_dbm: float

class ThzAtmosphericResponse(BaseModel):
    molecular_absorption_db_per_km: float
    rain_attenuation_db_per_km: float
    free_space_path_loss_db: float
    total_path_loss_db: float
    received_power_dbm: float
    ber_at_distances: List[float]
    distance_range_meters: List[float]
    absorption_spectrum: List[Dict[str, float]]
    max_viable_range_meters: float

def calculate_water_vapor_density(temperature_celsius: float, humidity_percent: float) -> float:
    # Uses the Magnus formula to define saturation vapor pressure, then convert to absolute water density
    temp_k = temperature_celsius + 273.15
    # calculate saturation pressure in hPa
    sat_pressure = 6.1078 * np.exp(17.27 * temperature_celsius / (temperature_celsius + 237.3))
    vapor_pressure = (humidity_percent / 100.0) * sat_pressure
    
    # Water vapor density (g/m3)
    water_vapor_density = 216.7 * vapor_pressure / temp_k
    return water_vapor_density

def calculate_h2o_absorption(f: float, rho: float, pressure_pa: float, temp_k: float) -> float:
    # Note: f is frequency in GHz, rho is water vapor density (g/m^3)
    # Simplified empirical model approximating ITU-R P.676 behavior for water vapor.
    # THz is extremely sensitive to water, this creates high absorption areas.
    e = 1.0  # Normalized factor placeholder
    
    # Simple line-by-line approximation emphasizing major spikes directly
    lines = [22.235, 183.310, 325.153, 380.197, 448.001, 556.936]
    
    absorption_h2o = 0.0
    # Add a broad background continuum
    absorption_h2o += (f / 100.0)**2 * rho * 0.01 
    
    # Specific spikes
    for line in lines:
        width = 2.0 * (pressure_pa/101300.0)
        # Lorentz-like formula for resonance
        influence = (width**2) / ((f - line)**2 + width**2)
        strength = rho * 0.2 * (line/100.0)
        absorption_h2o += strength * influence

    return absorption_h2o

def calculate_o2_absorption(f: float) -> float:
    # Oxygen absorbs heavily at ~60 GHz and ~119 GHz.
    # THz links strictly avoid these frequency bins.
    
    gamma_o2 = 7.19e-3 + 6.09 / (f**2 + 0.227) + 4.81 / ((f - 57)**2 + 1.50) + 10.0 / ((f - 119)**2 + 1.5)
    
    return gamma_o2

def calculate_rain_attenuation(f: float, rain_rate: float) -> float:
    if rain_rate == 0:
        return 0.0
    
    # Simplified interpolation of ITU-R P.838 coefficients
    if f <= 100:
        a = 0.1217; b = 0.7957
    elif f <= 200:
        a = np.interp(f, [100, 200], [0.1217, 0.3450])
        b = np.interp(f, [100, 200], [0.7957, 0.7133])
    elif f <= 300:
        a = np.interp(f, [200, 300], [0.3450, 0.5765])
        b = np.interp(f, [200, 300], [0.7133, 0.6772])
    elif f <= 400:
        a = np.interp(f, [300, 400], [0.5765, 0.7803])
        b = np.interp(f, [300, 400], [0.6772, 0.6600])
    elif f <= 500:
        a = np.interp(f, [400, 500], [0.7803, 0.9675])
        b = np.interp(f, [400, 500], [0.6600, 0.6450])
    else:
        # Above 500 GHz cap coefficients
        a = 1.0; b = 0.64
        
    return a * (rain_rate ** b)

def calculate_total_absorption(f_ghz: float, temp_c: float, humidity: float, pressure_hpa: float):
    rho = calculate_water_vapor_density(temp_c, humidity)
    
    h2o = calculate_h2o_absorption(f_ghz, rho, pressure_hpa * 100, temp_c + 273.15)
    o2 = calculate_o2_absorption(f_ghz)
    
    return h2o, o2, h2o + o2

def calculate_fspl(d_meters: float, f_hz: float) -> float:
    # FSPL formula variables:
    # c = speed of light
    # d_meters = distance between TX and RX antennas in meters
    # f_hz = frequency of the signal in Hertz
    c = 3e8 # m/s
    if d_meters == 0:
        return 0.0
    return 20 * np.log10(4 * np.pi * d_meters * f_hz / c)

def ber_from_snr(snr_db: float) -> float:
    # QPSK error calculation using complementary error function.
    # What is the complementary error function (erfc)?
    # In simple words, it mathematically describes the probability that the random 
    # thermal noise in the air gets strong enough to bump the transmitted signal 
    # across the boundary line into the wrong quadrant on the receiver side.
    snr_linear = 10 ** (snr_db / 10.0)
    return 0.5 * erfc(np.sqrt(snr_linear / 2.0))

def get_received_power_dbm(d_meters: float, f_hz: float, tx_power: float, mol_db_km: float, rain_db_km: float) -> float:
    fspl = calculate_fspl(d_meters, f_hz)
    dist_km = d_meters / 1000.0
    atmosphere = mol_db_km * dist_km
    rain = rain_db_km * dist_km
    total_loss = fspl + atmosphere + rain
    
    # Typical high gain antennas for THz required due to extreme losses
    tx_antenna_gain_dbi = 10.0
    rx_antenna_gain_dbi = 10.0
    return tx_power + tx_antenna_gain_dbi - total_loss + rx_antenna_gain_dbi

def handle_thz_calculate(req: ThzAtmosphericRequest) -> ThzAtmosphericResponse:
    frequency_hz = req.frequency_ghz * 1e9
    
    h2o_abs, o2_abs, total_mol_abs = calculate_total_absorption(
        req.frequency_ghz, req.temperature_celsius, req.humidity_percent, req.pressure_hpa
    )
    
    rain_attenuation = calculate_rain_attenuation(req.frequency_ghz, req.rain_rate_mm_per_hr)
    
    fspl_current = calculate_fspl(req.link_distance_meters, frequency_hz)
    dist_km = req.link_distance_meters / 1000.0
    total_path_loss = fspl_current + (total_mol_abs * dist_km) + (rain_attenuation * dist_km)
    
    received_power = req.tx_power_dbm + 10.0 - total_path_loss + 10.0
    
    # 100 points logarithmic spacing for distance
    distance_range = np.logspace(0, 3, 100) # 10^0 to 10^3 = 1m to 1000m
    
    # Noise calculation -> SNR -> BER
    bandwidth_hz = 10e9
    noise_figure_db = 10.0
    noise_power_dbm = -174.0 + 10 * np.log10(bandwidth_hz) + noise_figure_db
    
    ber_values = []
    max_viable_range = 0.0
    found_limit = False
    
    for d in distance_range:
        pr = get_received_power_dbm(d, frequency_hz, req.tx_power_dbm, total_mol_abs, rain_attenuation)
        snr = pr - noise_power_dbm
        ber = ber_from_snr(snr)
        ber_values.append(ber)
        
        # Crosses 0.001?
        if ber > 0.001 and not found_limit:
            max_viable_range = float(d)
            found_limit = True

    if not found_limit:
        max_viable_range = 1000.0 # Exceeds our graph
        
    # Spectrum array (50 to 1000 GHz, 200 points)
    freqs = np.linspace(50, 1000, 200)
    spectrum = []
    for f in freqs:
        f_h2o, f_o2, f_total = calculate_total_absorption(f, req.temperature_celsius, req.humidity_percent, req.pressure_hpa)
        spectrum.append({
            "frequencyGhz": float(f),
            "totalAbsorptionDbPerKm": float(f_total),
            "h2oAbsorptionDbPerKm": float(f_h2o),
            "o2AbsorptionDbPerKm": float(f_o2)
        })
        
    return ThzAtmosphericResponse(
        molecular_absorption_db_per_km=total_mol_abs,
        rain_attenuation_db_per_km=rain_attenuation,
        free_space_path_loss_db=fspl_current,
        total_path_loss_db=total_path_loss,
        received_power_dbm=received_power,
        ber_at_distances=[float(x) for x in ber_values],
        distance_range_meters=[float(x) for x in distance_range],
        absorption_spectrum=spectrum,
        max_viable_range_meters=max_viable_range
    )
