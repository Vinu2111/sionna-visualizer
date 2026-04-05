CREATE TABLE alliance_organizations (
  id BIGSERIAL PRIMARY KEY,
  user_id BIGINT UNIQUE NOT NULL,
  org_name VARCHAR(300),
  member_type VARCHAR(50),
  alliance_track VARCHAR(100),
  member_id VARCHAR(100),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE bharat_pocs (
  id BIGSERIAL PRIMARY KEY,
  user_id BIGINT NOT NULL,
  org_id BIGINT REFERENCES alliance_organizations(id) ON DELETE SET NULL,
  title VARCHAR(300) NOT NULL,
  description TEXT,
  target_use_case VARCHAR(500),
  alliance_track VARCHAR(100),
  current_trl INTEGER DEFAULT 1,
  expected_completion_trl INTEGER,
  status VARCHAR(30) DEFAULT 'ACTIVE',
  target_completion_date DATE,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE poc_trl_history (
  id BIGSERIAL PRIMARY KEY,
  poc_id BIGINT NOT NULL REFERENCES bharat_pocs(id) ON DELETE CASCADE,
  trl_level INTEGER NOT NULL,
  achieved_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  linked_simulation_id BIGINT,
  evidence_description VARCHAR(1000)
);

CREATE TABLE poc_simulation_links (
  id BIGSERIAL PRIMARY KEY,
  poc_id BIGINT NOT NULL REFERENCES bharat_pocs(id) ON DELETE CASCADE,
  simulation_id BIGINT NOT NULL,
  trl_evidence_for INTEGER,
  linked_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE alliance_kpi_targets (
  id BIGSERIAL PRIMARY KEY,
  poc_id BIGINT NOT NULL REFERENCES bharat_pocs(id) ON DELETE CASCADE,
  kpi_name VARCHAR(200),
  target_value DOUBLE PRECISION,
  actual_value DOUBLE PRECISION,
  unit VARCHAR(50),
  alliance_track VARCHAR(100),
  status VARCHAR(20) DEFAULT 'PENDING'
);

CREATE TABLE poc_quarterly_status (
  id BIGSERIAL PRIMARY KEY,
  poc_id BIGINT NOT NULL REFERENCES bharat_pocs(id) ON DELETE CASCADE,
  quarter VARCHAR(5) NOT NULL,
  year INTEGER NOT NULL,
  status VARCHAR(20) DEFAULT 'NOT_DUE',
  due_date DATE,
  submitted_at TIMESTAMP,
  UNIQUE(poc_id, quarter, year)
);

CREATE INDEX idx_alliance_org_user ON alliance_organizations(user_id);
CREATE INDEX idx_bharat_poc_user ON bharat_pocs(user_id);
CREATE INDEX idx_bharat_poc_track ON bharat_pocs(alliance_track);
CREATE INDEX idx_poc_trl_poc ON poc_trl_history(poc_id);
CREATE INDEX idx_poc_sim_links_poc ON poc_simulation_links(poc_id);
CREATE INDEX idx_alliance_kpi_poc ON alliance_kpi_targets(poc_id);
CREATE INDEX idx_quarterly_poc ON poc_quarterly_status(poc_id);
