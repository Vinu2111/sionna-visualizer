CREATE TABLE nl_parse_records (
  id BIGSERIAL PRIMARY KEY,
  user_id BIGINT,
  query_text VARCHAR(500) NOT NULL,
  extracted_params_json TEXT,
  confidence VARCHAR(10),
  simulation_type VARCHAR(30),
  parsed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_nl_parse_user ON nl_parse_records(user_id);
CREATE INDEX idx_nl_parse_time ON nl_parse_records(parsed_at DESC);
