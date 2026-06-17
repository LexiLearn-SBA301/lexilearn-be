package com.sba.lexilearnbe.modules.work.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.validator.constraints.URL;

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

    @Size(max = 500, message = "Đường dẫn ảnh bọc sách quá dài")
    @URL(message = "Đường dẫn ảnh bìa không đúng định dạng URL")
    private String coverUrl;

    @NotNull(message = "Trạng thái xuất bản không được để trống")
    private Boolean isPublished;

    @NotBlank(message = "Thể loại chính không được để trống")
    private String genre;

    @NotBlank(message = "Thời kỳ không được để trống")
    private String period;
}