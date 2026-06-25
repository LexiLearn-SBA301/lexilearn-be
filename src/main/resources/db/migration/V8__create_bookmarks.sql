CREATE TABLE bookmarks (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    account_id          UUID NOT NULL,
    work_id             UUID NOT NULL,
    current_section_id  UUID,
    position            INTEGER NOT NULL DEFAULT 0,
    progress_percent    NUMERIC(5,2) NOT NULL DEFAULT 0,
    is_completed        BOOLEAN NOT NULL DEFAULT FALSE,
    completed_at        TIMESTAMPTZ,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_bookmarks_account
        FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE,
    CONSTRAINT fk_bookmarks_work
        FOREIGN KEY (work_id) REFERENCES works(id) ON DELETE CASCADE,
    CONSTRAINT fk_bookmarks_section
        FOREIGN KEY (current_section_id) REFERENCES work_sections(id) ON DELETE SET NULL,
    CONSTRAINT uq_bookmarks_account_work
        UNIQUE (account_id, work_id),
    CONSTRAINT chk_bookmarks_position
        CHECK (position >= 0),
    CONSTRAINT chk_bookmarks_progress
        CHECK (progress_percent BETWEEN 0 AND 100),
    CONSTRAINT chk_bookmarks_completion
        CHECK (
            (is_completed = FALSE AND completed_at IS NULL) OR
            (is_completed = TRUE AND completed_at IS NOT NULL)
        )
);

CREATE INDEX idx_bookmarks_account_recent
    ON bookmarks(account_id, updated_at DESC);

CREATE INDEX idx_bookmarks_work
    ON bookmarks(work_id);

CREATE TRIGGER tr_bookmarks_updated_at
    BEFORE UPDATE ON bookmarks
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
