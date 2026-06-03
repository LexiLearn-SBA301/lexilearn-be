package com.sba.lexilearnbe.shared.common.response;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    @Builder.Default
    private String code = "success";

    private String message;
    private T result;
    private LocalDateTime timestamp;
    private String path;
}
