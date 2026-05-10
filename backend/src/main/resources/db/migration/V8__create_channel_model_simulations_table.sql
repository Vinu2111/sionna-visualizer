CREATE TABLE channel_model_simulations (
  id BIGSERIAL PRIMARY KEY,
  channel_model VARCHAR(10) NOT NULL,
  modulation VARCHAR(10) NOT NULL,
  snr_min DOUBLE PRECISION,
  snr_max DOUBLE PRECISION,
  carrier_frequency DOUBLE PRECISION,
  delay_spread DOUBLE PRECISION,
  ber_values TEXT,
  delay_profile TEXT,
  simulation_time_seconds DOUBLE PRECISION,
  user_id BIGINT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
