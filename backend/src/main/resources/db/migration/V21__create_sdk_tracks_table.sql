CREATE TABLE IF NOT EXISTS sdk_tracks (
  id BIGSERIAL PRIMARY KEY,
  api_key_id BIGINT,
  simulation_id BIGINT,
  sdk_version VARCHAR(20),
  sdk_language VARCHAR(20) DEFAULT 'python',
  simulation_type VARCHAR(50),
  title VARCHAR(300),
  tags TEXT,
  tracked_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_sdk_tracks_api_key
ON sdk_tracks(api_key_id);

CREATE INDEX IF NOT EXISTS idx_sdk_tracks_simulation
ON sdk_tracks(simulation_id);

ALTER TABLE simulation_results
ADD COLUMN IF NOT EXISTS tracked_via_sdk BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE simulation_results
ADD COLUMN IF NOT EXISTS sdk_version VARCHAR(20);

ALTER TABLE simulation_results
ADD COLUMN IF NOT EXISTS sdk_language VARCHAR(20);
