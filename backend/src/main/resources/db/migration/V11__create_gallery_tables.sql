CREATE TABLE gallery_items (
  id BIGSERIAL PRIMARY KEY,
  simulation_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  title VARCHAR(200) NOT NULL,
  description VARCHAR(500),
  visibility VARCHAR(20) DEFAULT 'PUBLIC',
  view_count BIGINT DEFAULT 0,
  fork_count BIGINT DEFAULT 0,
  download_count BIGINT DEFAULT 0,
  custom_tags TEXT,
  published_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE gallery_comments (
  id BIGSERIAL PRIMARY KEY,
  gallery_item_id BIGINT NOT NULL REFERENCES gallery_items(id) ON DELETE CASCADE,
  user_id BIGINT NOT NULL,
  author_name VARCHAR(100),
  content VARCHAR(1000) NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_gallery_visibility ON gallery_items(visibility);
CREATE INDEX idx_gallery_user ON gallery_items(user_id);
CREATE INDEX idx_gallery_published ON gallery_items(published_at DESC);
CREATE INDEX idx_comments_gallery ON gallery_comments(gallery_item_id);
