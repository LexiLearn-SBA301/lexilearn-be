package com.sba.lexilearnbe.modules.work.dto.request;

import com.sba.lexilearnbe.modules.work.enums.CommentatorType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

public record CreateWorkCommentaryRequest(
        @Size(max = 300, message = "Tiêu đề bình phẩm tối đa 300 ký tự")
        @Pattern(regexp = "(?s).*\\S.*", message = "Tiêu đề bình phẩm không được để trống")
        String title,

        @NotBlank(message = "Nội dung bình phẩm không được để trống")
        String content,

        @NotBlank(message = "Tên người bình phẩm không được để trống")
        @Size(max = 200, message = "Tên người bình phẩm tối đa 200 ký tự")
        String commentatorName,

        @NotNull(message = "Loại người bình phẩm không được để trống")
        CommentatorType commentatorType,

        @Size(max = 300, message = "Tên nguồn bình phẩm tối đa 300 ký tự")
        String sourceTitle,

        @Size(max = 500, message = "Đường dẫn nguồn bình phẩm tối đa 500 ký tự")
        @URL(message = "Đường dẫn nguồn bình phẩm không hợp lệ")
        String sourceUrl,

        @Min(value = 0, message = "Năm xuất bản không hợp lệ")
        @Max(value = 2100, message = "Năm xuất bản không hợp lệ")
        Integer publishedYear,

        Boolean isFeatured,
        Boolean isPublished
) {
}
