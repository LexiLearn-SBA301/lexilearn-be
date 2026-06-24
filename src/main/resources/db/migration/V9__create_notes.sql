CREATE TABLE notes (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    account_id          UUID NOT NULL,
    section_id          UUID NOT NULL,
    start_offset        INTEGER NOT NULL,
    end_offset          INTEGER NOT NULL,
    highlighted_text    TEXT NOT NULL,
    user_note           TEXT,
    color               VARCHAR(20) NOT NULL DEFAULT 'YELLOW',
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_notes_account
        FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE,
    CONSTRAINT fk_notes_section
        FOREIGN KEY (section_id) REFERENCES work_sections(id) ON DELETE CASCADE,
    CONSTRAINT chk_notes_offsets
        CHECK (start_offset >= 0 AND end_offset > start_offset),
    CONSTRAINT chk_notes_color
        CHECK (color IN ('YELLOW', 'GREEN', 'BLUE', 'RED'))
);

CREATE INDEX idx_notes_account_section
    ON notes(account_id, section_id);

CREATE INDEX idx_notes_section
    ON notes(section_id);

CREATE TRIGGER tr_notes_updated_at
    BEFORE UPDATE ON notes
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
