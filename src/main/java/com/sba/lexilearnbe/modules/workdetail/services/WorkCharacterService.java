package com.sba.lexilearnbe.modules.workdetail.services;

import com.sba.lexilearnbe.modules.workdetail.dto.request.CreateWorkCharacterRequest;
import com.sba.lexilearnbe.modules.workdetail.dto.request.ReorderWorkCharactersRequest;
import com.sba.lexilearnbe.modules.workdetail.dto.request.UpdateWorkCharacterRequest;
import com.sba.lexilearnbe.modules.workdetail.dto.response.WorkCharacterResponse;

import java.util.List;
import java.util.UUID;

public interface WorkCharacterService {

    List<WorkCharacterResponse> getCharacters(UUID workId);

    WorkCharacterResponse createCharacter(UUID workId, CreateWorkCharacterRequest request);

    WorkCharacterResponse updateCharacter(UUID characterId, UpdateWorkCharacterRequest request);

    void deleteCharacter(UUID characterId);

    List<WorkCharacterResponse> reorderCharacters(UUID workId, ReorderWorkCharactersRequest request);
}
