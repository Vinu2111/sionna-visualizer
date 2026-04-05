CREATE TABLE sigmf_imports (
  id BIGSERIAL PRIMARY KEY,
  simulation_id BIGINT NOT NULL,
  center_frequency DOUBLE PRECISION,
  sample_rate DOUBLE PRECISION,
  data_type VARCHAR(20),
  estimated_snr DOUBLE PRECISION,
  ber_match_percentage DOUBLE PRECISION,
  hardware_description VARCHAR(500),
  user_id BIGINT,
  imported_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
