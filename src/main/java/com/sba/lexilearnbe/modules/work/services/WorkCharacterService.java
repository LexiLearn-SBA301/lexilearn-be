package com.sba.lexilearnbe.modules.work.services;

import com.sba.lexilearnbe.modules.work.dto.request.CreateWorkCharacterRequest;
import com.sba.lexilearnbe.modules.work.dto.request.UpdateWorkCharacterRequest;
import com.sba.lexilearnbe.modules.work.dto.response.WorkCharacterResponse;

import java.util.List;
import java.util.UUID;

public interface WorkCharacterService {

    List<WorkCharacterResponse> getCharacters(UUID workId);

    WorkCharacterResponse createCharacter(UUID workId, CreateWorkCharacterRequest request);

    WorkCharacterResponse updateCharacter(UUID workId, UUID characterId, UpdateWorkCharacterRequest request);

    void deleteCharacter(UUID workId, UUID characterId);
}
