-- ============================================================
-- V11: Thêm cột full_name vào bảng accounts
-- ============================================================

ALTER TABLE accounts
    ADD COLUMN full_name VARCHAR(100) NOT NULL DEFAULT '';
