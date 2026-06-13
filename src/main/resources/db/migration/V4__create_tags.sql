-- V4__create_tags.sql

CREATE TABLE tags (
                      id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                      name            VARCHAR(100) NOT NULL UNIQUE,
                      slug            VARCHAR(100) NOT NULL UNIQUE,
                      description     TEXT,
                      created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                      updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE work_tags (
                           work_id         UUID NOT NULL,
                           tag_id          UUID NOT NULL,
                           created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                           PRIMARY KEY (work_id, tag_id),
                           CONSTRAINT fk_work_tags_work
                               FOREIGN KEY (work_id) REFERENCES works(id) ON DELETE CASCADE,
                           CONSTRAINT fk_work_tags_tag
                               FOREIGN KEY (tag_id) REFERENCES tags(id) ON DELETE CASCADE
);

CREATE INDEX idx_work_tags_tag ON work_tags(tag_id);

CREATE TRIGGER tr_tags_updated_at BEFORE UPDATE ON tags FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();