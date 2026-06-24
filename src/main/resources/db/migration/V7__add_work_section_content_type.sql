ALTER TABLE work_sections
    ADD COLUMN content_type VARCHAR(30) NOT NULL DEFAULT 'PROSE';

UPDATE work_sections section
SET content_type = 'POETRY'
FROM works work
WHERE section.work_id = work.id
  AND (
      LOWER(work.genre) = 'truyen_tho'
      OR LOWER(work.sub_genre) = 'luc_bat'
  );

ALTER TABLE work_sections
    ADD CONSTRAINT chk_work_sections_content_type
        CHECK (content_type IN ('PROSE', 'POETRY', 'MIXED'));
