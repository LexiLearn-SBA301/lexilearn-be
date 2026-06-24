# Reading Experience - Nghiep vu, test case va cach test Swagger

## 1. Pham vi phan D

Phan D phu trach trai nghiem doc ca nhan cua user sau khi da dang nhap:

- Luu tac pham vao danh sach dang doc.
- Luu section dang doc hien tai.
- Luu vi tri doc trong section.
- Luu tien do doc cua ca tac pham.
- Danh dau da doc xong.
- Tao highlight/ghi chu tren mot doan text trong section.
- Xem va xoa note cua chinh user.

Phan D khong sua noi dung tac pham. Noi dung van nam o `work_sections.content`
do module `workdetail` quan ly.

## 2. Bang du lieu

### bookmarks

Luu tien do doc cua tung user theo tung tac pham.

| Field | Y nghia |
|---|---|
| `id` | ID cua bookmark |
| `account_id` | User dang doc, FK toi `accounts.id` |
| `work_id` | Tac pham dang doc, FK toi `works.id` |
| `current_section_id` | Section user dang doc hien tai, co the null |
| `position` | Vi tri doc trong `current_section.content`, tinh theo UTF-16 offset |
| `progress_percent` | Tien do doc cua ca tac pham, tu 0 den 100 |
| `is_completed` | User da doc xong tac pham chua |
| `completed_at` | Thoi diem doc xong, chi co khi `is_completed = true` |
| `created_at` | Thoi diem tao bookmark |
| `updated_at` | Thoi diem cap nhat bookmark gan nhat |

Quy tac:

- Moi account chi co mot bookmark cho mot work: `UNIQUE(account_id, work_id)`.
- `position >= 0`.
- `progress_percent` nam trong khoang `0..100`.
- Neu `current_section_id != null`, section phai thuoc dung `work_id`.
- Neu `current_section_id != null`, `position` khong duoc lon hon do dai
  `current_section.content`.
- Neu `isCompleted = true`, backend ep `progressPercent = 100.00` va set
  `completedAt = now`.
- Neu `isCompleted = false`, backend clear `completedAt`.

### notes

Luu highlight/ghi chu cua user theo section.

| Field | Y nghia |
|---|---|
| `id` | ID cua note |
| `account_id` | User tao note, FK toi `accounts.id` |
| `section_id` | Section duoc highlight, FK toi `work_sections.id` |
| `start_offset` | Vi tri bat dau highlight trong `section.content` |
| `end_offset` | Vi tri ket thuc highlight trong `section.content` |
| `highlighted_text` | Snapshot text user da highlight |
| `user_note` | Ghi chu ca nhan, co the null |
| `color` | Mau highlight: `YELLOW`, `GREEN`, `BLUE`, `RED` |
| `created_at` | Thoi diem tao note |
| `updated_at` | Thoi diem cap nhat note |

Quy tac:

- User chi thay note cua chinh minh.
- User chi xoa note cua chinh minh.
- `startOffset >= 0`.
- `endOffset > startOffset`.
- `endOffset <= section.content.length()`.
- Backend check `section.content.substring(startOffset, endOffset)` phai khop
  `highlightedText`.
- Chua lam PATCH note trong scope hien tai. Muon sua vung highlight thi xoa note
  cu va tao note moi.

## 3. API

Tat ca API phan D can dang nhap. FE gui access token qua header:

```text
Authorization: Bearer <access_token>
```

Backend tu parse `accountId` tu JWT. FE khong gui `accountId` trong body.

### GET /api/v1/me/bookmarks

Lay danh sach tac pham dang doc cua user hien tai.

Response:

```json
{
  "code": "success",
  "message": "Lay danh sach bookmark thanh cong",
  "result": [
    {
      "id": "bookmark-id",
      "work": {
        "id": "work-id",
        "slug": "truyen-kieu",
        "title": "Truyen Kieu",
        "coverUrl": "https://example.com/cover.jpg",
        "authorName": "Nguyen Du"
      },
      "currentSection": {
        "id": "section-id",
        "number": 1,
        "title": "Phan 1"
      },
      "position": 1200,
      "progressPercent": 45.50,
      "isCompleted": false,
      "completedAt": null,
      "createdAt": "2026-06-22T07:00:00",
      "updatedAt": "2026-06-22T07:10:00"
    }
  ],
  "timestamp": "2026-06-22T07:10:00",
  "path": "/api/v1/me/bookmarks"
}
```

### PUT /api/v1/me/bookmarks/{workId}

Tao hoac cap nhat bookmark cua user cho mot tac pham.

Request:

```json
{
  "currentSectionId": "section-id",
  "position": 1200,
  "progressPercent": 45.5,
  "isCompleted": false
}
```

Request danh dau doc xong:

```json
{
  "currentSectionId": "section-id",
  "position": 3500,
  "progressPercent": 90,
  "isCompleted": true
}
```

Ket qua khi `isCompleted = true`: backend tra `progressPercent = 100.00` va
`completedAt` co gia tri.

### DELETE /api/v1/me/bookmarks/{workId}

Xoa bookmark cua user cho tac pham.

Response:

```json
{
  "code": "success",
  "message": "Xoa bookmark thanh cong",
  "result": null,
  "timestamp": "2026-06-22T07:10:00",
  "path": "/api/v1/me/bookmarks/work-id"
}
```

### GET /api/v1/sections/{sectionId}/notes

Lay danh sach note cua user hien tai trong mot section.

Response:

```json
{
  "code": "success",
  "message": "Lay danh sach ghi chu thanh cong",
  "result": [
    {
      "id": "note-id",
      "sectionId": "section-id",
      "startOffset": 15,
      "endOffset": 23,
      "highlightedText": "kiet tac",
      "userNote": "Y chinh can nho",
      "color": "YELLOW",
      "createdAt": "2026-06-22T07:00:00",
      "updatedAt": "2026-06-22T07:00:00"
    }
  ],
  "timestamp": "2026-06-22T07:10:00",
  "path": "/api/v1/sections/section-id/notes"
}
```

### POST /api/v1/sections/{sectionId}/notes

Tao highlight/ghi chu.

Request:

```json
{
  "startOffset": 15,
  "endOffset": 23,
  "highlightedText": "kiet tac",
  "userNote": "Y chinh can nho",
  "color": "YELLOW"
}
```

Luu y: `highlightedText` phai khop chinh xac voi text trong
`section.content.substring(startOffset, endOffset)`.

### DELETE /api/v1/notes/{noteId}

Xoa note cua user hien tai.

Response:

```json
{
  "code": "success",
  "message": "Xoa ghi chu thanh cong",
  "result": null,
  "timestamp": "2026-06-22T07:10:00",
  "path": "/api/v1/notes/note-id"
}
```

## 4. Cach test tren Swagger

### Buoc 1 - Chay backend

Dam bao PostgreSQL/Redis dang chay va migration da apply thanh cong.

Mo Swagger:

```text
http://localhost:8080/swagger-ui/index.html
```

### Buoc 2 - Dang nhap lay token

Goi:

```text
POST /api/v1/auth/login
```

Body:

```json
{
  "email": "user@example.com",
  "password": "password123"
}
```

Copy `result.accessToken`.

### Buoc 3 - Authorize Swagger

Click nut `Authorize` tren Swagger, dien:

```text
Bearer <access_token>
```

Neu Swagger da tu them prefix `Bearer`, chi paste token. Neu chua, paste ca chuoi
`Bearer ...`.

### Buoc 4 - Lay work va section de test

Goi:

```text
GET /api/v1/works
```

Lay `work.id` cua mot tac pham.

Sau do goi:

```text
GET /api/v1/works/{workId}/sections
```

Lay `section.id`.

Neu can test note offset, goi:

```text
GET /api/v1/sections/{sectionId}
```

Copy mot doan text trong `result.content`, tu tinh `startOffset/endOffset` theo
vi tri string. De test nhanh, co the highlight doan dau tien:

```text
startOffset = 0
endOffset = do dai doan text dau tien
highlightedText = dung doan text dau tien trong content
```

## 5. Test case Bookmark

### TC-BM-01: Lay bookmark khi chua co du lieu

API:

```text
GET /api/v1/me/bookmarks
```

Expected:

- HTTP 200.
- `code = success`.
- `result = []`.

### TC-BM-02: Tao bookmark moi

API:

```text
PUT /api/v1/me/bookmarks/{workId}
```

Body:

```json
{
  "currentSectionId": "section-id",
  "position": 0,
  "progressPercent": 0,
  "isCompleted": false
}
```

Expected:

- HTTP 200.
- Response co `work.id = workId`.
- Response co `currentSection.id = section-id`.
- `position = 0`.
- `progressPercent = 0`.
- `isCompleted = false`.
- `completedAt = null`.

### TC-BM-03: Cap nhat bookmark da ton tai

API:

```text
PUT /api/v1/me/bookmarks/{workId}
```

Body:

```json
{
  "currentSectionId": "section-id",
  "position": 1200,
  "progressPercent": 45.5,
  "isCompleted": false
}
```

Expected:

- HTTP 200.
- Van chi co mot bookmark cho work do.
- `position = 1200`.
- `progressPercent = 45.5`.
- `updatedAt` thay doi.

### TC-BM-04: Danh dau doc xong

API:

```text
PUT /api/v1/me/bookmarks/{workId}
```

Body:

```json
{
  "currentSectionId": "section-id",
  "position": 1200,
  "progressPercent": 45.5,
  "isCompleted": true
}
```

Expected:

- HTTP 200.
- Backend ep `progressPercent = 100.00`.
- `isCompleted = true`.
- `completedAt != null`.

### TC-BM-05: Section khong thuoc work

API:

```text
PUT /api/v1/me/bookmarks/{workIdA}
```

Body dung `currentSectionId` cua work khac.

Expected:

- HTTP 400.
- `code = validation_error`.

### TC-BM-06: Position vuot do dai content

API:

```text
PUT /api/v1/me/bookmarks/{workId}
```

Body:

```json
{
  "currentSectionId": "section-id",
  "position": 999999999,
  "progressPercent": 10,
  "isCompleted": false
}
```

Expected:

- HTTP 400.
- `code = validation_error`.

### TC-BM-07: Progress ngoai khoang 0..100

Body:

```json
{
  "currentSectionId": "section-id",
  "position": 0,
  "progressPercent": 120,
  "isCompleted": false
}
```

Expected:

- HTTP 400.
- `code = validation_error`.

### TC-BM-08: Xoa bookmark

API:

```text
DELETE /api/v1/me/bookmarks/{workId}
```

Expected:

- HTTP 200.
- `code = success`.
- Message xoa thanh cong.
- Goi lai `GET /api/v1/me/bookmarks` khong con bookmark do.

### TC-BM-09: Xoa bookmark khong ton tai

API:

```text
DELETE /api/v1/me/bookmarks/{workIdChuaBookmark}
```

Expected:

- HTTP 404.
- `code = bookmark_not_found`.

## 6. Test case Note

### TC-NOTE-01: Lay notes khi chua co du lieu

API:

```text
GET /api/v1/sections/{sectionId}/notes
```

Expected:

- HTTP 200.
- `result = []`.

### TC-NOTE-02: Tao note hop le

Lay `content` cua section. Chon mot doan text co offset dung.

API:

```text
POST /api/v1/sections/{sectionId}/notes
```

Body vi du:

```json
{
  "startOffset": 0,
  "endOffset": 10,
  "highlightedText": "10 ky tu dau",
  "userNote": "Ghi chu test",
  "color": "YELLOW"
}
```

Expected:

- HTTP 201.
- Response co `sectionId`.
- Offset va `highlightedText` dung request.
- `color = YELLOW`.

### TC-NOTE-03: Lay lai note vua tao

API:

```text
GET /api/v1/sections/{sectionId}/notes
```

Expected:

- HTTP 200.
- Danh sach co note vua tao.
- Notes sap xep theo `startOffset` tang dan.

### TC-NOTE-04: Highlighted text khong khop offset

API:

```text
POST /api/v1/sections/{sectionId}/notes
```

Body:

```json
{
  "startOffset": 0,
  "endOffset": 5,
  "highlightedText": "sai text",
  "userNote": "test sai",
  "color": "YELLOW"
}
```

Expected:

- HTTP 400.
- `code = validation_error`.

### TC-NOTE-05: Offset khong hop le

Body:

```json
{
  "startOffset": 10,
  "endOffset": 5,
  "highlightedText": "abc",
  "userNote": "test",
  "color": "YELLOW"
}
```

Expected:

- HTTP 400.
- `code = validation_error`.

### TC-NOTE-06: Offset vuot do dai content

Body:

```json
{
  "startOffset": 0,
  "endOffset": 999999999,
  "highlightedText": "abc",
  "userNote": "test",
  "color": "YELLOW"
}
```

Expected:

- HTTP 400.
- `code = validation_error`.

### TC-NOTE-07: Color khong hop le

Body:

```json
{
  "startOffset": 0,
  "endOffset": 5,
  "highlightedText": "abcde",
  "userNote": "test",
  "color": "PURPLE"
}
```

Expected:

- HTTP 400.
- `code = invalid_format` hoac `validation_error`, tuy Jackson/validation bat loi
  enum o tang nao.

### TC-NOTE-08: Xoa note cua minh

API:

```text
DELETE /api/v1/notes/{noteId}
```

Expected:

- HTTP 200.
- `code = success`.
- Goi lai list notes khong con note do.

### TC-NOTE-09: Xoa note khong ton tai hoac khong thuoc minh

API:

```text
DELETE /api/v1/notes/{noteIdKhac}
```

Expected:

- HTTP 404.
- `code = note_not_found`.

## 7. Test case Auth/Ownership

### TC-AUTH-01: Khong co token

Goi bat ky API phan D ma khong bam Authorize tren Swagger.

Expected:

- HTTP 401.
- `code = unauthenticated` hoac `token_invalid` tuy truong hop.

### TC-AUTH-02: User A khong thay note/bookmark cua User B

1. Login User A, tao bookmark/note.
2. Logout hoac doi token sang User B.
3. Goi `GET /api/v1/me/bookmarks`.
4. Goi `GET /api/v1/sections/{sectionId}/notes`.

Expected:

- User B khong thay du lieu cua User A.
- Neu User B xoa note ID cua User A, response `note_not_found`.

## 8. Loi thuong gap khi test

### 401 Unauthorized

Nguyen nhan:

- Chua bam Authorize tren Swagger.
- Token het han.
- Token khong co prefix `Bearer` trong Swagger setup hien tai.

### 404 work_not_found / section_not_found

Nguyen nhan:

- Dung sai UUID.
- Work/section da bi xoa.
- Work chua publish, do service dang validate chi cho doc work published.

### 400 validation_error khi tao note

Nguyen nhan pho bien:

- Offset sai.
- `highlightedText` khong khop content.
- `endOffset <= startOffset`.
- `endOffset` vuot do dai content.

### Khong tinh duoc offset nhanh

Voi test thu cong tren Swagger, nen chon doan dau tien cua content:

```text
startOffset = 0
endOffset = do dai chuoi copied
highlightedText = chuoi copied
```

Sau nay FE se tinh offset tu selection cua browser.
