CREATE TABLE figure_exports (
  id BIGSERIAL PRIMARY KEY,
  simulation_id BIGINT NOT NULL,
  journal_style VARCHAR(20) NOT NULL,
  export_format VARCHAR(10) NOT NULL,
  chart_type VARCHAR(50),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
