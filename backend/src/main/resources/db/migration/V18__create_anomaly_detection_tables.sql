CREATE TABLE anomaly_reports (
  id BIGSERIAL PRIMARY KEY,
  simulation_id BIGINT NOT NULL,
  user_id BIGINT,
  total_anomalies INTEGER DEFAULT 0,
  has_critical BOOLEAN DEFAULT false,
  overall_status VARCHAR(20) DEFAULT 'CLEAR',
  analyzed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE anomaly_records (
  id BIGSERIAL PRIMARY KEY,
  report_id BIGINT NOT NULL
    REFERENCES anomaly_reports(id) ON DELETE CASCADE,
  simulation_id BIGINT NOT NULL,
  anomaly_type VARCHAR(50),
  severity VARCHAR(20),
  title VARCHAR(200),
  description VARCHAR(1000),
  affected_snr_point DOUBLE PRECISION,
  affected_ber_value DOUBLE PRECISION,
  likely_cause VARCHAR(500),
  suggested_fix VARCHAR(500),
  ai_explanation TEXT,
  ai_explained_at TIMESTAMP,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_anomaly_report_simulation ON anomaly_reports(simulation_id);
CREATE INDEX idx_anomaly_report_user ON anomaly_reports(user_id);
CREATE INDEX idx_anomaly_record_report ON anomaly_records(report_id);
CREATE INDEX idx_anomaly_record_severity ON anomaly_records(severity);
