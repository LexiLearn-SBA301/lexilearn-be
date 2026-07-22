ALTER TABLE work_reviews
    DROP CONSTRAINT IF EXISTS uq_work_reviews_account_work;

UPDATE work_review_revisions approved
SET status = 'SUPERSEDED'
WHERE approved.status = 'APPROVED'
  AND EXISTS (
      SELECT 1
      FROM work_review_revisions pending
      WHERE pending.review_id = approved.review_id
        AND pending.status = 'PENDING'
  );

UPDATE work_review_revisions
SET status = 'APPROVED',
    rejection_reason = NULL,
    reviewed_by = NULL,
    reviewed_at = COALESCE(reviewed_at, updated_at, created_at)
WHERE status = 'PENDING';
