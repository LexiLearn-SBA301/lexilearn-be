package com.sba.lexilearnbe.modules.work.services;

import com.sba.lexilearnbe.modules.work.dto.request.TagRequest;
import com.sba.lexilearnbe.modules.work.dto.response.TagResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface TagService {
    Page<TagResponse> getAllTags(String search, Pageable pageable);
    TagResponse createTag(TagRequest request);
    TagResponse updateTag(UUID id, TagRequest request);
    void deleteTag(UUID id);
}