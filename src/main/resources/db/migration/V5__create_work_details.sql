CREATE TABLE work_sections (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    work_id     UUID NOT NULL,
    number      INTEGER NOT NULL,
    title       VARCHAR(255) NOT NULL,
    content     TEXT NOT NULL,
    word_count  INTEGER NOT NULL DEFAULT 0,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_work_sections_work
        FOREIGN KEY (work_id) REFERENCES works(id) ON DELETE CASCADE,
    CONSTRAINT uq_work_sections_work_number
        UNIQUE (work_id, number),
    CONSTRAINT chk_work_sections_number
        CHECK (number > 0),
    CONSTRAINT chk_work_sections_word_count
        CHECK (word_count >= 0)
);

CREATE TABLE characters (
    id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    work_id        UUID NOT NULL,
    name           VARCHAR(200) NOT NULL,
    description    TEXT,
    analysis       TEXT NOT NULL,
    display_order  INTEGER NOT NULL,
    created_at     TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at     TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_characters_work
        FOREIGN KEY (work_id) REFERENCES works(id) ON DELETE CASCADE,
    CONSTRAINT uq_characters_work_display_order
        UNIQUE (work_id, display_order),
    CONSTRAINT chk_characters_display_order
        CHECK (display_order >= 0)
);

CREATE TABLE artistic_features (
    id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    work_id        UUID NOT NULL,
    title          VARCHAR(255) NOT NULL,
    content        TEXT NOT NULL,
    display_order  INTEGER NOT NULL,
    created_at     TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at     TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_artistic_features_work
        FOREIGN KEY (work_id) REFERENCES works(id) ON DELETE CASCADE,
    CONSTRAINT uq_artistic_features_work_display_order
        UNIQUE (work_id, display_order),
    CONSTRAINT chk_artistic_features_display_order
        CHECK (display_order >= 0)
);

CREATE TRIGGER tr_work_sections_updated_at
    BEFORE UPDATE ON work_sections
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER tr_characters_updated_at
    BEFORE UPDATE ON characters
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER tr_artistic_features_updated_at
    BEFORE UPDATE ON artistic_features
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
