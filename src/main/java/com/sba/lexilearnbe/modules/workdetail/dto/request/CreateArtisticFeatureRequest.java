package com.sba.lexilearnbe.modules.workdetail.dto.request;

import com.sba.lexilearnbe.modules.workdetail.enums.ArtisticFeatureType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateArtisticFeatureRequest {

    @NotNull(message = "Loại đặc điểm nghệ thuật không được để trống")
    private ArtisticFeatureType featureType;

    @NotBlank(message = "Tiêu đề đặc điểm nghệ thuật không được để trống")
    @Size(max = 200, message = "Tiêu đề đặc điểm nghệ thuật tối đa 200 ký tự")
    private String title;

    private String description;
}
