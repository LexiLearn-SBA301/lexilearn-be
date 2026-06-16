package com.sba.lexilearnbe.modules.workdetail.controller.publicapi;

import com.sba.lexilearnbe.modules.workdetail.dto.response.WorkCharacterResponse;
import com.sba.lexilearnbe.modules.workdetail.services.WorkCharacterService;
import com.sba.lexilearnbe.shared.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Work Character", description = "API đọc thông tin nhân vật")
public class WorkCharacterController {

    private final WorkCharacterService workCharacterService;

    @GetMapping("/works/{workId}/characters")
    @Operation(summary = "Lấy danh sách nhân vật của tác phẩm")
    public ResponseEntity<ApiResponse<List<WorkCharacterResponse>>> getCharacters(
            @PathVariable UUID workId
    ) {
        List<WorkCharacterResponse> result =
                workCharacterService.getCharacters(workId);

        return ResponseEntity.ok(
                ApiResponse.<List<WorkCharacterResponse>>builder()
                        .message("Lấy danh sách nhân vật thành công")
                        .result(result)
                        .build()
        );
    }
}