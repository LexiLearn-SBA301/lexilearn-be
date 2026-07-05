CREATE TABLE work_commentaries (
    id                UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    work_id           UUID NOT NULL,
    title             VARCHAR(300),
    content           TEXT NOT NULL,
    commentator_name  VARCHAR(200) NOT NULL,
    commentator_type  VARCHAR(30) NOT NULL,
    source_title      VARCHAR(300),
    source_url        VARCHAR(500),
    published_year    INTEGER,
    display_order     INTEGER NOT NULL DEFAULT 0,
    is_featured       BOOLEAN NOT NULL DEFAULT FALSE,
    is_published      BOOLEAN NOT NULL DEFAULT TRUE,
    created_at        TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at        TIMESTAMPTZ,

    CONSTRAINT fk_work_commentaries_work
        FOREIGN KEY (work_id) REFERENCES works(id) ON DELETE CASCADE,
    CONSTRAINT uq_work_commentaries_work_display_order
        UNIQUE (work_id, display_order),
    CONSTRAINT chk_work_commentaries_type
        CHECK (
            commentator_type IN
            ('CRITIC', 'SCHOLAR', 'WRITER', 'TEACHER', 'EDITORIAL', 'READER')
        ),
    CONSTRAINT chk_work_commentaries_order
        CHECK (display_order >= 0),
    CONSTRAINT chk_work_commentaries_published_year
        CHECK (published_year IS NULL OR published_year BETWEEN 0 AND 2100)
);

CREATE INDEX idx_work_commentaries_public_list
    ON work_commentaries(work_id, is_published, display_order);

CREATE TRIGGER tr_work_commentaries_updated_at
    BEFORE UPDATE ON work_commentaries
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
