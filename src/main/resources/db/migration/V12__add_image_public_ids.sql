ALTER TABLE authors
    ADD COLUMN portrait_public_id VARCHAR(500);

ALTER TABLE works
    ADD COLUMN cover_public_id VARCHAR(500);
