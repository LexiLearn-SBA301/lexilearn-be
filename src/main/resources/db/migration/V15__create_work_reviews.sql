CREATE TABLE work_reviews (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    work_id     UUID NOT NULL,
    account_id  UUID NOT NULL,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ,

    CONSTRAINT fk_work_reviews_work
        FOREIGN KEY (work_id) REFERENCES works(id) ON DELETE CASCADE,
    CONSTRAINT fk_work_reviews_account
        FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE,
    CONSTRAINT uq_work_reviews_account_work
        UNIQUE (account_id, work_id)
);

CREATE TABLE work_review_revisions (
    id                UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    review_id         UUID NOT NULL,
    version_number    INTEGER NOT NULL,
    title             VARCHAR(300),
    content           TEXT NOT NULL,
    status            VARCHAR(20) NOT NULL,
    rejection_reason  VARCHAR(1000),
    reviewed_by       UUID,
    reviewed_at       TIMESTAMPTZ,
    created_at        TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at        TIMESTAMPTZ,

    CONSTRAINT fk_work_review_revisions_review
        FOREIGN KEY (review_id) REFERENCES work_reviews(id) ON DELETE CASCADE,
    CONSTRAINT fk_work_review_revisions_reviewer
        FOREIGN KEY (reviewed_by) REFERENCES accounts(id) ON DELETE SET NULL,
    CONSTRAINT uq_work_review_revisions_version
        UNIQUE (review_id, version_number),
    CONSTRAINT chk_work_review_revisions_version
        CHECK (version_number > 0),
    CONSTRAINT chk_work_review_revisions_status
        CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED', 'SUPERSEDED')),
    CONSTRAINT chk_work_review_revisions_rejection
        CHECK (
            status <> 'REJECTED'
            OR (rejection_reason IS NOT NULL AND LENGTH(TRIM(rejection_reason)) > 0)
        )
);

CREATE UNIQUE INDEX uq_work_review_revisions_pending
    ON work_review_revisions(review_id)
    WHERE status = 'PENDING';

CREATE UNIQUE INDEX uq_work_review_revisions_approved
    ON work_review_revisions(review_id)
    WHERE status = 'APPROVED';

CREATE INDEX idx_work_reviews_work
    ON work_reviews(work_id);

CREATE INDEX idx_work_reviews_account
    ON work_reviews(account_id);

CREATE INDEX idx_work_review_revisions_status_created
    ON work_review_revisions(status, created_at);

CREATE TRIGGER tr_work_reviews_updated_at
    BEFORE UPDATE ON work_reviews
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER tr_work_review_revisions_updated_at
    BEFORE UPDATE ON work_review_revisions
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
