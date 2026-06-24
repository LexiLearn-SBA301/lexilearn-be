Tích hợp Module Reading (Tiến độ đọc & Highlight)
Tài liệu này mô tả kế hoạch triển khai các tính năng lưu tiến độ đọc (Bookmarks) và highlight/ghi chú (Notes) vào màn hình Reading hiện tại của module Work (ReadingPage.jsx & ReadingPageContent.jsx).

Mục tiêu
Lưu & Khôi phục Tiến độ đọc (Bookmarks): Tự động lưu tiến độ (section hiện tại, scroll offset, phần trăm) và khôi phục khi user quay lại tác phẩm.
Đánh dấu & Ghi chú (Highlights/Notes): Cho phép user bôi đen văn bản để highlight (với màu tùy chọn), thêm ghi chú cá nhân và xem lại các highlight cũ.
Quản lý danh sách: Trang (hoặc block) hiển thị danh sách các tác phẩm "Đang đọc" (Bookmarks).
WARNING

Open Questions (Câu hỏi cần làm rõ)

Tính toán Offset cho Highlight: Hiện tại văn bản văn xuôi đang được split bằng \n\n để map ra thẻ <p>. Việc user bôi đen văn bản xuyên qua các đoạn <p> sẽ làm cho việc tính startOffset và endOffset theo chuỗi content gốc gặp khó khăn. Đề xuất: Có thể render toàn bộ content dưới dạng dangerouslySetInnerHTML (sau khi đã escape HTML và thay \n\n bằng <br/><br/>), kết hợp bọc các thẻ <mark> vào các đoạn highlight. Hoặc sử dụng một thư viện highlight text chuyên dụng (như mark.js). Bạn nghĩ sao về cách render này?
Danh sách Đang đọc: Theo prompt có mô tả về "Trang Đang đọc hoặc block Tiếp tục đọc". Màn hình hiển thị danh sách Bookmark này dự kiến sẽ đặt ở đâu trong UI (VD: Sidebar thư viện, Trang chủ, Menu User)? Tôi có nên tạo hẳn một page riêng (/dang-doc) cho nó không?
UI Menu Highlight: Hiện tại khi bôi đen có popup "Hỏi Mộc Bản AI". Tôi sẽ thay thế/bổ sung popup đó thành một toolbar nhỏ có các nút chọn màu highlight (Vàng, Xanh lá, Xanh dương, Đỏ) và nút "Thêm ghi chú". Đồng ý chứ?
Trạng thái lưu: Nút "Đánh dấu hoàn thành" và trạng thái lưu/xoá tiến độ sẽ được thiết kế ở đâu cho hợp lý? (VD: Đặt dưới nút "Chuyển chương tiếp theo" ở cuối bài, hoặc trên thanh header thả xuống?)
Phân tích & Thay đổi Đề xuất
1. API Services & Hooks (src/services/reading.service.js & src/features/library/hooks/useReading.js)
   reading.service.js
   Tạo các hàm gọi API sử dụng Axios/fetch hiện tại của dự án:
   getBookmarks(): Gọi GET /api/v1/me/bookmarks
   upsertBookmark(workId, payload): Gọi PUT /api/v1/me/bookmarks/{workId}
   deleteBookmark(workId): Gọi DELETE /api/v1/me/bookmarks/{workId}
   getSectionNotes(sectionId): Gọi GET /api/v1/sections/{sectionId}/notes
   createNote(sectionId, payload): Gọi POST /api/v1/sections/{sectionId}/notes
   deleteNote(noteId): Gọi DELETE /api/v1/notes/{noteId}
   useReading.js
   Bọc các hàm API thành React Query hooks:
   useGetBookmarks()
   useUpsertBookmark()
   useDeleteBookmark()
   useGetSectionNotes(sectionId)
   useCreateNote()
   useDeleteNote()
2. Logic Lưu & Khôi phục Tiến độ (ReadingPage.jsx & ReadingPageContent.jsx)
   Khôi phục (Restore Progress):
   Khi ReadingPage.jsx mount, lấy danh sách bookmarks và lọc ra bookmark có workId khớp với ID của tác phẩm đang đọc.
   Nếu bookmark đó trỏ tới một currentSectionId khác với section đang mở và người dùng vừa mới vào trang (chưa tự ý chọn mục lục), hiển thị một Toast/Modal nhỏ hỏi "Tiếp tục đọc từ chương X?".
   Hoặc nếu user vừa vào /thu-vien/:slug/doc, sẽ tự động redirect tới /thu-vien/:slug/doc/:sectionId dựa theo bookmark.
   Lưu tiến độ (Save Progress):
   Lắng nghe event scroll trong ReadingPageContent. Cần debounce ~1500ms để không gọi API liên tục.
   Hoặc chỉ lưu khi: Chuyển chương (handleNavigate), User rời khỏi trang (useEffect cleanup), hoặc nhấn nút "Lưu thủ công".
3. Logic Highlight & Note (ReadingPageContent.jsx)
   Render text kèm Highlight:
   Thay vì split('\n\n') đơn giản, cần xử lý content gốc, nhúng các tag <mark class="highlight-yellow" data-note-id="xxx">...</mark> dựa vào mảng notes trả về từ API.
   Cần viết một parser nhỏ để mapping các khoảng [startOffset, endOffset] mà không làm bể HTML.
   Lấy offset khi bôi đen (Highlight Selection):
   Tính toán global startOffset và endOffset của khoảng text đã chọn so với content gốc.
   Điều này yêu cầu bọc container bài đọc, dùng TreeWalker hoặc Range API để tính tổng số ký tự text node đi qua tính từ đầu container.
   Hiển thị tooltip chọn màu highlight và input nhập user note. Gọi useCreateNote mutation, sau đó invalidate useGetSectionNotes để re-render highlight mới.
4. UI/UX
   Cập nhật popup khi bôi đen text: Có thể chọn màu để Highlight + Icon Note + Icon Hỏi AI.
   Sidebar hoặc Modal cho phép xem danh sách các Notes đã tạo trong section hiện tại (Click vào để scroll tới đoạn đó).
   Cuối màn hình ReadingPageContent, thêm phần "Tiến độ: X% - Đánh dấu hoàn thành".
   Verification Plan
   Manual Verification
   Đăng nhập với account test (test.reader@lexilearn.local).
   Truy cập một tác phẩm (VD: Chị em Thúy Kiều), scroll giữa chừng. Reload trang xem có khôi phục vị trí không.
   Chuyển sang section khác, tắt trình duyệt, mở lại, xem có đúng mở section đó ra không.
   Bôi đen một đoạn văn xuôi và thơ, thêm highlight. Đảm bảo offset tính đúng và gọi API 200 OK.
   Nhấn hoàn thành tác phẩm và kiểm tra API.
   Xin hãy xem qua phần Open Questions ở trên và phản hồi để tôi có thể bắt đầu code.