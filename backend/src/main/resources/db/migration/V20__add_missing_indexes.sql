CREATE UNIQUE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_simulation_tags_sim_tag ON simulation_tags(simulation_id, tag);
CREATE INDEX IF NOT EXISTS idx_gallery_items_visibility_published_at ON gallery_items(visibility, published_at);
CREATE INDEX IF NOT EXISTS idx_workspace_members_workspace_user ON workspace_members(workspace_id, user_id);
