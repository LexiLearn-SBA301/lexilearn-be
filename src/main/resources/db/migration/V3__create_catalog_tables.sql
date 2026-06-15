-- V3__create_catalog_tables.sql
-- Dán đoạn này vào TRƯỚC các câu lệnh CREATE TRIGGER
CREATE OR REPLACE FUNCTION update_updated_at_column()
    RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;
CREATE TABLE authors (
                         id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                         name            VARCHAR(200) NOT NULL,
                         pen_name        VARCHAR(200),
                         slug            VARCHAR(200) NOT NULL UNIQUE,
                         birth_year      INT,
                         death_year      INT,
                         period          VARCHAR(30) NOT NULL,
                         bio             TEXT,
                         portrait_url    VARCHAR(500),
                         created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                         updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                         CONSTRAINT chk_authors_period
                             CHECK (period IN ('dan_gian', 'trung_dai', 'hien_dai')),
                         CONSTRAINT chk_authors_years
                             CHECK (birth_year IS NULL OR death_year IS NULL OR death_year >= birth_year)
);

CREATE TABLE works (
                       id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                       author_id           UUID,
                       title               VARCHAR(300) NOT NULL,
                       slug                VARCHAR(300) NOT NULL UNIQUE,
                       original_title      VARCHAR(300),
                       genre               VARCHAR(50) NOT NULL,
                       sub_genre           VARCHAR(50),
                       period              VARCHAR(30) NOT NULL,
                       grade               SMALLINT,
                       semester            SMALLINT,
                       publish_year        INT,
                       summary             TEXT,
                       cover_url           VARCHAR(500),
                       is_published        BOOLEAN NOT NULL DEFAULT FALSE,
                       view_count          BIGINT NOT NULL DEFAULT 0,
                       historical_context  TEXT,
                       realistic_value     TEXT,
                       humanistic_value    TEXT,
                       artistic_value      TEXT,
                       famous_quote        TEXT,
                       quote_attribution   VARCHAR(300),
                       created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                       updated_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                       CONSTRAINT fk_works_author
                           FOREIGN KEY (author_id) REFERENCES authors(id) ON DELETE SET NULL,
                       CONSTRAINT chk_works_period
                           CHECK (period IN ('dan_gian', 'trung_dai', 'hien_dai')),
                       CONSTRAINT chk_works_grade
                           CHECK (grade IS NULL OR grade IN (10, 11, 12)),
                       CONSTRAINT chk_works_semester
                           CHECK (semester IS NULL OR semester IN (1, 2)),
                       CONSTRAINT chk_works_view_count CHECK (view_count >= 0)
);

CREATE INDEX idx_works_author ON works(author_id);
CREATE INDEX idx_works_library_filter ON works(grade, semester, genre, period) WHERE is_published = TRUE;
CREATE INDEX idx_authors_period ON authors(period);

CREATE TRIGGER tr_authors_updated_at BEFORE UPDATE ON authors FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER tr_works_updated_at BEFORE UPDATE ON works FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();