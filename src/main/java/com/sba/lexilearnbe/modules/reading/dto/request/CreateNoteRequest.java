package com.sba.lexilearnbe.modules.reading.dto.request;

import com.sba.lexilearnbe.modules.reading.enums.NoteColor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record CreateNoteRequest(
    @NotNull(message = "Vị trí bắt đầu highlight không được để trống")
    @PositiveOrZero(message = "Vị trí bắt đầu highlight không được âm")
    Integer startOffset,
    @NotNull(message = "Vị trí kết thúc highlight không được để trống")
    @PositiveOrZero(message = "Vị trí kết thúc highlight không được âm")
    Integer endOffset,
    @NotBlank(message = "Nội dung highlight không được để trống")
    String highlightedText,
    String userNote,
    @NotNull(message = "Màu highlight không được để trống")
    NoteColor color
) {
}
