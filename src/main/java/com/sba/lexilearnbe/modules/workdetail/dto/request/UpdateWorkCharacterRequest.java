package com.sba.lexilearnbe.modules.workdetail.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdateWorkCharacterRequest {

    @Size(max = 200, message = "Tên nhân vật tối đa 200 ký tự")
    @Pattern(
            regexp = "(?s).*\\S.*",
            message = "Tên nhân vật không được để trống"
    )
    private String name;

    private String description;

    @Pattern(
            regexp = "(?s).*\\S.*",
            message = "Phân tích nhân vật không được để trống"
    )
    private String analysis;
}
