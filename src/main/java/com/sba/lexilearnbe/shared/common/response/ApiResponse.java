package com.sba.lexilearnbe.shared.common.response;


import lombok.*;

import java.time.LocalDateTime;

/**
 * Mẫu response chung cho toàn bộ API — JSON luôn trả đủ 5 field
 * (field null vẫn hiện, vd "result": null) để FE parse 1 schema duy nhất.
 *
 * path + timestamp được {@link ApiResponseBodyAdvice} tự điền nếu nơi build bỏ trống
 * — controller chỉ cần set code/message/result.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {

    @Builder.Default
    private String code = "success";

    private String message;
    private T result;
    private LocalDateTime timestamp;
    private String path;
}
