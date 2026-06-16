package com.sba.lexilearnbe.modules.workdetail.controller.admin;

import com.sba.lexilearnbe.modules.workdetail.dto.request.CreateWorkCharacterRequest;
import com.sba.lexilearnbe.modules.workdetail.dto.request.ReorderWorkCharactersRequest;
import com.sba.lexilearnbe.modules.workdetail.dto.request.UpdateWorkCharacterRequest;
import com.sba.lexilearnbe.modules.workdetail.dto.response.WorkCharacterResponse;
import com.sba.lexilearnbe.modules.workdetail.services.WorkCharacterService;
import com.sba.lexilearnbe.shared.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin Work Character", description = "API quản trị nhân vật")
public class AdminWorkCharacterController {

    private final WorkCharacterService workCharacterService;

    @PostMapping("/works/{workId}/characters")
    @Operation(summary = "Tạo nhân vật")
    public ResponseEntity<ApiResponse<WorkCharacterResponse>> createCharacter(
            @PathVariable UUID workId,
            @Valid @RequestBody CreateWorkCharacterRequest request
    ) {
        WorkCharacterResponse result =
                workCharacterService.createCharacter(workId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<WorkCharacterResponse>builder()
                        .message("Tạo nhân vật thành công")
                        .result(result)
                        .build()
        );
    }

    @PatchMapping("/characters/{characterId}")
    @Operation(summary = "Cập nhật nhân vật")
    public ResponseEntity<ApiResponse<WorkCharacterResponse>> updateCharacter(
            @PathVariable UUID characterId,
            @Valid @RequestBody UpdateWorkCharacterRequest request
    ) {
        WorkCharacterResponse result =
                workCharacterService.updateCharacter(characterId, request);

        return ResponseEntity.ok(
                ApiResponse.<WorkCharacterResponse>builder()
                        .message("Cập nhật nhân vật thành công")
                        .result(result)
                        .build()
        );
    }

    @DeleteMapping("/characters/{characterId}")
    @Operation(summary = "Xóa nhân vật")
    public ResponseEntity<ApiResponse<Void>> deleteCharacter(
            @PathVariable UUID characterId
    ) {
        workCharacterService.deleteCharacter(characterId);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .message("Xóa nhân vật thành công")
                        .build()
        );
    }

    @PutMapping("/works/{workId}/characters/order")
    @Operation(summary = "Sắp xếp lại thứ tự nhân vật")
    public ResponseEntity<ApiResponse<List<WorkCharacterResponse>>> reorderCharacters(
            @PathVariable UUID workId,
            @Valid @RequestBody ReorderWorkCharactersRequest request
    ) {
        List<WorkCharacterResponse> result =
                workCharacterService.reorderCharacters(workId, request);

        return ResponseEntity.ok(
                ApiResponse.<List<WorkCharacterResponse>>builder()
                        .message("Sắp xếp thứ tự nhân vật thành công")
                        .result(result)
                        .build()
        );
    }
}