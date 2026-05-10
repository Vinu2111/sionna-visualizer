CREATE TABLE experiments (
  id BIGSERIAL PRIMARY KEY,
  user_id BIGINT NOT NULL,
  name VARCHAR(200) NOT NULL,
  description VARCHAR(500),
  color VARCHAR(7) DEFAULT '#1976d2',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE simulation_tags (
  id BIGSERIAL PRIMARY KEY,
  simulation_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  tag VARCHAR(100) NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  UNIQUE(simulation_id, tag)
);

CREATE INDEX idx_experiment_user 
ON experiments(user_id);

CREATE INDEX idx_tags_simulation 
ON simulation_tags(simulation_id);

CREATE INDEX idx_tags_user_tag 
ON simulation_tags(user_id, tag);
