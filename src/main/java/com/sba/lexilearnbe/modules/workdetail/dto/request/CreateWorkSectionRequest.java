package com.sba.lexilearnbe.modules.workdetail.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateWorkSectionRequest {

    @NotNull(message = "Số thứ tự phần văn bản không được để trống")
    @Positive(message = "Số thứ tự phần văn bản phải lớn hơn 0")
    private Integer number;

    @NotBlank(message = "Tiêu đề phần văn bản không được để trống")
    @Size(max = 255, message = "Tiêu đề phần văn bản tối đa 255 ký tự")
    private String title;

    @NotBlank(message = "Nội dung phần văn bản không được để trống")
    private String content;
}