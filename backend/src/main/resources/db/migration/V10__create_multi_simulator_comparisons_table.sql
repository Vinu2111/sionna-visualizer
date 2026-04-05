CREATE TABLE multi_simulator_comparisons (
  id BIGSERIAL PRIMARY KEY,
  sionna_simulation_id BIGINT NOT NULL,
  simulator_type VARCHAR(50) NOT NULL,
  snr_points TEXT,
  sionna_ber TEXT,
  external_ber TEXT,
  sionna_throughput TEXT,
  external_throughput TEXT,
  ber_crossover_snr DOUBLE PRECISION,
  average_ber_difference DOUBLE PRECISION,
  better_performer_at_20db VARCHAR(50),
  matched_data_points INTEGER,
  user_id BIGINT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_multi_sim_user 
ON multi_simulator_comparisons(user_id);

CREATE INDEX idx_multi_sim_sionna 
ON multi_simulator_comparisons(sionna_simulation_id);
