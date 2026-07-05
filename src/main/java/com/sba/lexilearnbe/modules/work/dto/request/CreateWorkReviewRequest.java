package com.sba.lexilearnbe.modules.work.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateWorkReviewRequest(
        @Size(max = 300, message = "Tiêu đề bình phẩm tối đa 300 ký tự")
        @Pattern(regexp = "(?s).*\\S.*", message = "Tiêu đề bình phẩm không được để trống")
        String title,

        @NotBlank(message = "Nội dung bình phẩm không được để trống")
        @Size(max = 10000, message = "Nội dung bình phẩm tối đa 10000 ký tự")
        String content
) {
}
