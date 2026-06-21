package com.sba.lexilearnbe.modules.workdetail.dto.request;

import com.sba.lexilearnbe.modules.workdetail.enums.ArtisticFeatureType;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdateArtisticFeatureRequest {

    private ArtisticFeatureType featureType;

    @Size(max = 200, message = "Tiêu đề đặc điểm nghệ thuật tối đa 200 ký tự")
    @Pattern(
            regexp = "(?s).*\\S.*",
            message = "Tiêu đề đặc điểm nghệ thuật không được để trống"
    )
    private String title;

    private String description;
}
