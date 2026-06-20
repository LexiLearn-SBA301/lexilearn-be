package com.sba.lexilearnbe.modules.workdetail.dto.request;

import com.sba.lexilearnbe.modules.workdetail.enums.WorkCharacterRoleType;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdateWorkCharacterRequest {

    @Size(max = 150, message = "Tên nhân vật tối đa 150 ký tự")
    @Pattern(
            regexp = "(?s).*\\S.*",
            message = "Tên nhân vật không được để trống"
    )
    private String name;

    private WorkCharacterRoleType roleType;

    private String description;

    @Pattern(
            regexp = "(?s).*\\S.*",
            message = "Phân tích nhân vật không được để trống"
    )
    private String analysis;
}
