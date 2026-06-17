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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        WorkCharacterResponse result = workCharacterService.createCharacter(workId, request);

        ApiResponse<WorkCharacterResponse> response =
                ApiResponse.<WorkCharacterResponse>builder()
                        .message("Tạo nhân vật thành công")
                        .result(result)
                        .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/characters/{characterId}")
    @Operation(summary = "Cập nhật nhân vật")
    public ResponseEntity<ApiResponse<WorkCharacterResponse>> updateCharacter(
            @PathVariable UUID characterId,
            @Valid @RequestBody UpdateWorkCharacterRequest request
    ) {
        WorkCharacterResponse result = workCharacterService.updateCharacter(characterId, request);

        ApiResponse<WorkCharacterResponse> response =
                ApiResponse.<WorkCharacterResponse>builder()
                        .message("Cập nhật nhân vật thành công")
                        .result(result)
                        .build();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/characters/{characterId}")
    @Operation(summary = "Xóa nhân vật")
    public ResponseEntity<Void> deleteCharacter(
            @PathVariable UUID characterId
    ) {
        workCharacterService.deleteCharacter(characterId);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/works/{workId}/characters/sequence")
    @Operation(summary = "Sắp xếp lại thứ tự nhân vật")
    public ResponseEntity<ApiResponse<List<WorkCharacterResponse>>> reorderCharacters(
            @PathVariable UUID workId,
            @Valid @RequestBody ReorderWorkCharactersRequest request
    ) {
        List<WorkCharacterResponse> result = workCharacterService.reorderCharacters(workId, request);

        ApiResponse<List<WorkCharacterResponse>> response =
                ApiResponse.<List<WorkCharacterResponse>>builder()
                        .message("Sắp xếp thứ tự nhân vật thành công")
                        .result(result)
                        .build();

        return ResponseEntity.ok(response);
    }
}
