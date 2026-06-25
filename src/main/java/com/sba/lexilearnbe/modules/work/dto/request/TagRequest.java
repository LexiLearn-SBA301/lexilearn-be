package com.sba.lexilearnbe.modules.work.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TagRequest {
    @NotBlank(message = "Tên thẻ không được để trống")
    @Size(max = 100, message = "Tên thẻ không vượt quá 100 ký tự")
    private String name;
    private String description;
}