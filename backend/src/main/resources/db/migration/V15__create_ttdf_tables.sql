CREATE TABLE ttdf_projects (
  id BIGSERIAL PRIMARY KEY,
  user_id BIGINT UNIQUE NOT NULL,
  title VARCHAR(300),
  ttdf_grant_id VARCHAR(100),
  pi_name VARCHAR(200),
  institution VARCHAR(300),
  grant_amount_lakhs DOUBLE PRECISION,
  start_date DATE,
  end_date DATE,
  current_trl INTEGER DEFAULT 1,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE ttdf_milestones (
  id BIGSERIAL PRIMARY KEY,
  project_id BIGINT NOT NULL REFERENCES ttdf_projects(id) ON DELETE CASCADE,
  title VARCHAR(300) NOT NULL,
  description VARCHAR(1000),
  month_number INTEGER,
  due_date DATE,
  status VARCHAR(20) DEFAULT 'UPCOMING',
  linked_simulation_id BIGINT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE ttdf_kpi_targets (
  id BIGSERIAL PRIMARY KEY,
  milestone_id BIGINT NOT NULL REFERENCES ttdf_milestones(id) ON DELETE CASCADE,
  kpi_name VARCHAR(200),
  target_value DOUBLE PRECISION,
  actual_value DOUBLE PRECISION,
  unit VARCHAR(50),
  metric_type VARCHAR(20) DEFAULT 'CUSTOM',
  status VARCHAR(20) DEFAULT 'PENDING'
);

CREATE INDEX idx_ttdf_project_user ON ttdf_projects(user_id);
CREATE INDEX idx_ttdf_milestones_project ON ttdf_milestones(project_id);
CREATE INDEX idx_ttdf_milestones_status ON ttdf_milestones(status);
CREATE INDEX idx_ttdf_kpi_milestone ON ttdf_kpi_targets(milestone_id);
