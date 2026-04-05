ALTER TABLE simulation_results 
ADD COLUMN IF NOT EXISTS note TEXT;

ALTER TABLE simulation_results 
ADD COLUMN IF NOT EXISTS starred BOOLEAN DEFAULT false;

ALTER TABLE simulation_results 
ADD COLUMN IF NOT EXISTS experiment_id BIGINT 
  REFERENCES experiments(id) ON DELETE SET NULL;

CREATE INDEX idx_simulations_starred 
ON simulation_results(starred) WHERE starred = true;

CREATE INDEX idx_simulations_experiment 
ON simulation_results(experiment_id);
