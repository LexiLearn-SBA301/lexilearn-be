package com.sba.lexilearnbe.modules.work.services.impl;

import com.sba.lexilearnbe.modules.work.dto.request.TagRequest;
import com.sba.lexilearnbe.modules.work.dto.response.TagResponse;
import com.sba.lexilearnbe.modules.work.entity.Tag;
import com.sba.lexilearnbe.modules.work.mapper.TagMapper;
import com.sba.lexilearnbe.modules.work.repository.TagRepository;
import com.sba.lexilearnbe.modules.work.services.TagService;
import com.sba.lexilearnbe.modules.work.utils.SlugUtils;
import com.sba.lexilearnbe.shared.common.exception.ApiException;
import com.sba.lexilearnbe.shared.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;
    private final TagMapper tagMapper;

    @Override
    public Page<TagResponse> getAllTags(String search, Pageable pageable) {
        Page<Tag> tagPage;
        String validSearch = (search != null && !search.trim().isEmpty()) ? search.trim() : null;

        if (validSearch != null) {
            tagPage = tagRepository.findByNameContainingIgnoreCase(validSearch, pageable);
        } else {
            tagPage = tagRepository.findAll(pageable);
        }
        return tagPage.map(tagMapper::toResponse);
    }
    @Override
    @Transactional // Đã khóa chặt an toàn
    public TagResponse createTag(TagRequest request) {
        if (tagRepository.existsByNameIgnoreCase(request.getName().trim())) {
            throw new ApiException(ErrorCode.TAG_ALREADY_EXISTS); // Bác nhớ định nghĩa ErrorCode này nhé
        }

        Tag tag = tagMapper.toEntity(request);
        tag.setSlug(SlugUtils.generateSlug(request.getName())); // Sinh slug tự động như lúc làm Work

        return tagMapper.toResponse(tagRepository.save(tag));
    }

    @Override
    @Transactional // Đã khóa chặt an toàn
    public TagResponse updateTag(UUID id, TagRequest request) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.TAG_NOT_FOUND));

        if (tagRepository.existsByNameIgnoreCaseAndIdNot(request.getName().trim(), id)) {
            throw new ApiException(ErrorCode.TAG_ALREADY_EXISTS);
        }

        tagMapper.updateEntityFromRequest(request, tag);
        tag.setSlug(SlugUtils.generateSlug(request.getName()));

        return tagMapper.toResponse(tagRepository.save(tag));
    }

    @Override
    @Transactional
    public void deleteTag(UUID id) {
        if (!tagRepository.existsById(id)) {
            throw new ApiException(ErrorCode.TAG_NOT_FOUND);
        }
        tagRepository.deleteTagFromAllWorks(id);
        tagRepository.deleteById(id);
    }
}