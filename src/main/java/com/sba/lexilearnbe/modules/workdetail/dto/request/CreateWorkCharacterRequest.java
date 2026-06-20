package com.sba.lexilearnbe.modules.workdetail.dto.request;

import com.sba.lexilearnbe.modules.workdetail.enums.WorkCharacterRoleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateWorkCharacterRequest {

    @NotBlank(message = "Tên nhân vật không được để trống")
    @Size(max = 150, message = "Tên nhân vật tối đa 150 ký tự")
    private String name;

    private WorkCharacterRoleType roleType;

    private String description;

    private String analysis;
}
