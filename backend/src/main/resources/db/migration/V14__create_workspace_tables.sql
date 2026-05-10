CREATE TABLE workspaces (
  id BIGSERIAL PRIMARY KEY,
  name VARCHAR(200) NOT NULL,
  description VARCHAR(500),
  institution VARCHAR(200),
  owner_id BIGINT NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE workspace_members (
  id BIGSERIAL PRIMARY KEY,
  workspace_id BIGINT NOT NULL REFERENCES workspaces(id) ON DELETE CASCADE,
  user_id BIGINT NOT NULL,
  role VARCHAR(20) NOT NULL DEFAULT 'MEMBER',
  joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  UNIQUE(workspace_id, user_id)
);

CREATE TABLE simulation_comments (
  id BIGSERIAL PRIMARY KEY,
  simulation_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  author_name VARCHAR(100),
  content TEXT NOT NULL,
  parent_comment_id BIGINT REFERENCES simulation_comments(id) ON DELETE CASCADE,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE simulation_annotations (
  id BIGSERIAL PRIMARY KEY,
  simulation_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  author_name VARCHAR(100),
  snr_point DOUBLE PRECISION,
  ber_point DOUBLE PRECISION,
  annotation_text VARCHAR(500) NOT NULL,
  pin_number INTEGER,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE simulation_versions (
  id BIGSERIAL PRIMARY KEY,
  simulation_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  created_by_name VARCHAR(100),
  version_number INTEGER NOT NULL,
  parameters_snapshot TEXT NOT NULL,
  changed_fields TEXT,
  is_restore BOOLEAN DEFAULT false,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_workspace_owner ON workspaces(owner_id);
CREATE INDEX idx_workspace_members_workspace ON workspace_members(workspace_id);
CREATE INDEX idx_workspace_members_user ON workspace_members(user_id);
CREATE INDEX idx_comments_simulation ON simulation_comments(simulation_id);
CREATE INDEX idx_annotations_simulation ON simulation_annotations(simulation_id);
CREATE INDEX idx_versions_simulation ON simulation_versions(simulation_id);
