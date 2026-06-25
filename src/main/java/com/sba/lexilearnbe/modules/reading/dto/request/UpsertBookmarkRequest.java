package com.sba.lexilearnbe.modules.reading.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record UpsertBookmarkRequest(
    UUID currentSectionId,
    @NotNull(message = "Vị trí đọc không được để trống")
    @Min(value = 0, message = "Vị trí đọc không được âm")
    Integer position,
    @NotNull(message = "Tiến độ đọc không được để trống")
    @DecimalMin(value = "0.00", message = "Tiến độ đọc phải từ 0 đến 100")
    @DecimalMax(value = "100.00", message = "Tiến độ đọc phải từ 0 đến 100")
    BigDecimal progressPercent,
    @NotNull(message = "Trạng thái hoàn thành không được để trống")
    Boolean isCompleted
) {
}
