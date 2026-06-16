package com.sba.lexilearnbe.modules.workdetail.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateArtisticFeatureRequest {

    @NotBlank(message = "Tiêu đề đặc điểm nghệ thuật không được để trống")
    @Size(max = 255, message = "Tiêu đề đặc điểm nghệ thuật tối đa 255 ký tự")
    private String title;

    @NotBlank(message = "Nội dung đặc điểm nghệ thuật không được để trống")
    private String content;

    @NotNull(message = "Thứ tự hiển thị không được để trống")
    @PositiveOrZero(message = "Thứ tự hiển thị không được nhỏ hơn 0")
    private Integer displayOrder;
}
