package com.sba.lexilearnbe.modules.work.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateWorkReviewRequest(
        @Size(max = 300, message = "Tiêu đề bình phẩm tối đa 300 ký tự")
        String title,

        @Size(max = 10000, message = "Nội dung bình phẩm tối đa 10000 ký tự")
        @Pattern(regexp = "(?s).*\\S.*", message = "Nội dung bình phẩm không được để trống")
        String content
) {
}
