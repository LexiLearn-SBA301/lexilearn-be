# Tài liệu Tích hợp Đồng bộ Dữ liệu (BE -> AI RAG)

Tài liệu này đặc tả luồng đồng bộ dữ liệu một chiều từ Backend (Java/Postgres) sang AI Service (Python/MongoDB) để phục vụ tính năng RAG.

## 1. Cơ chế đồng bộ (Full Snapshot)

Hệ thống AI áp dụng cơ chế **Full Snapshot Sync**. 
Thay vì gửi partial update (chỉ gửi những trường bị sửa), BE sẽ gửi **toàn bộ dữ liệu của tác phẩm (bao gồm tác phẩm, tác giả, tags, các sections, và các bình phẩm)** mỗi khi có bất kỳ sự thay đổi nào liên quan đến tác phẩm đó.

AI Service sẽ tự động xử lý việc xóa các chunk dữ liệu cũ và tạo chunk mới.

## 2. API Contracts

AI Service cung cấp 2 endpoints nội bộ (Internal API) dành riêng cho BE:

### 2.1. API Upsert (Thêm mới / Cập nhật)
Được gọi khi có bất kỳ thao tác Create/Update nào tác động lên dữ liệu của một tác phẩm.

- **Endpoint**: `PUT /internal/works/{work_slug}/sync`
- **Content-Type**: `application/json`
- **Payload**: JSON format (xem chi tiết ở mục 3).
- **Response**: 
  ```json
  {
    "success": true,
    "work_slug": "to_long",
    "chunks_upserted": 12,
    "chunks_deactivated": 8
  }
  ```

### 2.2. API Delete (Xóa tác phẩm)
Được gọi khi Admin thực sự **xóa (delete)** hoặc **ẩn (deactivate)** hoàn toàn một tác phẩm.

- **Endpoint**: `DELETE /internal/works/{work_slug}/sync`
- **Response**:
  ```json
  {
    "success": true,
    "work_slug": "to_long",
    "chunks_upserted": 0,
    "chunks_deactivated": 12
  }
  ```

---

## 3. Trigger Points (Các điểm cần gọi API Sync trên BE)

BE cần hook/event vào các thao tác sau của Admin để gọi API `PUT`:

1. **Quản lý Tác phẩm (Works)**:
   - Khi tạo mới một tác phẩm -> Gọi `PUT` (sections & commentaries là mảng rỗng `[]`).
   - Khi cập nhật thông tin tác phẩm (đổi tên, năm sáng tác, hoàn cảnh, v.v...) -> Gọi `PUT`.
   - Khi thay đổi ảnh bìa (cover) -> Gọi `PUT`.
   - Khi **xóa** tác phẩm -> Gọi API **`DELETE`**.

2. **Quản lý Nội dung (Work Sections)**:
   - Khi thêm mới 1 section -> Gọi `PUT` (gửi toàn bộ tác phẩm kèm tất cả sections).
   - Khi sửa nội dung/title 1 section -> Gọi `PUT`.
   - Khi xóa 1 section -> Gọi `PUT` (gửi tác phẩm kèm danh sách sections còn lại).

3. **Quản lý Bình phẩm (Commentaries)**:
   - Khi Admin thêm/duyệt (publish)/sửa/xóa một bài bình phẩm thuộc tác phẩm -> Gọi `PUT` (gửi toàn bộ tác phẩm kèm danh sách các bình phẩm đang được public).

4. **Quản lý Tác giả (Authors)** (Tùy chọn nhưng khuyến nghị):
   - Nếu Admin sửa thông tin tác giả (tiểu sử, năm sinh...), BE có thể quét các tác phẩm của tác giả này và gọi `PUT` cho từng tác phẩm, hoặc bỏ qua nếu không quá quan trọng.

---

## 4. Payload Schema & Quy định Dữ liệu

Dưới đây là cấu trúc JSON tĩnh mà BE cần serialize khi gọi API `PUT`:

```json
{
  "schema_version": "literature_work_snapshot.v1",
  "synced_at": "2026-07-19T10:00:00Z",
  "work": {
    "id": "uuid-cua-tac-pham",
    "title": "TỎ LÒNG",
    "slug": "to_long",
    "original_title": "Thuật hoài",
    "genre": "tho_ca",
    "sub_genre": "that_ngon_tu_tuyet",
    "period": "trung_dai",
    "grade": 10,
    "semester": 1,
    "publish_year": null,
    "summary": "Tóm tắt tác phẩm...",
    "cover_url": "https://...",
    "is_published": true,
    "historical_context": "Hoàn cảnh ra đời...",
    "realistic_value": "Giá trị hiện thực...",
    "humanistic_value": "Giá trị nhân đạo...",
    "artistic_value": "Giá trị nghệ thuật...",
    "famous_quote": "Công danh nam tử...",
    "quote_attribution": "Phạm Ngũ Lão"
  },
  "author": {
    "id": "uuid-cua-tac-gia",
    "name": "Phạm Ngũ Lão",
    "pen_name": null,
    "slug": "pham_ngu_lao",
    "birth_year": 1255,
    "death_year": 1320,
    "period": "trung_dai",
    "bio": "Tiểu sử...",
    "portrait_url": null
  },
  "tags": [
    {
      "id": "uuid-tag-1",
      "name": "Thơ trung đại",
      "slug": "tho_trung_dai",
      "description": null
    }
  ],
  "sections": [
    {
      "id": "uuid-section-1",
      "number": 1,
      "title": "Phiên âm",
      "content": "Nội dung phần phiên âm...",
      "content_type": "POETRY",
      "word_count": 28
    }
  ],
  "commentaries": [
    {
      "id": "uuid-comment-1",
      "title": "Khí phách tuổi trẻ",
      "content": "Nội dung bình phẩm...",
      "commentator_name": "Lê Trí Viễn",
      "commentator_type": "EXPERT",
      "source_title": "Bình giảng văn học",
      "source_url": null,
      "published_year": 1998,
      "display_order": 1,
      "is_featured": true,
      "is_published": true
    }
  ]
}
```

### 🔴 CÁC LƯU Ý SỐNG CÒN (CRITICAL RULES) DÀNH CHO BE:

1. **`schema_version`**: Phải luôn hardcode là `"literature_work_snapshot.v1"`. AI sẽ từ chối các version khác.
2. **Không dùng giá trị `null` cho các collection**: Nếu tác phẩm vừa tạo chưa có section, commentaries hay tags nào, BE **bắt buộc phải gửi mảng rỗng `[]`**, tuyệt đối không gửi `null`. 
   - *Ví dụ đúng:* `"sections": []`
   - *Ví dụ sai:* `"sections": null` (Sẽ bị báo lỗi 422 Unprocessable Entity).
3. **Chỉ gửi dữ liệu đã được Public**: Mảng `sections` và `commentaries` chỉ nên chứa các record đang ở trạng thái public/active.
4. **Tham số `number` của Section**: AI phụ thuộc vào trường `number` của section để xác định thứ tự (chunk order). BE cần đảm bảo `number` luôn có giá trị int >= 1.
5. **Đồng bộ tham số URL và Payload**: Tên `work_slug` truyền vào URL (`/internal/works/{work_slug}/sync`) **BẮT BUỘC** phải khớp exacly với `payload.work.slug`. Cả 2 nên dùng định dạng `snake_case` hoặc `kebab-case`.
