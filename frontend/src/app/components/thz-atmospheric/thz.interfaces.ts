export interface ThzParams {
  frequencyGhz: number;
  humidityPercent: number;
  temperatureCelsius: number;
  pressureHpa: number;
  rainRateMmPerHr: number;
  linkDistanceMeters: number;
  txPowerDbm: number;
}

export interface SpectrumPoint {
  frequencyGhz: number;
  totalAbsorptionDbPerKm: number;
  h2oAbsorptionDbPerKm: number;
  o2AbsorptionDbPerKm: number;
}

export interface ThzResult {
  molecular_absorption_db_per_km: number;
  rain_attenuation_db_per_km: number;
  free_space_path_loss_db: number;
  total_path_loss_db: number;
  received_power_dbm: number;
  ber_at_distances: number[];
  distance_range_meters: number[];
  absorption_spectrum: SpectrumPoint[];
  max_viable_range_meters: number;
}

export interface LinkBudget {
  txPowerDbm: number;
  txAntennaGainDbi: number;
  freeSpacePathLossDb: number;
  molecularAbsorptionDb: number;
  rainAttenuationDb: number;
  rxAntennaGainDbi: number;
  receivedPowerDbm: number;
}

export interface ThzScenario {
  scenarioId: number;
  name: string;
  params: ThzParams;
  createdAt: string;
}
