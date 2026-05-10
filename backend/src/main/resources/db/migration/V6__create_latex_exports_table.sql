CREATE TABLE latex_exports (
  id BIGSERIAL PRIMARY KEY,
  simulation_id BIGINT NOT NULL,
  table_caption VARCHAR(200),
  table_label VARCHAR(100),
  generated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
