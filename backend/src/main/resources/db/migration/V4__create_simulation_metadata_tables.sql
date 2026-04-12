CREATE TABLE IF NOT EXISTS simulation_versions (
    id BIGSERIAL PRIMARY KEY,
    simulation_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    created_by_name VARCHAR(100),
    version_number INTEGER NOT NULL,
    parameters_snapshot TEXT NOT NULL,
    changed_fields TEXT,
    is_restore BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS simulation_tags (
    id BIGSERIAL PRIMARY KEY,
    simulation_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    tag VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS simulation_comments (
    id BIGSERIAL PRIMARY KEY,
    simulation_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    author_name VARCHAR(100),
    content TEXT NOT NULL,
    parent_comment_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS simulation_annotations (
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
