CREATE TABLE thz_scenarios (
  id BIGSERIAL PRIMARY KEY,
  user_id BIGINT NOT NULL,
  name VARCHAR(200) NOT NULL,
  frequency_ghz DOUBLE PRECISION,
  humidity_percent DOUBLE PRECISION,
  temperature_celsius DOUBLE PRECISION,
  pressure_hpa DOUBLE PRECISION,
  rain_rate_mm_per_hr DOUBLE PRECISION,
  link_distance_meters DOUBLE PRECISION,
  tx_power_dbm DOUBLE PRECISION,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_thz_scenarios_user
ON thz_scenarios(user_id);
