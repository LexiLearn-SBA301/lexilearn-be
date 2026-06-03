1. users

id            UUID         PK
email         VARCHAR(255) NOT NULL, UNIQUE
password_hash VARCHAR(255) NOT NULL
name          VARCHAR(100) NOT NULL
avatar_url    VARCHAR(500) NULL
role          VARCHAR(20)  NOT NULL DEFAULT 'USER'  -- USER | ADMIN
is_active     BOOLEAN      DEFAULT TRUE
created_at    TIMESTAMP    DEFAULT NOW()
updated_at    TIMESTAMP    DEFAULT NOW()
  
---
2. authors

id           UUID         PK
name         VARCHAR(200) NOT NULL
pen_name     VARCHAR(200) NULL
slug         VARCHAR(200) NOT NULL, UNIQUE
birth_year   INT          NULL
death_year   INT          NULL
period       VARCHAR(30)  NOT NULL  -- dan_gian | trung_dai | hien_dai
bio          TEXT         NULL
portrait_url VARCHAR(500) NULL
created_at   TIMESTAMP    DEFAULT NOW()
updated_at   TIMESTAMP    DEFAULT NOW()

  ---
3. works ← gộp work_contexts

id                 UUID         PK
author_id          UUID         NULL, FK → authors.id   -- NULL = khuyết danh
title              VARCHAR(300) NOT NULL
slug               VARCHAR(300) NOT NULL, UNIQUE
original_title     VARCHAR(300) NULL
genre              VARCHAR(50)  NOT NULL  -- truyen_ngan | tho_ca | kich_ban | khao_cuu
sub_genre          VARCHAR(50)  NULL
period             VARCHAR(30)  NOT NULL  -- dan_gian | trung_dai | hien_dai
grade              SMALLINT     NULL      -- 10 | 11 | 12
semester           SMALLINT     NULL      -- 1 | 2
publish_year       INT          NULL
summary            TEXT         NULL
cover_url          VARCHAR(500) NULL
is_published       BOOLEAN      DEFAULT TRUE
-- gộp từ work_contexts
historical_context TEXT         NULL
realistic_value    TEXT         NULL
humanistic_value   TEXT         NULL
artistic_value     TEXT         NULL
famous_quote       TEXT         NULL
quote_attribution  VARCHAR(300) NULL
created_at         TIMESTAMP    DEFAULT NOW()
updated_at         TIMESTAMP    DEFAULT NOW()

  ---
4. characters

id            UUID         PK
work_id       UUID         NOT NULL, FK → works.id
name          VARCHAR(150) NOT NULL
role_type     VARCHAR(50)  NULL  -- chinh | phu | phan_dien | nguoi_dan_chuyen
description   TEXT         NULL
analysis      TEXT         NULL
display_order INT          DEFAULT 0
created_at    TIMESTAMP    DEFAULT NOW()
updated_at    TIMESTAMP    DEFAULT NOW()

  ---
5. artistic_features

id            UUID         PK
work_id       UUID         NOT NULL, FK → works.id
feature_type  VARCHAR(50)  NOT NULL  -- narrative | language | imagery | structure | symbolism
title         VARCHAR(200) NOT NULL
description   TEXT         NULL
display_order INT          DEFAULT 0
created_at    TIMESTAMP    DEFAULT NOW()
updated_at    TIMESTAMP    DEFAULT NOW()

  ---
6. work_sections ← đổi tên từ chapters

id         UUID         PK
work_id    UUID         NOT NULL, FK → works.id
number     INT          NOT NULL
title      VARCHAR(300) NOT NULL
content    TEXT         NOT NULL
word_count INT          NULL
created_at TIMESTAMP    DEFAULT NOW()
updated_at TIMESTAMP    DEFAULT NOW()

UNIQUE(work_id, number)
  
---
7. chunks

id           UUID         PK
section_id   UUID         NOT NULL, FK → work_sections.id
content      TEXT         NOT NULL
embedding    VECTOR(768)  NOT NULL   -- đổi số nếu đổi model
start_offset INT          NULL       -- vị trí trong section.content
end_offset   INT          NULL
chunk_order  INT          NOT NULL
token_count  INT          NULL
created_at   TIMESTAMP    DEFAULT NOW()

  ---
8. tags

id          UUID         PK
name        VARCHAR(100) NOT NULL, UNIQUE
slug        VARCHAR(100) NOT NULL, UNIQUE
description TEXT         NULL
created_at  TIMESTAMP    DEFAULT NOW()
updated_at  TIMESTAMP    DEFAULT NOW()
  
---
9. work_tags

work_id UUID  FK → works.id
tag_id  UUID  FK → tags.id

PRIMARY KEY (work_id, tag_id)

  ---
10. bookmarks

id                 UUID         PK
user_id            UUID         NOT NULL, FK → users.id
work_id            UUID         NOT NULL, FK → works.id
current_section_id UUID         NULL, FK → work_sections.id
position           INT          DEFAULT 0
progress_percent   DECIMAL(5,2) DEFAULT 0
is_completed       BOOLEAN      DEFAULT FALSE
completed_at       TIMESTAMP    NULL
created_at         TIMESTAMP    DEFAULT NOW()
updated_at         TIMESTAMP    DEFAULT NOW()

UNIQUE(user_id, work_id)
  
---
11. notes ← bỏ work_id

id               UUID         PK
user_id          UUID         NOT NULL, FK → users.id
section_id       UUID         NOT NULL, FK → work_sections.id
start_offset     INT          NOT NULL
end_offset       INT          NOT NULL
highlighted_text TEXT         NOT NULL
user_note        TEXT         NULL
color            VARCHAR(20)  DEFAULT 'yellow'  -- yellow | green | red | blue
created_at       TIMESTAMP    DEFAULT NOW()
updated_at       TIMESTAMP    DEFAULT NOW()

  ---
12. chat_sessions ← bỏ message_count

id         UUID         PK
user_id    UUID         NOT NULL, FK → users.id
work_id    UUID         NULL, FK → works.id   -- SET NULL khi work bị xóa
title      VARCHAR(300) NOT NULL
created_at TIMESTAMP    DEFAULT NOW()
updated_at TIMESTAMP    DEFAULT NOW()

  ---
13. chat_messages

id             UUID         PK
session_id     UUID         NOT NULL, FK → chat_sessions.id
cited_note_id  UUID         NULL, FK → notes.id
role           VARCHAR(20)  NOT NULL  -- USER | ASSISTANT
content        TEXT         NOT NULL
context_chunks JSONB        NULL
token_count    INT          NULL
created_at     TIMESTAMP    DEFAULT NOW()

  ---
Indexes

CREATE INDEX idx_works_grade_genre    ON works(grade, genre, period) WHERE is_published = TRUE;
CREATE INDEX idx_works_author         ON works(author_id);
CREATE INDEX idx_works_slug           ON works(slug);

CREATE INDEX idx_sections_work_number ON work_sections(work_id, number);

CREATE INDEX idx_chunks_embedding     ON chunks USING hnsw (embedding vector_cosine_ops);
CREATE INDEX idx_chunks_section       ON chunks(section_id, chunk_order);

CREATE INDEX idx_bookmarks_user       ON bookmarks(user_id, updated_at DESC);

CREATE INDEX idx_notes_user_section   ON notes(user_id, section_id);

CREATE INDEX idx_chat_sessions_user   ON chat_sessions(user_id, updated_at DESC);
CREATE INDEX idx_chat_messages_session ON chat_messages(session_id, created_at);
