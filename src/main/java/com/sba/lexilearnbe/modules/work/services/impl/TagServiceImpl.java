package com.sba.lexilearnbe.modules.work.services.impl;

import com.sba.lexilearnbe.modules.work.dto.response.TagResponse;
import com.sba.lexilearnbe.modules.work.repository.TagRepository;
import com.sba.lexilearnbe.modules.work.services.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;

    @Override
    public List<TagResponse> getAllTags() {
        return tagRepository.findAllByOrderByNameAsc().stream()
                .map(tag -> TagResponse.builder()
                        .id(tag.getId())
                        .name(tag.getName())
                        .slug(tag.getSlug())
                        .build())
                .collect(Collectors.toList());
    }
}