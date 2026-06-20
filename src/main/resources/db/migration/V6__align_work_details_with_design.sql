ALTER TABLE work_sections
    ALTER COLUMN title TYPE VARCHAR(300),
    ALTER COLUMN word_count DROP NOT NULL,
    ALTER COLUMN word_count DROP DEFAULT;

ALTER TABLE work_sections
    DROP CONSTRAINT IF EXISTS chk_work_sections_word_count;

ALTER TABLE work_sections
    ADD CONSTRAINT chk_work_sections_word_count
        CHECK (word_count IS NULL OR word_count >= 0);

ALTER TABLE characters
    ALTER COLUMN name TYPE VARCHAR(150),
    ADD COLUMN role_type VARCHAR(50),
    ALTER COLUMN analysis DROP NOT NULL,
    ALTER COLUMN display_order SET DEFAULT 0;

ALTER TABLE characters
    DROP CONSTRAINT IF EXISTS chk_characters_display_order;

ALTER TABLE characters
    ADD CONSTRAINT chk_characters_role_type
        CHECK (
            role_type IS NULL OR
            role_type IN ('MAIN', 'SUPPORTING', 'ANTAGONIST', 'NARRATOR')
        );

ALTER TABLE characters
    ADD CONSTRAINT chk_characters_order
        CHECK (display_order >= 0);

ALTER TABLE artistic_features
    ADD COLUMN feature_type VARCHAR(50),
    ADD COLUMN description TEXT,
    ALTER COLUMN title TYPE VARCHAR(200),
    ALTER COLUMN display_order SET DEFAULT 0;

UPDATE artistic_features
SET feature_type = 'LANGUAGE'
WHERE feature_type IS NULL;

UPDATE artistic_features
SET description = content
WHERE description IS NULL;

ALTER TABLE artistic_features
    ALTER COLUMN feature_type SET NOT NULL,
    DROP COLUMN content;

ALTER TABLE artistic_features
    DROP CONSTRAINT IF EXISTS chk_artistic_features_display_order;

ALTER TABLE artistic_features
    ADD CONSTRAINT chk_artistic_features_type
        CHECK (
            feature_type IN
            ('NARRATIVE', 'LANGUAGE', 'IMAGERY', 'STRUCTURE', 'SYMBOLISM')
        );

ALTER TABLE artistic_features
    ADD CONSTRAINT chk_artistic_features_order
        CHECK (display_order >= 0);

CREATE INDEX idx_characters_work_order
    ON characters(work_id, display_order);

CREATE INDEX idx_artistic_features_work_order
    ON artistic_features(work_id, display_order);
