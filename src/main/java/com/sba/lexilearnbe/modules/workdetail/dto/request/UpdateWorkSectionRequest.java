package com.sba.lexilearnbe.modules.workdetail.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdateWorkSectionRequest {

    @Positive(message = "Số thứ tự phần văn bản phải lớn hơn 0")
    private Integer number;

    @Size(max = 255, message = "Tiêu đề phần văn bản tối đa 255 ký tự")
    @Pattern(
            regexp = "(?s).*\\S.*",
            message = "Tiêu đề phần văn bản không được để trống"
    )
    private String title;

    @Pattern(
            regexp = "(?s).*\\S.*",
            message = "Nội dung phần văn bản không được để trống"
    )
    private String content;
}