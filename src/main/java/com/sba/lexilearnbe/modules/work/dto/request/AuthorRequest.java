package com.sba.lexilearnbe.modules.work.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.validator.constraints.URL;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthorRequest {

    @NotBlank(message = "Tên tác giả không được để trống")
    @Size(max = 100, message = "Tên tác giả không được vượt quá 100 ký tự")
    private String name;

    @Size(max = 100, message = "Bút danh không được vượt quá 100 ký tự")
    private String penName;

    @Max(value = 2026, message = "Năm sinh không hợp lệ")
    private Integer birthYear;

    @Max(value = 2026, message = "Năm mất không được lớn hơn năm hiện tại")
    private Integer deathYear;

    @Pattern(regexp = "^(dan_gian|trung_dai|hien_dai)$",
            message = "Thời kỳ chỉ được chọn 1 trong 3 giá trị: dan_gian, trung_dai, hien_dai")
    private String period;

    @Size(max = 2000, message = "Tiểu sử quá dài, vui lòng rút gọn dưới 2000 ký tự")
    private String bio;

    @Size(max = 500, message = "Đường dẫn ảnh quá dài")
    @URL(message = "Đường dẫn ảnh chân dung không đúng định dạng URL")
    private String portraitUrl;
}