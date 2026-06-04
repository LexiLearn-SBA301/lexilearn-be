-- ============================================================
-- V1: Auth tables — accounts, roles, account_roles
-- ============================================================

-- ── accounts ────────────────────────────────────────────────
CREATE TABLE accounts (
    id                  UUID            PRIMARY KEY,
    email               VARCHAR(255)    NOT NULL,
    password_hash       VARCHAR(255)    NOT NULL,
    status              VARCHAR(20)     NOT NULL DEFAULT 'UNVERIFIED'
                            CHECK (status IN ('ACTIVE', 'UNVERIFIED', 'LOCKED')),
    email_verified_at   TIMESTAMP,
    created_at          TIMESTAMP       NOT NULL,
    updated_at          TIMESTAMP
);

-- Email so sánh case-insensitive (app cũng lowercase trước khi lưu,
-- index này là chốt chặn cuối ở DB: Foo@x.com == foo@x.com)
CREATE UNIQUE INDEX uq_accounts_email ON accounts (LOWER(email));

-- ── roles ───────────────────────────────────────────────────
CREATE TABLE roles (
    id          UUID            PRIMARY KEY DEFAULT gen_random_uuid(),
    name        VARCHAR(50)     NOT NULL UNIQUE,
    created_at  TIMESTAMP       NOT NULL DEFAULT now(),
    updated_at  TIMESTAMP
);

-- ── account_roles (M:N) ─────────────────────────────────────
CREATE TABLE account_roles (
    account_id  UUID NOT NULL,
    role_id     UUID NOT NULL,
    PRIMARY KEY (account_id, role_id),
    -- Xóa account → xóa luôn liên kết role của nó
    CONSTRAINT fk_account_roles_account FOREIGN KEY (account_id)
        REFERENCES accounts (id) ON DELETE CASCADE,
    -- Không cho xóa role khi vẫn còn account đang giữ
    CONSTRAINT fk_account_roles_role FOREIGN KEY (role_id)
        REFERENCES roles (id) ON DELETE RESTRICT
);

-- Tra cứu ngược: tìm account theo role
CREATE INDEX idx_account_roles_role_id ON account_roles (role_id);

-- ── Seed roles ──────────────────────────────────────────────
-- Không có prefix ROLE_ trong DB; prefix đó thêm ở tầng Spring Security
INSERT INTO roles (name) VALUES ('USER'), ('ADMIN');
