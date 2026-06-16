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
public class CreateWorkCharacterRequest {

    @NotBlank(message = "Tên nhân vật không được để trống")
    @Size(max = 200, message = "Tên nhân vật tối đa 200 ký tự")
    private String name;

    private String description;

    @NotBlank(message = "Phân tích nhân vật không được để trống")
    private String analysis;

    @NotNull(message = "Thứ tự hiển thị không được để trống")
    @PositiveOrZero(message = "Thứ tự hiển thị không được nhỏ hơn 0")
    private Integer displayOrder;
}
