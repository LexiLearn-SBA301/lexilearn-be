package com.sba.lexilearnbe.modules.workdetail.services.impl;

import com.sba.lexilearnbe.modules.work.entity.Work;
import com.sba.lexilearnbe.modules.work.repository.WorkRepository;
import com.sba.lexilearnbe.modules.workdetail.dto.request.CreateWorkCharacterRequest;
import com.sba.lexilearnbe.modules.workdetail.dto.request.UpdateWorkCharacterRequest;
import com.sba.lexilearnbe.modules.workdetail.dto.response.WorkCharacterResponse;
import com.sba.lexilearnbe.modules.workdetail.entity.WorkCharacter;
import com.sba.lexilearnbe.modules.workdetail.mapper.WorkCharacterMapper;
import com.sba.lexilearnbe.modules.workdetail.repository.WorkCharacterRepository;
import com.sba.lexilearnbe.modules.workdetail.services.WorkCharacterService;
import com.sba.lexilearnbe.modules.workdetail.util.WorkReadAccessValidator;
import com.sba.lexilearnbe.shared.common.exception.ApiException;
import com.sba.lexilearnbe.shared.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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
        WorkReadAccessValidator.requireNonNull(request, "Request");
        Work work = requireWork(workId);

        WorkCharacter character = WorkCharacter.builder()
                .work(work)
                .name(request.getName().trim())
                .description(request.getDescription())
                .analysis(request.getAnalysis())
                .displayOrder(getNextDisplayOrder(workId))
                .build();

        return workCharacterMapper.toResponse(workCharacterRepository.save(character));
    }

    @Override
    @Transactional
    public WorkCharacterResponse updateCharacter(UUID characterId, UpdateWorkCharacterRequest request) {
        WorkReadAccessValidator.requireNonNull(request, "Request");
        WorkCharacter character = requireCharacter(characterId);

        if (request.getName() != null) {
            character.setName(request.getName().trim());
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
        WorkReadAccessValidator.requireNonNull(workId, "workId");

        return workRepository.findById(workId)
                .orElseThrow(() -> new ApiException(ErrorCode.WORK_NOT_FOUND));
    }

    private Work requireReadableWork(UUID workId) {
        Work work = requireWork(workId);
        WorkReadAccessValidator.validate(work);
        return work;
    }

    private WorkCharacter requireCharacter(UUID characterId) {
        WorkReadAccessValidator.requireNonNull(characterId, "characterId");

        return workCharacterRepository.findById(characterId)
                .orElseThrow(() -> new ApiException(
                        ErrorCode.RESOURCE_NOT_FOUND,
                        "Nhân vật không tồn tại"
                ));
    }

    private int getNextDisplayOrder(UUID workId) {
        return workCharacterRepository.findMaxDisplayOrderByWorkId(workId) + 1;
    }
}
