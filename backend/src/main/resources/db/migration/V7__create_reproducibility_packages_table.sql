CREATE TABLE reproducibility_packages (
  id BIGSERIAL PRIMARY KEY,
  simulation_id BIGINT NOT NULL,
  include_ber_data BOOLEAN DEFAULT true,
  include_beam_data BOOLEAN DEFAULT true,
  anonymized BOOLEAN DEFAULT false,
  generated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
