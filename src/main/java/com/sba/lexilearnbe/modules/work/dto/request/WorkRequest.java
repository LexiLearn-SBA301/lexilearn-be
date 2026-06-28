package com.sba.lexilearnbe.modules.work.dto.request;

import com.sba.lexilearnbe.shared.infrastructure.storage.UploadedImageRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WorkRequest {

    @NotBlank(message = "Tiêu đề tác phẩm không được để trống")
    @Size(max = 200, message = "Tiêu đề không được vượt quá 200 ký tự")
    private String title;

    @NotNull(message = "ID tác giả không được để trống")
    private UUID authorId;

    @Size(max = 100, message = "Thể loại phụ quá dài")
    private String subGenre;

    @Size(max = 500, message = "Câu trích dẫn quá dài")
    private String famousQuote;

    private String summary;

    @NotNull(message = "Trạng thái xuất bản không được để trống")
    private Boolean isPublished;

    @NotBlank(message = "Thể loại chính không được để trống")
    private String genre;

    @NotBlank(message = "Thời kỳ không được để trống")
    private String period;

    @NotNull(message = "Năm xuất bản không được để trống")
    private Integer publishYear;
    private String originalTitle;
    private Integer grade;
    private Integer semester;
    private String historicalContext;
    private String realisticValue;
    private String humanisticValue;
    private String artisticValue;
    private String quoteAttribution;
    private Set<UUID> tagIds;

    @Valid
    private UploadedImageRequest cover;
}
