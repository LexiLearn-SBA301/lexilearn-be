package com.sba.lexilearnbe.modules.workdetail.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdateArtisticFeatureRequest {

    @Size(max = 255, message = "Tiêu đề đặc điểm nghệ thuật tối đa 255 ký tự")
    @Pattern(
            regexp = "(?s).*\\S.*",
            message = "Tiêu đề đặc điểm nghệ thuật không được để trống"
    )
    private String title;

    @Pattern(
            regexp = "(?s).*\\S.*",
            message = "Nội dung đặc điểm nghệ thuật không được để trống"
    )
    private String content;
}
