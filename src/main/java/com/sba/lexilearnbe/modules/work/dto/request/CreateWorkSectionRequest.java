package com.sba.lexilearnbe.modules.work.dto.request;

import com.sba.lexilearnbe.modules.work.enums.WorkSectionContentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateWorkSectionRequest {

    @Positive(message = "Số thứ tự phần văn bản phải lớn hơn 0")
    private Integer number;

    @NotBlank(message = "Tiêu đề phần văn bản không được để trống")
    @Size(max = 300, message = "Tiêu đề phần văn bản tối đa 300 ký tự")
    private String title;

    @NotBlank(message = "Nội dung phần văn bản không được để trống")
    private String content;

    private WorkSectionContentType contentType;
}
