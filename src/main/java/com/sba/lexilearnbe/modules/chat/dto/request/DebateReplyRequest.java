package com.sba.lexilearnbe.modules.chat.dto.request;

import jakarta.validation.constraints.Size;

/**
 * 1 lượt phát biểu của người học khi hội đồng phê bình đang tạm dừng chờ họ.
 *
 * message để trống/null = BỎ QUA (chưa nói gì) hoặc KẾT THÚC phản biện (đã nói vài lượt)
 * -> hội đồng đi tiếp ngay thay vì chờ hết giờ. Hai hành động đó là CÙNG một tín hiệu,
 * khác nhau đúng ở chỗ đã gửi tin nào chưa.
 *
 * Vòng 1 chỉ cần message (nêu luận điểm của mình). Vòng 2 bắt buộc thêm targetArgId +
 * stance (phản biện thì phải nhắm vào một luận điểm cụ thể) — AI validate và trả 400 nếu
 * id không có thật trong bảng tin.
 */
public record DebateReplyRequest(
        @Size(max = 2000, message = "Nội dung tối đa 2000 ký tự")
        String message,

        /** [Vòng 2] id luận điểm bị nhắm, vd "tam_ly-a2" — FE lấy từ event vòng 1. */
        String targetArgId,

        /** [Vòng 2] agree | disagree | qualify */
        String stance
) {
}
