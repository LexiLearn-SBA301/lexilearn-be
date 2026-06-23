package com.sba.lexilearnbe.modules.work.services.impl;

import com.sba.lexilearnbe.modules.work.entity.Work;
import com.sba.lexilearnbe.modules.work.repository.WorkRepository;
import com.sba.lexilearnbe.modules.work.dto.request.CreateWorkCharacterRequest;
import com.sba.lexilearnbe.modules.work.dto.request.UpdateWorkCharacterRequest;
import com.sba.lexilearnbe.modules.work.dto.response.WorkCharacterResponse;
import com.sba.lexilearnbe.modules.work.entity.WorkCharacter;
import com.sba.lexilearnbe.modules.work.mapper.WorkCharacterMapper;
import com.sba.lexilearnbe.modules.work.repository.WorkCharacterRepository;
import com.sba.lexilearnbe.modules.work.services.WorkCharacterService;
import com.sba.lexilearnbe.modules.work.utils.WorkReadAccessValidator;
import com.sba.lexilearnbe.shared.common.exception.ApiException;
import com.sba.lexilearnbe.shared.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WorkCharacterServiceImpl implements WorkCharacterService {

    private final WorkRepository workRepository;
    private final WorkCharacterRepository workCharacterRepository;
    private final WorkCharacterMapper workCharacterMapper;

    @Override
    @Transactional(readOnly = true)
    public List<WorkCharacterResponse> getCharacters(UUID workId) {
        requireReadableWork(workId);

        return workCharacterRepository.findAllByWork_IdOrderByDisplayOrderAsc(workId)
                .stream()
                .map(workCharacterMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public WorkCharacterResponse createCharacter(UUID workId, CreateWorkCharacterRequest request) {
        Work work = requireWork(workId);

        WorkCharacter character = WorkCharacter.builder()
                .work(work)
                .name(request.getName().trim())
                .roleType(request.getRoleType())
                .description(request.getDescription())
                .analysis(request.getAnalysis())
                .displayOrder(getNextDisplayOrder(workId))
                .build();

        try {
            return workCharacterMapper.toResponse(workCharacterRepository.saveAndFlush(character));
        } catch (DataIntegrityViolationException exception) {
            throw new ApiException(
                    ErrorCode.VALIDATION_ERROR,
                    "Thứ tự nhân vật đã tồn tại trong tác phẩm"
            );
        }
    }

    @Override
    @Transactional
    public WorkCharacterResponse updateCharacter(UUID characterId, UpdateWorkCharacterRequest request) {
        WorkCharacter character = requireCharacter(characterId);

        if (request.getName() != null) {
            character.setName(request.getName().trim());
        }
        if (request.getRoleType() != null) {
            character.setRoleType(request.getRoleType());
        }
        if (request.getDescription() != null) {
            character.setDescription(request.getDescription());
        }
        if (request.getAnalysis() != null) {
            character.setAnalysis(request.getAnalysis());
        }

        return workCharacterMapper.toResponse(workCharacterRepository.save(character));
    }

    @Override
    @Transactional
    public void deleteCharacter(UUID characterId) {
        workCharacterRepository.delete(requireCharacter(characterId));
    }

    private Work requireWork(UUID workId) {
        Objects.requireNonNull(workId, "workId không được để trống");

        return workRepository.findById(workId)
                .orElseThrow(() -> new ApiException(ErrorCode.WORK_NOT_FOUND));
    }

    private Work requireReadableWork(UUID workId) {
        Work work = requireWork(workId);
        WorkReadAccessValidator.validate(work);
        return work;
    }

    private WorkCharacter requireCharacter(UUID characterId) {
        Objects.requireNonNull(characterId, "characterId không được để trống");

        return workCharacterRepository.findByIdWithWork(characterId)
                .orElseThrow(() -> new ApiException(
                        ErrorCode.RESOURCE_NOT_FOUND,
                        "Nhân vật không tồn tại"
                ));
    }

    private int getNextDisplayOrder(UUID workId) {
        return workCharacterRepository.findMaxDisplayOrderByWorkId(workId) + 1;
    }
}
